package com.fajar.cineverse.movie_discovery.presentation.pagination

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.fajar.cineverse.core.common.DispatcherProvider
import com.fajar.cineverse.core.domain.MovieRepository
import com.fajar.cineverse.core.domain.model.MovieGenre
import com.fajar.cineverse.core.domain.model.MoviePreviewsItemData
import com.fajar.cineverse.core.domain.model.Result
import com.fajar.cineverse.core.ui.mapper.DefaultErrorMapper
import com.fajar.cineverse.core.ui.model.ExceptionWithUIText
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext

internal class MoviePreviewsPagingSource(
    private val genre: MovieGenre,
    private val repository: MovieRepository,
    private val defaultErrorMapper: DefaultErrorMapper,
    private val dispatcherProvider: DispatcherProvider,
) : PagingSource<Int, MoviePreviewsItemData>() {

    override fun getRefreshKey(state: PagingState<Int, MoviePreviewsItemData>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.let { page ->
                page.prevKey?.plus(page.data.size)
                    ?: page.nextKey?.minus(page.data.size)
            }
        }
    }

    private val loadedMovieIds = hashSetOf<Long>()
    private val loadedMovieIdsLock = Any()

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, MoviePreviewsItemData> {
        val page = params.key ?: START_PAGINATION_KEY

        return withContext(dispatcherProvider.default) {
            try {
                val result = repository.getListMoviePreviewsByGenre(
                    genre = genre,
                    page = page
                )

                currentCoroutineContext().ensureActive()

                when (result) {
                    is Result.Error -> {
                        val errorUIText = defaultErrorMapper.getErrorStringResource(
                            result
                        )
                        LoadResult.Error(ExceptionWithUIText(errorUIText))
                    }

                    is Result.Success -> {
                        val currentPageItems = result.data
                            .pagedList

                        val isEndOfPagination =
                            currentPageItems.isEmpty() || !result.data.hasNext

                        // Remove duplicate movies across pages
                        val uniqueItems = synchronized(loadedMovieIdsLock) {
                            currentPageItems.filter { movie ->
                                loadedMovieIds.add(movie.id)
                            }
                        }

                        val prevKey = if (page == START_PAGINATION_KEY) {
                            null
                        } else page.minus(1)

                        val nextKey = if (isEndOfPagination) {
                            null
                        } else page.plus(1)

                        LoadResult.Page(
                            data = uniqueItems,
                            prevKey = prevKey,
                            nextKey = nextKey
                        )
                    }
                }
            } catch (e: Exception) {
                currentCoroutineContext().ensureActive()

                LoadResult.Error(e)
            }
        }
    }

    companion object {
        private const val START_PAGINATION_KEY = 1
    }
}
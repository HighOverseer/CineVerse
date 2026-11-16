package com.fajar.cineverse.movie_detail.presentation.pagination

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.fajar.cineverse.core.common.DispatcherProvider
import com.fajar.cineverse.core.domain.MovieRepository
import com.fajar.cineverse.core.domain.model.Result
import com.fajar.cineverse.core.ui.mapper.DefaultErrorMapper
import com.fajar.cineverse.core.ui.model.ExceptionWithUIText
import com.fajar.cineverse.movie_detail.presentation.mapper.DUIMapper
import com.fajar.cineverse.movie_detail.presentation.model.MovieReviewDUI
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext

internal class MovieReviewsPagingSource(
    private val selectedMovieId: Long,
    private val repository: MovieRepository,
    private val duiMapper: DUIMapper,
    private val defaultErrorMapper: DefaultErrorMapper,
    private val dispatcherProvider: DispatcherProvider,
) : PagingSource<Int, MovieReviewDUI>() {

    override fun getRefreshKey(state: PagingState<Int, MovieReviewDUI>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.let { page ->
                page.prevKey?.plus(1)
                    ?: page.nextKey?.minus(1)
            }
        }
    }

    private val loadedReviewIds = hashSetOf<String>()
    private val loadedReviewIdsLock = Any()

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, MovieReviewDUI> {
        val page = params.key ?: START_PAGINATION_KEY

        return withContext(dispatcherProvider.default) {
            try {
                val result = repository.getListMovieReviews(
                    selectedMovieId = selectedMovieId,
                    page = page,
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
                        val uniqueItems = synchronized(loadedReviewIdsLock) {
                            currentPageItems.filter { review ->
                                loadedReviewIds.add(review.id)
                            }
                        }.map(duiMapper::mapReviewToDUI)

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
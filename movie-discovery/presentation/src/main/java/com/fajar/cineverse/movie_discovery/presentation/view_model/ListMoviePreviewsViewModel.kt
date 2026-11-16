package com.fajar.cineverse.movie_discovery.presentation.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.fajar.cineverse.core.common.DispatcherProvider
import com.fajar.cineverse.core.domain.MovieRepository
import com.fajar.cineverse.core.domain.model.MovieGenre
import com.fajar.cineverse.core.ui.mapper.DefaultErrorMapper
import com.fajar.cineverse.movie_discovery.presentation.pagination.MoviePreviewsPagingSource
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.flowOn

@HiltViewModel(assistedFactory = ListMoviePreviewsViewModel.Factory::class)
internal class ListMoviePreviewsViewModel @AssistedInject constructor(
    private val repository: MovieRepository,
    private val dispatcherProvider: DispatcherProvider,
    private val defaultErrorMapper: DefaultErrorMapper,
    @Assisted private val selectedGenre: MovieGenre
) : ViewModel() {

    val selectedGenreName: String
        get() = selectedGenre.name

    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    val pagingItemsOfMoviePreviews = Pager(
        config = PagingConfig(
            pageSize = PAGE_SIZE,
            initialLoadSize = INITIAL_LOAD_SIZE,
            prefetchDistance = PREFETCH_DISTANCE,
            enablePlaceholders = false
        ),
        pagingSourceFactory = {
            MoviePreviewsPagingSource(
                genre = selectedGenre,
                repository = repository,
                defaultErrorMapper = defaultErrorMapper,
                dispatcherProvider = dispatcherProvider
            )
        }
    ).flow
        .flowOn(dispatcherProvider.default)
        .cachedIn(viewModelScope)

    companion object {
        private const val PAGE_SIZE = 10
        private const val INITIAL_LOAD_SIZE = 20
        private const val PREFETCH_DISTANCE = 4
    }

    @AssistedFactory
    interface Factory {
        fun create(selectedGenre: MovieGenre): ListMoviePreviewsViewModel
    }
}
package com.fajar.cineverse.movie_detail.presentation

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.fajar.cineverse.core.common.DispatcherProvider
import com.fajar.cineverse.core.domain.MovieRepository
import com.fajar.cineverse.core.domain.model.MovieDetailData
import com.fajar.cineverse.core.domain.model.fold
import com.fajar.cineverse.core.ui.mapper.DefaultErrorMapper
import com.fajar.cineverse.core.ui.model.UIText
import com.fajar.cineverse.movie_detail.presentation.mapper.DUIMapper
import com.fajar.cineverse.movie_detail.presentation.pagination.MovieReviewsPagingSource
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@Immutable
internal data class MovieDetailUIState(
    val isLoading: Boolean = false,
    val movieDetail: MovieDetailData? = null,
    val errorUserMessage: UIText? = null,
    val isFavorite: Boolean = false
)

@HiltViewModel(assistedFactory = MovieDetailViewModel.Factory::class)
internal class MovieDetailViewModel @AssistedInject constructor(
    private val repository: MovieRepository,
    private val duiMapper: DUIMapper,
    private val dispatcherProvider: DispatcherProvider,
    private val defaultErrorMapper: DefaultErrorMapper,
    @Assisted private val selectedMovieId: Long
) : ViewModel() {

    private val _uiState = MutableStateFlow(MovieDetailUIState())
    val uiState = _uiState.asStateFlow()

    @Volatile
    private var hasBeenInitialized = false
    fun initializeIfNeeded() {
        if (hasBeenInitialized) return

        hasBeenInitialized = true
        loadMovieDetail()
    }

    private var loadMovieDetailJob: Job? = null
    private fun loadMovieDetail() {
        if (loadMovieDetailJob?.isActive == true) return

        loadMovieDetailJob = viewModelScope.launch(dispatcherProvider.default) {
            _uiState.update { it.copy(isLoading = true) }

            repository.getMovieDetail(selectedMovieId)
                .fold(
                    onSuccess = { movieDetail ->
                        _uiState.update {
                            it.copy(
                                movieDetail = movieDetail,
                                isLoading = false
                            )
                        }
                    },
                    onError = { error ->
                        val errorUserMessage = defaultErrorMapper
                            .getErrorStringResource(error)
                        _uiState.update {
                            it.copy(
                                errorUserMessage = errorUserMessage,
                                isLoading = false
                            )
                        }
                    }
                )
        }
    }

    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    val pagingItemsOfMovieReviews by lazy {
        Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                initialLoadSize = INITIAL_LOAD_SIZE,
                prefetchDistance = PREFETCH_DISTANCE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                MovieReviewsPagingSource(
                    selectedMovieId = selectedMovieId,
                    duiMapper = duiMapper,
                    repository = repository,
                    defaultErrorMapper = defaultErrorMapper,
                    dispatcherProvider = dispatcherProvider
                )
            }
        ).flow
            .flowOn(dispatcherProvider.default)
            .cachedIn(viewModelScope)
    }

    companion object {
        private const val PAGE_SIZE = 5
        private const val INITIAL_LOAD_SIZE = 5
        private const val PREFETCH_DISTANCE = 1
    }

    @AssistedFactory
    interface Factory {
        fun create(selectedMovieId: Long): MovieDetailViewModel
    }
}
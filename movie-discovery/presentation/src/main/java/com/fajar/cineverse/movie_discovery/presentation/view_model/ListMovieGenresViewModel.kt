package com.fajar.cineverse.movie_discovery.presentation.view_model

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fajar.cineverse.core.common.DispatcherProvider
import com.fajar.cineverse.core.domain.MovieRepository
import com.fajar.cineverse.core.domain.model.MovieGenre
import com.fajar.cineverse.core.domain.model.fold
import com.fajar.cineverse.core.ui.mapper.DefaultErrorMapper
import com.fajar.cineverse.core.ui.model.UIText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

@Immutable
internal data class ListMovieGenresUIState(
    val isLoading: Boolean = false,
    val listOfMovieGenres: ImmutableList<MovieGenre> = persistentListOf(),
    val errorUserMessage: UIText? = null,
)

@HiltViewModel
internal class ListMovieGenresViewModel @Inject constructor(
    private val repository: MovieRepository,
    private val dispatcherProvider: DispatcherProvider,
    private val defaultErrorMapper: DefaultErrorMapper,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ListMovieGenresUIState())
    val uiState = _uiState.asStateFlow()

    @Volatile
    private var hasBeenInitialized = false
    fun initializeIfNeeded() {
        if (hasBeenInitialized) return

        hasBeenInitialized = true
        loadListMovieGenres()
    }

    private var loadListMovieGenresJob: Job? = null
    private fun loadListMovieGenres() {
        if (loadListMovieGenresJob?.isActive == true) return

        loadListMovieGenresJob = viewModelScope.launch(dispatcherProvider.default) {
            _uiState.update { it.copy(isLoading = true) }

            repository.getAllMovieGenres()
                .fold(
                    onSuccess = { listOfMovieGenres ->
                        val immutableOfListMovieGenres = listOfMovieGenres.toImmutableList()

                        _uiState.update {
                            it.copy(
                                listOfMovieGenres = immutableOfListMovieGenres,
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

    fun onErrorMessageHandled() {
        viewModelScope.launch(dispatcherProvider.default) {
            _uiState.update { it.copy(errorUserMessage = null) }

            delay(RETRY_RELOAD_DELAY)
            loadListMovieGenres()
        }
    }

    companion object {
        private val RETRY_RELOAD_DELAY = 2.seconds
    }
}
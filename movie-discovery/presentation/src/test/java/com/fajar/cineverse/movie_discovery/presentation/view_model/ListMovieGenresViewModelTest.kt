package com.fajar.cineverse.movie_discovery.presentation.view_model

import com.fajar.cineverse.core.domain.MovieRepository
import com.fajar.cineverse.core.domain.model.Result
import com.fajar.cineverse.core.domain.model.RootNetworkError
import com.fajar.cineverse.core.test_utils.TestDispatcherProvider
import com.fajar.cineverse.core.test_utils.TestDummyFactory
import com.fajar.cineverse.core.ui.R
import com.fajar.cineverse.core.ui.mapper.DefaultErrorMapper
import com.fajar.cineverse.core.ui.model.UIText
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ListMovieGenresViewModelTest {

    private lateinit var repository: MovieRepository
    private lateinit var defaultErrorMapper: DefaultErrorMapper
    private lateinit var testScheduler: TestCoroutineScheduler
    private lateinit var dispatcherProvider: TestDispatcherProvider
    private lateinit var viewModel: ListMovieGenresViewModel

    @Before
    fun setUp() {
        testScheduler = TestCoroutineScheduler()
        dispatcherProvider = TestDispatcherProvider(testScheduler)
        Dispatchers.setMain(UnconfinedTestDispatcher(testScheduler))

        repository = mockk()
        defaultErrorMapper = DefaultErrorMapper()
        viewModel = ListMovieGenresViewModel(
            repository = repository,
            dispatcherProvider = dispatcherProvider,
            defaultErrorMapper = defaultErrorMapper
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `given repository succeeds, when initializeIfNeeded, then updates uiState with loaded genres and isLoading false`() = runTest(testScheduler) {
        val dummyGenres = TestDummyFactory.createDummyMovieGenres()
        coEvery { repository.getAllMovieGenres() } returns Result.Success(dummyGenres)

        val expectedUIState = ListMovieGenresUIState(
            isLoading = false,
            listOfMovieGenres = dummyGenres.toImmutableList(),
            errorUserMessage = null
        )

        viewModel.initializeIfNeeded()
        testScheduler.advanceUntilIdle()

        val actualUIState = viewModel.uiState.value
        assertEquals(expectedUIState, actualUIState)
        coVerify(exactly = 1) { repository.getAllMovieGenres() }
    }

    @Test
    fun `given repository fails, when initializeIfNeeded, then updates uiState with errorUserMessage and isLoading false`() = runTest(testScheduler) {
        val networkError = RootNetworkError.INTERNAL_SERVER_ERROR
        coEvery { repository.getAllMovieGenres() } returns Result.Error(networkError)

        val expectedErrorText = UIText.Static(R.string.an_error_occurred_please_try_again_later)
        val expectedUIState = ListMovieGenresUIState(
            isLoading = false,
            listOfMovieGenres = kotlinx.collections.immutable.persistentListOf(),
            errorUserMessage = expectedErrorText
        )

        viewModel.initializeIfNeeded()
        testScheduler.advanceUntilIdle()

        val actualUIState = viewModel.uiState.value
        assertEquals(expectedUIState, actualUIState)
        coVerify(exactly = 1) { repository.getAllMovieGenres() }
    }

    @Test
    fun `given viewmodel already initialized, when initializeIfNeeded called again, then does not load genres again`() = runTest(testScheduler) {
        val dummyGenres = TestDummyFactory.createDummyMovieGenres()
        coEvery { repository.getAllMovieGenres() } returns Result.Success(dummyGenres)

        viewModel.initializeIfNeeded()
        testScheduler.advanceUntilIdle()

        viewModel.initializeIfNeeded()
        testScheduler.advanceUntilIdle()

        coVerify(exactly = 1) { repository.getAllMovieGenres() }
    }

    @Test
    fun `given error state, when onErrorMessageHandled, then clears error message and reloads genres after delay`() = runTest(testScheduler) {
        val networkError = RootNetworkError.INTERNAL_SERVER_ERROR
        val dummyGenres = TestDummyFactory.createDummyMovieGenres()
        coEvery { repository.getAllMovieGenres() } returnsMany listOf(
            Result.Error(networkError),
            Result.Success(dummyGenres)
        )

        viewModel.initializeIfNeeded()
        testScheduler.advanceUntilIdle()

        val expectedStateAfterReload = ListMovieGenresUIState(
            isLoading = false,
            listOfMovieGenres = dummyGenres.toImmutableList(),
            errorUserMessage = null
        )

        viewModel.onErrorMessageHandled()
        testScheduler.advanceUntilIdle()

        val actualUIState = viewModel.uiState.value
        assertEquals(expectedStateAfterReload, actualUIState)
        coVerify(exactly = 2) { repository.getAllMovieGenres() }
    }
}

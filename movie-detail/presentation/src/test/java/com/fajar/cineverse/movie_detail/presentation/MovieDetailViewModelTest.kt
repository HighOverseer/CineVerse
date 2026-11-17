package com.fajar.cineverse.movie_detail.presentation

import androidx.paging.PagingSource
import com.fajar.cineverse.core.domain.MovieRepository
import com.fajar.cineverse.core.domain.model.Result
import com.fajar.cineverse.core.domain.model.RootNetworkError
import com.fajar.cineverse.core.test_utils.TestDispatcherProvider
import com.fajar.cineverse.core.test_utils.TestDummyFactory
import com.fajar.cineverse.core.ui.R
import com.fajar.cineverse.core.ui.mapper.DefaultErrorMapper
import com.fajar.cineverse.core.ui.model.UIText
import com.fajar.cineverse.core.ui.utils.formatter.DefaultDUIDateFormatter
import com.fajar.cineverse.movie_detail.presentation.mapper.DUIMapper
import com.fajar.cineverse.movie_detail.presentation.pagination.MovieReviewsPagingSource
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MovieDetailViewModelTest {

    private lateinit var repository: MovieRepository
    private lateinit var duiMapper: DUIMapper
    private lateinit var defaultErrorMapper: DefaultErrorMapper
    private lateinit var testScheduler: TestCoroutineScheduler
    private lateinit var dispatcherProvider: TestDispatcherProvider
    private lateinit var viewModel: MovieDetailViewModel

    private val selectedMovieId = 101L

    @Before
    fun setUp() {
        testScheduler = TestCoroutineScheduler()
        dispatcherProvider = TestDispatcherProvider(testScheduler)
        Dispatchers.setMain(UnconfinedTestDispatcher(testScheduler))

        repository = mockk()
        duiMapper = DUIMapper(DefaultDUIDateFormatter())
        defaultErrorMapper = DefaultErrorMapper()

        viewModel = MovieDetailViewModel(
            repository = repository,
            duiMapper = duiMapper,
            dispatcherProvider = dispatcherProvider,
            defaultErrorMapper = defaultErrorMapper,
            selectedMovieId = selectedMovieId
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `given repository succeeds, when initializeIfNeeded, then updates uiState with movie detail and isLoading false`() = runTest(testScheduler) {
        val dummyDetail = TestDummyFactory.createDummyMovieDetailData(id = selectedMovieId)
        coEvery { repository.getMovieDetail(selectedMovieId = selectedMovieId) } returns Result.Success(dummyDetail)

        val expectedUIState = MovieDetailUIState(
            isLoading = false,
            movieDetail = dummyDetail,
            errorUserMessage = null,
            isFavorite = false
        )

        viewModel.initializeIfNeeded()
        testScheduler.advanceUntilIdle()

        val actualUIState = viewModel.uiState.value
        assertEquals(expectedUIState, actualUIState)
        coVerify(exactly = 1) { repository.getMovieDetail(selectedMovieId = selectedMovieId) }
    }

    @Test
    fun `given repository returns error, when initializeIfNeeded, then updates uiState with errorUserMessage and isLoading false`() = runTest(testScheduler) {
        val networkError = RootNetworkError.NOT_FOUND
        coEvery { repository.getMovieDetail(selectedMovieId = selectedMovieId) } returns Result.Error(networkError)

        val expectedErrorText = UIText.Static(R.string.an_error_occurred_please_try_again)
        val expectedUIState = MovieDetailUIState(
            isLoading = false,
            movieDetail = null,
            errorUserMessage = expectedErrorText,
            isFavorite = false
        )

        viewModel.initializeIfNeeded()
        testScheduler.advanceUntilIdle()

        val actualUIState = viewModel.uiState.value
        assertEquals(expectedUIState, actualUIState)
        coVerify(exactly = 1) { repository.getMovieDetail(selectedMovieId = selectedMovieId) }
    }

    @Test
    fun `given viewmodel already initialized, when initializeIfNeeded called again, then does not fetch movie detail again`() = runTest(testScheduler) {
        val dummyDetail = TestDummyFactory.createDummyMovieDetailData(id = selectedMovieId)
        coEvery { repository.getMovieDetail(selectedMovieId = selectedMovieId) } returns Result.Success(dummyDetail)

        viewModel.initializeIfNeeded()
        testScheduler.advanceUntilIdle()

        viewModel.initializeIfNeeded()
        testScheduler.advanceUntilIdle()

        coVerify(exactly = 1) { repository.getMovieDetail(selectedMovieId = selectedMovieId) }
    }

    @Test
    fun `given repository returns reviews, when collecting pagingItemsOfMovieReviews, then emits paging data`() = runTest(testScheduler) {
        val dummyReviews = TestDummyFactory.createDummyMovieReviewsList()
        val dummyPage = TestDummyFactory.createDummyPage(pagedList = dummyReviews, hasNext = true)
        coEvery { repository.getListMovieReviews(selectedMovieId = selectedMovieId, page = 1) } returns Result.Success(dummyPage)

        val pagingDataFlow = viewModel.pagingItemsOfMovieReviews
        val pagingData = pagingDataFlow.first()

        assertNotNull(pagingData)
    }

    @Test
    fun `given repository returns error, when loading MovieReviewsPagingSource, then returns LoadResult Error`() = runTest(testScheduler) {
        val pagingSource = MovieReviewsPagingSource(
            selectedMovieId = selectedMovieId,
            repository = repository,
            duiMapper = duiMapper,
            defaultErrorMapper = defaultErrorMapper,
            dispatcherProvider = dispatcherProvider
        )
        val expectedError = RootNetworkError.INTERNAL_SERVER_ERROR
        coEvery { repository.getListMovieReviews(selectedMovieId = selectedMovieId, page = 1) } returns Result.Error(expectedError)

        val loadResult = pagingSource.load(
            PagingSource.LoadParams.Refresh(
                key = null,
                loadSize = 5,
                placeholdersEnabled = false
            )
        )

        assertTrue(loadResult is PagingSource.LoadResult.Error)
        coVerify(exactly = 1) { repository.getListMovieReviews(selectedMovieId = selectedMovieId, page = 1) }
    }
}

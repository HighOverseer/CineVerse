package com.fajar.cineverse.movie_discovery.presentation.view_model

import androidx.paging.PagingSource
import com.fajar.cineverse.core.domain.MovieRepository
import com.fajar.cineverse.core.domain.model.Result
import com.fajar.cineverse.core.domain.model.RootNetworkError
import com.fajar.cineverse.core.test_utils.TestDispatcherProvider
import com.fajar.cineverse.core.test_utils.TestDummyFactory
import com.fajar.cineverse.core.ui.mapper.DefaultErrorMapper
import com.fajar.cineverse.movie_discovery.presentation.pagination.MoviePreviewsPagingSource
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
class ListMoviePreviewsViewModelTest {

    private lateinit var repository: MovieRepository
    private lateinit var defaultErrorMapper: DefaultErrorMapper
    private lateinit var testScheduler: TestCoroutineScheduler
    private lateinit var dispatcherProvider: TestDispatcherProvider
    private lateinit var viewModel: ListMoviePreviewsViewModel
    private val selectedGenre = TestDummyFactory.createDummyMovieGenre(id = 28L, name = "Action")

    @Before
    fun setUp() {
        testScheduler = TestCoroutineScheduler()
        dispatcherProvider = TestDispatcherProvider(testScheduler)
        Dispatchers.setMain(UnconfinedTestDispatcher(testScheduler))

        repository = mockk()
        defaultErrorMapper = DefaultErrorMapper()

        viewModel = ListMoviePreviewsViewModel(
            repository = repository,
            dispatcherProvider = dispatcherProvider,
            defaultErrorMapper = defaultErrorMapper,
            selectedGenre = selectedGenre
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `given selected genre, when reading selectedGenreName, then returns correct genre name`() {
        val expectedGenreName = "Action"

        val actualGenreName = viewModel.selectedGenreName

        assertEquals(expectedGenreName, actualGenreName)
    }

    @Test
    fun `given repository returns previews, when collecting pagingItemsOfMoviePreviews, then emits paging data`() = runTest(testScheduler) {
        val dummyPreviews = TestDummyFactory.createDummyMoviePreviewsList()
        val dummyPage = TestDummyFactory.createDummyPage(pagedList = dummyPreviews, hasNext = true)
        coEvery { repository.getListMoviePreviewsByGenre(genre = selectedGenre, page = 1) } returns Result.Success(dummyPage)

        val pagingDataFlow = viewModel.pagingItemsOfMoviePreviews
        val pagingData = pagingDataFlow.first()

        assertNotNull(pagingData)
    }

    @Test
    fun `given repository returns error, when loading MoviePreviewsPagingSource, then returns LoadResult Error`() = runTest(testScheduler) {
        val pagingSource = MoviePreviewsPagingSource(
            genre = selectedGenre,
            repository = repository,
            defaultErrorMapper = defaultErrorMapper,
            dispatcherProvider = dispatcherProvider
        )
        val expectedError = RootNetworkError.INTERNAL_SERVER_ERROR
        coEvery { repository.getListMoviePreviewsByGenre(genre = selectedGenre, page = 1) } returns Result.Error(expectedError)

        val loadResult = pagingSource.load(
            PagingSource.LoadParams.Refresh(
                key = null,
                loadSize = 20,
                placeholdersEnabled = false
            )
        )

        assertTrue(loadResult is PagingSource.LoadResult.Error)
        coVerify(exactly = 1) { repository.getListMoviePreviewsByGenre(genre = selectedGenre, page = 1) }
    }
}

package com.fajar.cineverse.core.data

import com.fajar.cineverse.core.data.datasource.remote.MovieRemoteDataSource
import com.fajar.cineverse.core.domain.MovieRepository
import com.fajar.cineverse.core.domain.model.DataError
import com.fajar.cineverse.core.domain.model.MovieDetailData
import com.fajar.cineverse.core.domain.model.MovieGenre
import com.fajar.cineverse.core.domain.model.MoviePreviewsItemData
import com.fajar.cineverse.core.domain.model.MovieReviewData
import com.fajar.cineverse.core.domain.model.Page
import com.fajar.cineverse.core.domain.model.Result
import com.fajar.cineverse.core.domain.model.RootNetworkError
import com.fajar.cineverse.core.test_utils.TestDummyFactory
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class MovieRepositoryImplTest {

    private lateinit var remoteDataSource: MovieRemoteDataSource
    private lateinit var repository: MovieRepository

    @Before
    fun setUp() {
        remoteDataSource = mockk()
        repository = MovieRepositoryImpl(remoteDataSource = remoteDataSource)
    }

    @Test
    fun `given remote datasource succeeds, when getAllMovieGenres, then returns success with list of genres`() = runTest {
        val dummyGenres = TestDummyFactory.createDummyMovieGenres()
        val expectedResult: Result<List<MovieGenre>, DataError> = Result.Success(dummyGenres)
        coEvery { remoteDataSource.getAllMovieGenres() } returns expectedResult

        val actualResult = repository.getAllMovieGenres()

        assertEquals(expectedResult, actualResult)
        coVerify(exactly = 1) { remoteDataSource.getAllMovieGenres() }
    }

    @Test
    fun `given remote datasource returns error, when getAllMovieGenres, then returns error`() = runTest {
        val expectedError = RootNetworkError.INTERNAL_SERVER_ERROR
        val expectedResult: Result<List<MovieGenre>, DataError> = Result.Error(expectedError)
        coEvery { remoteDataSource.getAllMovieGenres() } returns expectedResult

        val actualResult = repository.getAllMovieGenres()

        assertEquals(expectedResult, actualResult)
        coVerify(exactly = 1) { remoteDataSource.getAllMovieGenres() }
    }

    @Test
    fun `given remote datasource succeeds, when getListMoviePreviewsByGenre, then returns success with paged previews`() = runTest {
        val dummyGenre = TestDummyFactory.createDummyMovieGenre()
        val dummyPreviews = TestDummyFactory.createDummyMoviePreviewsList()
        val dummyPage = TestDummyFactory.createDummyPage(pagedList = dummyPreviews, hasNext = true)
        val expectedResult: Result<Page<MoviePreviewsItemData>, DataError> = Result.Success(dummyPage)
        val page = 1

        coEvery { remoteDataSource.getListMoviePreviewsByGenre(genre = dummyGenre, page = page) } returns expectedResult

        val actualResult = repository.getListMoviePreviewsByGenre(genre = dummyGenre, page = page)

        assertEquals(expectedResult, actualResult)
        coVerify(exactly = 1) { remoteDataSource.getListMoviePreviewsByGenre(genre = dummyGenre, page = page) }
    }

    @Test
    fun `given remote datasource returns error, when getListMoviePreviewsByGenre, then returns error`() = runTest {
        val dummyGenre = TestDummyFactory.createDummyMovieGenre()
        val expectedError = RootNetworkError.NO_CONNECTIVITY_OR_SERVER_UNREACHABLE
        val expectedResult: Result<Page<MoviePreviewsItemData>, DataError> = Result.Error(expectedError)
        val page = 1

        coEvery { remoteDataSource.getListMoviePreviewsByGenre(genre = dummyGenre, page = page) } returns expectedResult

        val actualResult = repository.getListMoviePreviewsByGenre(genre = dummyGenre, page = page)

        assertEquals(expectedResult, actualResult)
        coVerify(exactly = 1) { remoteDataSource.getListMoviePreviewsByGenre(genre = dummyGenre, page = page) }
    }

    @Test
    fun `given remote datasource succeeds, when getMovieDetail, then returns success with movie detail`() = runTest {
        val movieId = 101L
        val dummyDetail = TestDummyFactory.createDummyMovieDetailData(id = movieId)
        val expectedResult: Result<MovieDetailData, DataError> = Result.Success(dummyDetail)
        coEvery { remoteDataSource.getMovieDetail(selectedMovieId = movieId) } returns expectedResult

        val actualResult = repository.getMovieDetail(selectedMovieId = movieId)

        assertEquals(expectedResult, actualResult)
        coVerify(exactly = 1) { remoteDataSource.getMovieDetail(selectedMovieId = movieId) }
    }

    @Test
    fun `given remote datasource returns error, when getMovieDetail, then returns error`() = runTest {
        val movieId = 101L
        val expectedError = RootNetworkError.NOT_FOUND
        val expectedResult: Result<MovieDetailData, DataError> = Result.Error(expectedError)
        coEvery { remoteDataSource.getMovieDetail(selectedMovieId = movieId) } returns expectedResult

        val actualResult = repository.getMovieDetail(selectedMovieId = movieId)

        assertEquals(expectedResult, actualResult)
        coVerify(exactly = 1) { remoteDataSource.getMovieDetail(selectedMovieId = movieId) }
    }

    @Test
    fun `given remote datasource succeeds, when getListMovieReviews, then returns success with paged reviews`() = runTest {
        val movieId = 101L
        val page = 1
        val dummyReviews = TestDummyFactory.createDummyMovieReviewsList()
        val dummyPage = TestDummyFactory.createDummyPage(pagedList = dummyReviews, hasNext = false)
        val expectedResult: Result<Page<MovieReviewData>, DataError> = Result.Success(dummyPage)

        coEvery { remoteDataSource.getListMovieReviews(selectedMovieId = movieId, page = page) } returns expectedResult

        val actualResult = repository.getListMovieReviews(selectedMovieId = movieId, page = page)

        assertEquals(expectedResult, actualResult)
        coVerify(exactly = 1) { remoteDataSource.getListMovieReviews(selectedMovieId = movieId, page = page) }
    }

    @Test
    fun `given remote datasource returns error, when getListMovieReviews, then returns error`() = runTest {
        val movieId = 101L
        val page = 1
        val expectedError = RootNetworkError.SERVER_UNAVAILABLE
        val expectedResult: Result<Page<MovieReviewData>, DataError> = Result.Error(expectedError)

        coEvery { remoteDataSource.getListMovieReviews(selectedMovieId = movieId, page = page) } returns expectedResult

        val actualResult = repository.getListMovieReviews(selectedMovieId = movieId, page = page)

        assertEquals(expectedResult, actualResult)
        coVerify(exactly = 1) { remoteDataSource.getListMovieReviews(selectedMovieId = movieId, page = page) }
    }
}

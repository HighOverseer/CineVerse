package com.fajar.cineverse.core.data.datasource.remote

import com.fajar.cineverse.core.data.datasource.remote.implementation.MovieRemoteDataSourceImpl
import com.fajar.cineverse.core.data.datasource.remote.implementation.network.MovieAPIService
import com.fajar.cineverse.core.data.datasource.remote.implementation.network.common.HttpException
import com.fajar.cineverse.core.data.datasource.remote.implementation.network.dto.AuthorDetailsDTO
import com.fajar.cineverse.core.data.datasource.remote.implementation.network.dto.GetAllMovieGenresDTO
import com.fajar.cineverse.core.data.datasource.remote.implementation.network.dto.GetAssociatedMovieVideosDTO
import com.fajar.cineverse.core.data.datasource.remote.implementation.network.dto.GetListMoviePreviewsResponseDTO
import com.fajar.cineverse.core.data.datasource.remote.implementation.network.dto.GetMovieDetailResponseDTO
import com.fajar.cineverse.core.data.datasource.remote.implementation.network.dto.GetMovieReviewsResponseDTO
import com.fajar.cineverse.core.data.datasource.remote.implementation.network.dto.GetMovieReviewsResponseItemDTO
import com.fajar.cineverse.core.data.datasource.remote.implementation.network.dto.MovieGenreItemDTO
import com.fajar.cineverse.core.data.datasource.remote.implementation.network.dto.MoviePreviewsResponseItemDTO
import com.fajar.cineverse.core.data.datasource.remote.implementation.network.dto.MovieVideosItemDTO
import com.fajar.cineverse.core.data.datasource.remote.implementation.network.mapper.MapperToDomain
import com.fajar.cineverse.core.data.datasource.remote.implementation.network.mapper.MovieImageUrlResolver
import com.fajar.cineverse.core.domain.model.DataError
import com.fajar.cineverse.core.domain.model.MovieDetailData
import com.fajar.cineverse.core.domain.model.MovieGenre
import com.fajar.cineverse.core.domain.model.MoviePreviewsItemData
import com.fajar.cineverse.core.domain.model.MovieReviewData
import com.fajar.cineverse.core.domain.model.Page
import com.fajar.cineverse.core.domain.model.Rating
import com.fajar.cineverse.core.domain.model.Result
import com.fajar.cineverse.core.domain.model.RootNetworkError
import com.fajar.cineverse.core.test_utils.TestDispatcherProvider
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.net.UnknownHostException

class MovieRemoteDataSourceImplTest {

    private lateinit var apiService: MovieAPIService
    private lateinit var mapperToDomain: MapperToDomain
    private lateinit var testScheduler: TestCoroutineScheduler
    private lateinit var dispatcherProvider: TestDispatcherProvider
    private lateinit var remoteDataSource: MovieRemoteDataSource

    private val baseImageUrl = "https://image.tmdb.org/t/p/original/"

    @Before
    fun setUp() {
        apiService = mockk()
        mapperToDomain = MapperToDomain(MovieImageUrlResolver(baseImageUrl))
        testScheduler = TestCoroutineScheduler()
        dispatcherProvider = TestDispatcherProvider(testScheduler)
        remoteDataSource = MovieRemoteDataSourceImpl(
            apiService = apiService,
            mapperToDomain = mapperToDomain,
            dispatcherProvider = dispatcherProvider
        )
    }

    @Test
    fun `given api service returns genres DTO, when getAllMovieGenres, then returns success result with mapped genres`() = runTest(testScheduler) {
        val genresDto = GetAllMovieGenresDTO(
            genres = listOf(
                MovieGenreItemDTO(id = 28, name = "Action"),
                MovieGenreItemDTO(id = 12, name = "Adventure")
            )
        )
        coEvery { apiService.getAllMovieGenres() } returns genresDto

        val expectedResult: Result<List<MovieGenre>, DataError> = Result.Success(
            listOf(
                MovieGenre(id = 28L, name = "Action"),
                MovieGenre(id = 12L, name = "Adventure")
            )
        )

        val actualResult = remoteDataSource.getAllMovieGenres()

        assertEquals(expectedResult, actualResult)
        coVerify(exactly = 1) { apiService.getAllMovieGenres() }
    }

    @Test
    fun `given api service throws http exception, when getAllMovieGenres, then returns network error result`() = runTest(testScheduler) {
        coEvery { apiService.getAllMovieGenres() } throws HttpException(statusCode = 404)
        val expectedResult: Result<List<MovieGenre>, DataError> = Result.Error(RootNetworkError.NOT_FOUND)

        val actualResult = remoteDataSource.getAllMovieGenres()

        assertEquals(expectedResult, actualResult)
        coVerify(exactly = 1) { apiService.getAllMovieGenres() }
    }

    @Test
    fun `given api service returns previews DTO, when getListMoviePreviewsByGenre, then returns success result with mapped page`() = runTest(testScheduler) {
        val genre = MovieGenre(id = 28L, name = "Action")
        val page = 1
        val previewsResponseDto = GetListMoviePreviewsResponseDTO(
            page = 1,
            totalPages = 5,
            totalResults = 50,
            results = listOf(
                MoviePreviewsResponseItemDTO(
                    id = 101,
                    title = "Inception",
                    posterPath = "/poster.jpg",
                    voteAverage = 8.8,
                    voteCount = 1000
                )
            )
        )
        coEvery { apiService.getMoviePreviewsByGenre(genreId = 28L, page = page) } returns previewsResponseDto

        val expectedResult: Result<Page<MoviePreviewsItemData>, DataError> = Result.Success(
            Page(
                pagedList = listOf(
                    MoviePreviewsItemData(
                        id = 101L,
                        title = "Inception",
                        thumbnailImageUrl = "${baseImageUrl}/poster.jpg",
                        rating = Rating(relativeScore = 8.8, maxScore = 10.0, count = 1000)
                    )
                ),
                hasNext = true
            )
        )

        val actualResult = remoteDataSource.getListMoviePreviewsByGenre(genre = genre, page = page)

        assertEquals(expectedResult, actualResult)
        coVerify(exactly = 1) { apiService.getMoviePreviewsByGenre(genreId = 28L, page = page) }
    }

    @Test
    fun `given api service throws unknown host exception, when getListMoviePreviewsByGenre, then returns no connectivity error`() = runTest(testScheduler) {
        val genre = MovieGenre(id = 28L, name = "Action")
        val page = 1
        coEvery { apiService.getMoviePreviewsByGenre(genreId = 28L, page = page) } throws UnknownHostException()
        val expectedResult: Result<Page<MoviePreviewsItemData>, DataError> = Result.Error(RootNetworkError.NO_CONNECTIVITY_OR_SERVER_UNREACHABLE)

        val actualResult = remoteDataSource.getListMoviePreviewsByGenre(genre = genre, page = page)

        assertEquals(expectedResult, actualResult)
        coVerify(exactly = 1) { apiService.getMoviePreviewsByGenre(genreId = 28L, page = page) }
    }

    @Test
    fun `given api service returns movie detail and video DTOs, when getMovieDetail, then returns success result with mapped movie detail`() = runTest(testScheduler) {
        val movieId = 101L
        val detailDto = GetMovieDetailResponseDTO(
            id = 101,
            title = "Inception",
            backdropPath = "/backdrop.jpg",
            overview = "Dream heist movie",
            voteAverage = 8.8,
            voteCount = 1000
        )
        val videosDto = GetAssociatedMovieVideosDTO(
            results = listOf(
                MovieVideosItemDTO(site = "YouTube", key = "YoHD9XEInc0", name = "Trailer", type = "Trailer")
            )
        )

        coEvery { apiService.getSelectedMovieDetail(movieId = movieId) } returns detailDto
        coEvery { apiService.getAssociatedMovieVideos(movieId = movieId) } returns videosDto

        val expectedResult: Result<MovieDetailData, DataError> = Result.Success(
            MovieDetailData(
                id = 101L,
                title = "Inception",
                imageUrl = "${baseImageUrl}/backdrop.jpg",
                overview = "Dream heist movie",
                rating = Rating(relativeScore = 8.8, maxScore = 10.0, count = 1000),
                associatedYTVideoKey = "YoHD9XEInc0"
            )
        )

        val actualResult = remoteDataSource.getMovieDetail(selectedMovieId = movieId)

        assertEquals(expectedResult, actualResult)
        coVerify(exactly = 1) { apiService.getSelectedMovieDetail(movieId = movieId) }
        coVerify(exactly = 1) { apiService.getAssociatedMovieVideos(movieId = movieId) }
    }

    @Test
    fun `given api service returns null backdrop path, when getMovieDetail, then returns unexpected error`() = runTest(testScheduler) {
        val movieId = 101L
        val detailDto = GetMovieDetailResponseDTO(
            id = 101,
            title = "Inception",
            backdropPath = null,
            overview = "Dream heist movie",
            voteAverage = 8.8,
            voteCount = 1000
        )
        val videosDto = GetAssociatedMovieVideosDTO(results = emptyList())

        coEvery { apiService.getSelectedMovieDetail(movieId = movieId) } returns detailDto
        coEvery { apiService.getAssociatedMovieVideos(movieId = movieId) } returns videosDto

        val expectedResult: Result<MovieDetailData, DataError> = Result.Error(RootNetworkError.UNEXPECTED_ERROR)

        val actualResult = remoteDataSource.getMovieDetail(selectedMovieId = movieId)

        assertEquals(expectedResult, actualResult)
        coVerify(exactly = 1) { apiService.getSelectedMovieDetail(movieId = movieId) }
        coVerify(exactly = 1) { apiService.getAssociatedMovieVideos(movieId = movieId) }
    }

    @Test
    fun `given api service throws http exception, when getMovieDetail, then returns network error result`() = runTest(testScheduler) {
        val movieId = 101L
        coEvery { apiService.getSelectedMovieDetail(movieId = movieId) } throws HttpException(statusCode = 500)
        coEvery { apiService.getAssociatedMovieVideos(movieId = movieId) } returns GetAssociatedMovieVideosDTO(emptyList())

        val expectedResult: Result<MovieDetailData, DataError> = Result.Error(RootNetworkError.INTERNAL_SERVER_ERROR)

        val actualResult = remoteDataSource.getMovieDetail(selectedMovieId = movieId)

        assertEquals(expectedResult, actualResult)
    }

    @Test
    fun `given api service returns reviews DTO, when getListMovieReviews, then returns success result with mapped page`() = runTest(testScheduler) {
        val movieId = 101L
        val page = 1
        val reviewsDto = GetMovieReviewsResponseDTO(
            page = 1,
            totalPages = 2,
            totalResults = 10,
            results = listOf(
                GetMovieReviewsResponseItemDTO(
                    id = "rev_1",
                    content = "Awesome movie!",
                    createdAt = "2023-01-01T00:00:00.000Z",
                    authorDetails = AuthorDetailsDTO(username = "john_doe", rating = 9.0)
                )
            )
        )
        coEvery { apiService.getSelectedMovieReview(movieId = movieId, page = page) } returns reviewsDto

        val expectedResult: Result<Page<MovieReviewData>, DataError> = Result.Success(
            Page(
                pagedList = listOf(
                    MovieReviewData(
                        id = "rev_1",
                        comment = "Awesome movie!",
                        postedTimestamp = 1672531200000L,
                        reviewerUsername = "john_doe"
                    )
                ),
                hasNext = true
            )
        )

        val actualResult = remoteDataSource.getListMovieReviews(selectedMovieId = movieId, page = page)

        assertEquals(expectedResult, actualResult)
        coVerify(exactly = 1) { apiService.getSelectedMovieReview(movieId = movieId, page = page) }
    }

    @Test
    fun `given api service throws http exception, when getListMovieReviews, then returns network error result`() = runTest(testScheduler) {
        val movieId = 101L
        val page = 1
        coEvery { apiService.getSelectedMovieReview(movieId = movieId, page = page) } throws HttpException(statusCode = 503)
        val expectedResult: Result<Page<MovieReviewData>, DataError> = Result.Error(RootNetworkError.SERVER_UNAVAILABLE)

        val actualResult = remoteDataSource.getListMovieReviews(selectedMovieId = movieId, page = page)

        assertEquals(expectedResult, actualResult)
        coVerify(exactly = 1) { apiService.getSelectedMovieReview(movieId = movieId, page = page) }
    }
}

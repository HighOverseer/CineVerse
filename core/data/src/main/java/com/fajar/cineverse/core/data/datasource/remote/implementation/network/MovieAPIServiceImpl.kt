package com.fajar.cineverse.core.data.datasource.remote.implementation.network

import com.fajar.cineverse.core.data.datasource.remote.implementation.network.common.DefaultAPIServiceScope
import com.fajar.cineverse.core.data.datasource.remote.implementation.network.dto.GetAllMovieGenresDTO
import com.fajar.cineverse.core.data.datasource.remote.implementation.network.dto.GetListMoviePreviewsResponseDTO
import com.fajar.cineverse.core.data.datasource.remote.implementation.network.dto.GetMovieDetailResponseDTO
import com.fajar.cineverse.core.data.datasource.remote.implementation.network.dto.GetMovieReviewsResponseDTO
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.http.appendPathSegments
import javax.inject.Inject

internal class MovieAPIServiceImpl @Inject constructor(
    private val httpClient: HttpClient
) : MovieAPIService, DefaultAPIServiceScope() {

    override suspend fun getAllMovieGenres(): GetAllMovieGenresDTO {
        return httpClient.get(ENDPOINTS.GET_ALL_MOVIE_GENRES.endpointPath)
            .getBodyIfNotSuccessThrowHttpException()
    }

    override suspend fun getMoviePreviewsByGenre(
        genreId: Long,
        page: Int
    ): GetListMoviePreviewsResponseDTO {
        return httpClient.get(ENDPOINTS.GET_DISCOVERED_MOVIE_PREVIEWS.endpointPath) {
            url {
                parameters.apply {
                    append(QueryParamKey.WithGenre.key, genreId.toString())
                    append(QueryParamKey.Page.key, page.toString())
                }
            }
        }.getBodyIfNotSuccessThrowHttpException()
    }

    override suspend fun getSelectedMovieDetail(movieId: Long): GetMovieDetailResponseDTO {
        return httpClient.get(movieId.toString())
            .getBodyIfNotSuccessThrowHttpException()
    }

    override suspend fun getSelectedMovieReview(
        movieId: Long,
        page: Int
    ): GetMovieReviewsResponseDTO {
        return httpClient.get {
            url {
                appendPathSegments(movieId.toString(), ENDPOINTS.GET_MOVIE_REVIEWS.endpointPath)
                parameters.append(QueryParamKey.Page.key, page.toString())
            }
        }.getBodyIfNotSuccessThrowHttpException()
    }

    private enum class ENDPOINTS(val endpointPath: String) {
        GET_ALL_MOVIE_GENRES("genre/movie/list"),
        GET_DISCOVERED_MOVIE_PREVIEWS("discover/movie"),
        GET_MOVIE_REVIEWS("reviews")
    }

    private enum class QueryParamKey(val key: String) {
        WithGenre("with_genres"),
        Page("page"),
    }
}
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
        return httpClient.get(ENDPOINTS.GetAllMovieGenres.endpointPath)
            .getBodyIfNotSuccessThrowHttpException()
    }

    override suspend fun getMoviePreviewsByGenre(
        genreId: Long,
        page: Int
    ): GetListMoviePreviewsResponseDTO {
        return httpClient.get(ENDPOINTS.GetDiscoveredMoviePreviews.endpointPath) {
            url {
                parameters.apply {
                    append(QueryParamKey.WithGenre.key, genreId.toString())
                    append(QueryParamKey.Page.key, page.toString())
                }
            }
        }.getBodyIfNotSuccessThrowHttpException()
    }

    override suspend fun getSelectedMovieDetail(movieId: Long): GetMovieDetailResponseDTO {
        return httpClient.get(
            ENDPOINTS.GetMovieDetail(movieId = movieId).endpointPath
        ).getBodyIfNotSuccessThrowHttpException()
    }

    override suspend fun getSelectedMovieReview(
        movieId: Long,
        page: Int
    ): GetMovieReviewsResponseDTO {
        return httpClient.get {
            url {
                appendPathSegments(
                    *ENDPOINTS
                        .GetMovieReviews(movieId = movieId)
                        .convertEndpointsToPathComponents()
                        .toTypedArray()
                )
                parameters.append(QueryParamKey.Page.key, page.toString())
            }
        }.getBodyIfNotSuccessThrowHttpException()
    }

    private sealed interface ENDPOINTS {
        val endpointPath: String

        data object GetAllMovieGenres : ENDPOINTS {
            override val endpointPath: String
                get() = "genre/movie/list"
        }

        data object GetDiscoveredMoviePreviews : ENDPOINTS {
            override val endpointPath: String
                get() = "discover/movie"
        }

        data class GetMovieDetail(val movieId: Long) : ENDPOINTS {
            override val endpointPath: String
                get() = "movie/${movieId}"
        }

        data class GetMovieReviews(val movieId: Long) : ENDPOINTS {
            override val endpointPath: String
                get() = "movie/${movieId}/reviews"
        }

        fun convertEndpointsToPathComponents(): List<String> {
            return endpointPath.split("/")
        }
    }

    private enum class QueryParamKey(val key: String) {
        WithGenre("with_genres"),
        Page("page"),
    }
}
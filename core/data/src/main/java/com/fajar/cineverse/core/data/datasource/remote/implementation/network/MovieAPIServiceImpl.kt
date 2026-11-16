package com.fajar.cineverse.core.data.datasource.remote.implementation.network

import com.fajar.cineverse.core.data.datasource.remote.implementation.network.common.DefaultAPIServiceScope
import com.fajar.cineverse.core.data.datasource.remote.implementation.network.dto.GetAllMovieGenresDTO
import com.fajar.cineverse.core.data.datasource.remote.implementation.network.dto.GetAssociatedMovieVideosDTO
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
        return httpClient.get(Endpoints.GetAllMovieGenres.endpointPath)
            .getBodyIfNotSuccessThrowHttpException()
    }

    override suspend fun getMoviePreviewsByGenre(
        genreId: Long,
        page: Int
    ): GetListMoviePreviewsResponseDTO {
        return httpClient.get(Endpoints.GetDiscoveredMoviePreviews.endpointPath) {
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
            Endpoints.GetMovieDetail(movieId = movieId).endpointPath
        ).getBodyIfNotSuccessThrowHttpException()
    }

    override suspend fun getSelectedMovieReview(
        movieId: Long,
        page: Int
    ): GetMovieReviewsResponseDTO {
        return httpClient.get {
            url {
                appendPathSegments(
                    *Endpoints
                        .GetMovieReviews(movieId = movieId)
                        .convertEndpointsToPathComponents()
                        .toTypedArray()
                )
                parameters.append(QueryParamKey.Page.key, page.toString())
            }
        }.getBodyIfNotSuccessThrowHttpException()
    }

    override suspend fun getAssociatedMovieVideos(movieId: Long): GetAssociatedMovieVideosDTO {
        return httpClient.get(
            Endpoints.GetMovieVideos(movieId = movieId).endpointPath
        ).getBodyIfNotSuccessThrowHttpException()
    }

    private sealed interface Endpoints {
        val endpointPath: String

        data object GetAllMovieGenres : Endpoints {
            override val endpointPath: String
                get() = "genre/movie/list"
        }

        data object GetDiscoveredMoviePreviews : Endpoints {
            override val endpointPath: String
                get() = "discover/movie"
        }

        data class GetMovieDetail(val movieId: Long) : Endpoints {
            override val endpointPath: String
                get() = "movie/${movieId}"
        }

        data class GetMovieReviews(val movieId: Long) : Endpoints {
            override val endpointPath: String
                get() = "movie/${movieId}/reviews"
        }

        data class GetMovieVideos(val movieId: Long) : Endpoints {
            override val endpointPath: String
                get() = "movie/$movieId/videos"
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
package com.fajar.cineverse.core.data.datasource.remote.implementation.network

import com.fajar.cineverse.core.data.datasource.remote.implementation.network.common.DefaultAPIServiceScope
import com.fajar.cineverse.core.data.datasource.remote.implementation.network.dto.GetAllMovieGenresDTO
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import javax.inject.Inject

internal class MovieAPIServiceImpl @Inject constructor(
    private val httpClient: HttpClient
) : MovieAPIService, DefaultAPIServiceScope() {

    override suspend fun getAllMovieGenres(): GetAllMovieGenresDTO {
        return httpClient.get(ENDPOINTS.GET_ALL_MOVIE_GENRES.endpointPath)
            .getBodyIfNotSuccessThrowHttpException()
    }

    private enum class ENDPOINTS(val endpointPath: String) {
        GET_ALL_MOVIE_GENRES("genre/movie/list"),
    }
}
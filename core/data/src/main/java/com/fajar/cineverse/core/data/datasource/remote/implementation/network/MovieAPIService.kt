package com.fajar.cineverse.core.data.datasource.remote.implementation.network

import com.fajar.cineverse.core.data.datasource.remote.implementation.network.dto.GetAllMovieGenresDTO

internal interface MovieAPIService {
    suspend fun getAllMovieGenres(): GetAllMovieGenresDTO
}
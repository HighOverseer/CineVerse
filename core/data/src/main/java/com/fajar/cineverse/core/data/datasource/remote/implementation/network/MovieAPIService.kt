package com.fajar.cineverse.core.data.datasource.remote.implementation.network

import com.fajar.cineverse.core.data.datasource.remote.implementation.network.dto.GetAllMovieGenresDTO
import com.fajar.cineverse.core.data.datasource.remote.implementation.network.dto.GetListMoviePreviewsResponseDTO

internal interface MovieAPIService {
    suspend fun getAllMovieGenres(): GetAllMovieGenresDTO

    suspend fun getMoviePreviewsByGenre(genreId: Long, page: Int): GetListMoviePreviewsResponseDTO
}
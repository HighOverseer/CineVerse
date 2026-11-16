package com.fajar.cineverse.core.data.datasource.remote.implementation.network

import com.fajar.cineverse.core.data.datasource.remote.implementation.network.dto.GetAllMovieGenresDTO
import com.fajar.cineverse.core.data.datasource.remote.implementation.network.dto.GetListMoviePreviewsResponseDTO
import com.fajar.cineverse.core.data.datasource.remote.implementation.network.dto.GetMovieDetailResponseDTO
import com.fajar.cineverse.core.data.datasource.remote.implementation.network.dto.GetMovieReviewsResponseDTO

internal interface MovieAPIService {
    suspend fun getAllMovieGenres(): GetAllMovieGenresDTO

    suspend fun getMoviePreviewsByGenre(
        genreId: Long,
        page: Int,
    ): GetListMoviePreviewsResponseDTO

    suspend fun getSelectedMovieDetail(movieId: Long): GetMovieDetailResponseDTO
    suspend fun getSelectedMovieReview(
        movieId: Long,
        page: Int
    ): GetMovieReviewsResponseDTO
}
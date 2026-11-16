package com.fajar.cineverse.core.data.datasource.remote

import com.fajar.cineverse.core.domain.model.DataError
import com.fajar.cineverse.core.domain.model.MovieGenre
import com.fajar.cineverse.core.domain.model.MoviePreviewsItemData
import com.fajar.cineverse.core.domain.model.Page
import com.fajar.cineverse.core.domain.model.Result

internal interface MovieRemoteDataSource {
    suspend fun getAllMovieGenres(): Result<List<MovieGenre>, DataError>
    suspend fun getListMoviePreviewsByGenre(
        genre: MovieGenre,
        page: Int
    ): Result<Page<MoviePreviewsItemData>, DataError>
}
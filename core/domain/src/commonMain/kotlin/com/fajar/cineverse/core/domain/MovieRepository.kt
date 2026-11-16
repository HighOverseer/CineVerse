package com.fajar.cineverse.core.domain

import com.fajar.cineverse.core.domain.model.DataError
import com.fajar.cineverse.core.domain.model.MovieGenre
import com.fajar.cineverse.core.domain.model.MoviePreviewsItemData
import com.fajar.cineverse.core.domain.model.Page
import com.fajar.cineverse.core.domain.model.Result

interface MovieRepository {
    suspend fun getAllMovieGenres(): Result<List<MovieGenre>, DataError>
    suspend fun getListMoviePreviewsByGenre(
        genre: MovieGenre,
        page: Int
    ): Result<Page<MoviePreviewsItemData>, DataError>
}
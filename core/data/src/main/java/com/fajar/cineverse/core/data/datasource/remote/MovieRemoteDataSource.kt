package com.fajar.cineverse.core.data.datasource.remote

import com.fajar.cineverse.core.domain.model.DataError
import com.fajar.cineverse.core.domain.model.MovieGenre
import com.fajar.cineverse.core.domain.model.Result

internal interface MovieRemoteDataSource {
    suspend fun getAllMovieGenres(): Result<List<MovieGenre>, DataError>
}
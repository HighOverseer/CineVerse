package com.fajar.cineverse.core.data

import com.fajar.cineverse.core.data.datasource.remote.MovieRemoteDataSource
import com.fajar.cineverse.core.domain.MovieRepository
import com.fajar.cineverse.core.domain.model.DataError
import com.fajar.cineverse.core.domain.model.MovieGenre
import com.fajar.cineverse.core.domain.model.Result
import javax.inject.Inject

internal class MovieRepositoryImpl @Inject constructor(
    private val remoteDataSource: MovieRemoteDataSource
) : MovieRepository {

    override suspend fun getAllMovieGenres(): Result<List<MovieGenre>, DataError> {
        return remoteDataSource.getAllMovieGenres()
    }
}
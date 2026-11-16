package com.fajar.cineverse.core.data.datasource.remote.implementation

import com.fajar.cineverse.core.common.DispatcherProvider
import com.fajar.cineverse.core.data.datasource.remote.MovieRemoteDataSource
import com.fajar.cineverse.core.data.datasource.remote.implementation.network.MovieAPIService
import com.fajar.cineverse.core.data.datasource.remote.implementation.network.common.callApiFromNetwork
import com.fajar.cineverse.core.data.datasource.remote.implementation.network.mapper.MapperToDomain
import com.fajar.cineverse.core.domain.model.DataError
import com.fajar.cineverse.core.domain.model.MovieGenre
import com.fajar.cineverse.core.domain.model.Result
import kotlinx.coroutines.withContext
import javax.inject.Inject

internal class MovieRemoteDataSourceImpl @Inject constructor(
    private val apiService: MovieAPIService,
    private val mapperToDomain: MapperToDomain,
    private val dispatcherProvider: DispatcherProvider
) : MovieRemoteDataSource {
    override suspend fun getAllMovieGenres(): Result<List<MovieGenre>, DataError> =
        withContext(dispatcherProvider.default) {
            callApiFromNetwork(
                execute = {
                    apiService.getAllMovieGenres()
                },
                mapResponseToResultData = { responseData ->
                    val listOfMovieGenres = responseData.genres
                        ?.mapNotNull(
                            transform = mapperToDomain::mapMovieGenreItemDTOToDomain
                        )
                        ?: emptyList()

                    return@callApiFromNetwork listOfMovieGenres
                }
            )
        }
}
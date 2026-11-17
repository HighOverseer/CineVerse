package com.fajar.cineverse.core.data.datasource.remote.implementation

import com.fajar.cineverse.core.common.DispatcherProvider
import com.fajar.cineverse.core.data.datasource.remote.MovieRemoteDataSource
import com.fajar.cineverse.core.data.datasource.remote.implementation.network.MovieAPIService
import com.fajar.cineverse.core.data.datasource.remote.implementation.network.common.callApiFromNetwork
import com.fajar.cineverse.core.data.datasource.remote.implementation.network.mapper.MapperToDomain
import com.fajar.cineverse.core.domain.model.DataError
import com.fajar.cineverse.core.domain.model.MovieDetailData
import com.fajar.cineverse.core.domain.model.MovieGenre
import com.fajar.cineverse.core.domain.model.MoviePreviewsItemData
import com.fajar.cineverse.core.domain.model.MovieReviewData
import com.fajar.cineverse.core.domain.model.Page
import com.fajar.cineverse.core.domain.model.Result
import com.fajar.cineverse.core.domain.model.RootNetworkError
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

    override suspend fun getListMoviePreviewsByGenre(
        genre: MovieGenre,
        page: Int
    ): Result<Page<MoviePreviewsItemData>, DataError> {
        return callApiFromNetwork(
            execute = {
                apiService.getMoviePreviewsByGenre(
                    genreId = genre.id,
                    page = page,
                )
            },
            mapResponseToResultData = { responseData ->
                val mappedListData = responseData.results
                    ?.mapNotNull(mapperToDomain::mapMoviePreviewItemDTOToDomain)
                    ?: emptyList()

                val hasNext = mappedListData.isNotEmpty() &&
                        responseData.page != null &&
                        responseData.page < minOf(
                    responseData.totalPages ?: 0,
                    MAX_REQUESTABLE_PAGE
                )
                return@callApiFromNetwork Page(
                    pagedList = mappedListData,
                    hasNext = hasNext
                )
            }
        )
    }

    override suspend fun getMovieDetail(selectedMovieId: Long): Result<MovieDetailData, DataError> {
        return withContext(dispatcherProvider.default) {
            callApiFromNetwork(
                execute = {
                    Pair(
                        first = apiService.getSelectedMovieDetail(movieId = selectedMovieId),
                        second = apiService.getAssociatedMovieVideos(movieId = selectedMovieId)
                    )
                },
                mapResponseToResultData = { (getMovieDetailResponseDTO, getAssociatedMovieVideoResponseDTO) ->
                    mapperToDomain.mapMovieDetailDTOToDomain(
                        getMovieDetailResponseDTO = getMovieDetailResponseDTO,
                        getAssociatedMovieVideoDTO = getAssociatedMovieVideoResponseDTO
                    ) ?: return@withContext Result.Error(RootNetworkError.UNEXPECTED_ERROR)
                }
            )
        }
    }

    override suspend fun getListMovieReviews(
        selectedMovieId: Long,
        page: Int
    ): Result<Page<MovieReviewData>, DataError> {
        return withContext(dispatcherProvider.default) {
            callApiFromNetwork(
                execute = {
                    apiService.getSelectedMovieReview(
                        movieId = selectedMovieId,
                        page = page
                    )
                },
                mapResponseToResultData = { responseData ->
                    val mappedListData = responseData.results
                        ?.mapNotNull(mapperToDomain::mapMovieReviewDTOToDomain)
                        ?: emptyList()

                    val hasNext = mappedListData.isNotEmpty() &&
                            responseData.page != null &&
                            responseData.page < minOf(
                        responseData.totalPages ?: 0,
                        MAX_REQUESTABLE_PAGE
                    )
                    return@callApiFromNetwork Page(
                        pagedList = mappedListData,
                        hasNext = hasNext
                    )
                }
            )
        }
    }

    companion object {
        /**
         * Based on doc (https://developer.themoviedb.org/docs/errors)
         */
        private const val MAX_REQUESTABLE_PAGE = 500
    }
}
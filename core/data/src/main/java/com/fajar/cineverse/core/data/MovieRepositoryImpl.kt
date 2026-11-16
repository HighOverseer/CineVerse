package com.fajar.cineverse.core.data

import com.fajar.cineverse.core.data.datasource.remote.MovieRemoteDataSource
import com.fajar.cineverse.core.domain.MovieRepository
import com.fajar.cineverse.core.domain.model.DataError
import com.fajar.cineverse.core.domain.model.MovieDetailData
import com.fajar.cineverse.core.domain.model.MovieGenre
import com.fajar.cineverse.core.domain.model.MoviePreviewsItemData
import com.fajar.cineverse.core.domain.model.MovieReviewData
import com.fajar.cineverse.core.domain.model.Page
import com.fajar.cineverse.core.domain.model.Result
import javax.inject.Inject

internal class MovieRepositoryImpl @Inject constructor(
    private val remoteDataSource: MovieRemoteDataSource
) : MovieRepository {

    override suspend fun getAllMovieGenres(): Result<List<MovieGenre>, DataError> {
        return remoteDataSource.getAllMovieGenres()
    }

    override suspend fun getListMoviePreviewsByGenre(
        genre: MovieGenre,
        page: Int
    ): Result<Page<MoviePreviewsItemData>, DataError> {
        return remoteDataSource
            .getListMoviePreviewsByGenre(
                genre = genre,
                page = page
            )
    }

    override suspend fun getMovieDetail(selectedMovieId: Long): Result<MovieDetailData, DataError> {
        return remoteDataSource.getMovieDetail(selectedMovieId)
    }

    override suspend fun getListMovieReviews(
        selectedMovieId: Long,
        page: Int
    ): Result<Page<MovieReviewData>, DataError> {
        return remoteDataSource.getListMovieReviews(
            selectedMovieId = selectedMovieId,
            page = page
        )
    }
}
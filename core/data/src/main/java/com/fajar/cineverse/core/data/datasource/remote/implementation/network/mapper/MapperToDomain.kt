package com.fajar.cineverse.core.data.datasource.remote.implementation.network.mapper

import com.fajar.cineverse.core.data.datasource.remote.implementation.network.dto.GetMovieDetailResponseDTO
import com.fajar.cineverse.core.data.datasource.remote.implementation.network.dto.GetMovieReviewsResponseItemDTO
import com.fajar.cineverse.core.data.datasource.remote.implementation.network.dto.MovieGenreItemDTO
import com.fajar.cineverse.core.data.datasource.remote.implementation.network.dto.MoviePreviewsResponseItemDTO
import com.fajar.cineverse.core.domain.model.MovieDetailData
import com.fajar.cineverse.core.domain.model.MovieGenre
import com.fajar.cineverse.core.domain.model.MoviePreviewsItemData
import com.fajar.cineverse.core.domain.model.MovieReviewData
import com.fajar.cineverse.core.domain.model.Rating
import kotlin.time.Instant

internal class MapperToDomain(
    private val imageUrlResolver: MovieImageUrlResolver
) {
    fun mapMovieGenreItemDTOToDomain(
        dto: MovieGenreItemDTO
    ): MovieGenre? {
        return dto.run {
            MovieGenre(
                id = id?.toLong() ?: return null,
                name = name ?: return null
            )
        }
    }

    fun mapMoviePreviewItemDTOToDomain(
        itemDto: MoviePreviewsResponseItemDTO,
    ): MoviePreviewsItemData? {
        return itemDto.run {
            val thumbnailImageUrl = imageUrlResolver.resolve(posterPath)
                ?: return null

            MoviePreviewsItemData(
                id = id?.toLong() ?: return null,
                title = title ?: return null,
                thumbnailImageUrl = thumbnailImageUrl,
                rating = voteAverage?.let {
                    Rating(
                        relativeScore = it,
                        maxScore = 10.0,
                        count = voteCount
                    )
                },
            )
        }
    }

    fun mapMovieDetailDTOToDomain(
        dto: GetMovieDetailResponseDTO,
    ): MovieDetailData? {
        dto.run {
            val imageUrl = imageUrlResolver.resolve(backdropPath)
                ?: return null

            return MovieDetailData(
                id = id?.toLong() ?: return null,
                title = title ?: return null,
                imageUrl = imageUrl,
                rating = voteAverage?.let {
                    Rating(
                        relativeScore = it,
                        maxScore = 10.0,
                        count = voteCount
                    )
                },
                overview = overview ?: "-",
            )
        }
    }

    fun mapMovieReviewDTOToDomain(
        reviewsDto: GetMovieReviewsResponseItemDTO
    ): MovieReviewData? {
        return reviewsDto.run {
            val postedTimestamp = createdAt?.let { date ->
                Instant.parseOrNull(date)
                    ?.toEpochMilliseconds()
            }

            MovieReviewData(
                id = id ?: return null,
                comment = content ?: return null,
                postedTimestamp = postedTimestamp ?: return null,
                reviewerUsername = authorDetails?.username ?: return null
            )
        }
    }
}
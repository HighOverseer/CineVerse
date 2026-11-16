package com.fajar.cineverse.core.data.datasource.remote.implementation.network.mapper

import com.fajar.cineverse.core.data.datasource.remote.implementation.network.dto.MovieGenreItemDTO
import com.fajar.cineverse.core.data.datasource.remote.implementation.network.dto.MoviePreviewsResponseItemDTO
import com.fajar.cineverse.core.domain.model.MovieGenre
import com.fajar.cineverse.core.domain.model.MoviePreviewsItemData
import com.fajar.cineverse.core.domain.model.Rating

internal class MapperToDomain(
    private val imageUrlResolver: MovieImageUrlResolver
) {
    fun mapMovieGenreItemDTOToDomain(
        dto: MovieGenreItemDTO
    ): MovieGenre? {
        return dto.run {
            MovieGenre(
                id = id?.toLong() ?: return null,
                text = name ?: return null
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
}
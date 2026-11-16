package com.fajar.cineverse.core.data.datasource.remote.implementation.network.mapper

import com.fajar.cineverse.core.data.datasource.remote.implementation.network.dto.MovieGenreItemDTO
import com.fajar.cineverse.core.domain.model.MovieGenre

internal class MapperToDomain {
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
}
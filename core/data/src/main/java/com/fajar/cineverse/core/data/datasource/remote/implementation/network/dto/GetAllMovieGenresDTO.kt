package com.fajar.cineverse.core.data.datasource.remote.implementation.network.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class GetAllMovieGenresDTO(
    @SerialName("genres")
    val genres: List<MovieGenreItemDTO>? = emptyList()
)

@Serializable
internal data class MovieGenreItemDTO(

    @SerialName("name")
    val name: String? = null,

    @SerialName("id")
    val id: Int? = null
)

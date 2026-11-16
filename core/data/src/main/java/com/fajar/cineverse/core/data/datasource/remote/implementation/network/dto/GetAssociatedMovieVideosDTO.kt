package com.fajar.cineverse.core.data.datasource.remote.implementation.network.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class GetAssociatedMovieVideosDTO(

    @SerialName("results")
    val results: List<MovieVideosItemDTO>? = emptyList()
)

@Serializable
internal data class MovieVideosItemDTO(

    @SerialName("site")
    val site: String? = null,

    @SerialName("name")
    val name: String? = null,

    @SerialName("id")
    val id: String? = null,

    @SerialName("type")
    val type: String? = null,

    @SerialName("key")
    val key: String? = null
)

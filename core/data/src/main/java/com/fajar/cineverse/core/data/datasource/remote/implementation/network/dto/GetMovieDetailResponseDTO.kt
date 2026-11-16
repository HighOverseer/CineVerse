package com.fajar.cineverse.core.data.datasource.remote.implementation.network.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class GetMovieDetailResponseDTO(

    @SerialName("title")
    val title: String? = null,

    @SerialName("backdrop_path")
    val backdropPath: String? = null,

    @SerialName("id")
    val id: Int? = null,

    @SerialName("vote_count")
    val voteCount: Int? = null,

    @SerialName("overview")
    val overview: String? = null,

    @SerialName("vote_average")
    val voteAverage: Double? = null

)
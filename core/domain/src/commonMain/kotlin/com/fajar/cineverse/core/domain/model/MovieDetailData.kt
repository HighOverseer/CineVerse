package com.fajar.cineverse.core.domain.model

data class MovieDetailData(
    val id: Long,
    val title: String,
    val imageUrl: String,
    val overview: String,
    val rating: Rating?,
    val associatedYTVideoKey: String?
)
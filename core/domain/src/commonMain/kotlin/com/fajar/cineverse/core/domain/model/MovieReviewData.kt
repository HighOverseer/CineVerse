package com.fajar.cineverse.core.domain.model

data class MovieReviewData(
    val id: String,
    val comment: String,
    val postedTimestamp: Long,
    val reviewerUsername: String
)
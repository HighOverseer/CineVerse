package com.fajar.cineverse.movie_detail.presentation.model

import androidx.compose.runtime.Immutable
import java.util.UUID

@Immutable
internal data class MovieReviewDUI(
    val id: String = UUID.randomUUID().toString(),
    val comment: String = "-",
    val displayPostedDate: String = "-",
    val reviewerUsername: String = "-"
)
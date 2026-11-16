package com.fajar.cineverse.core.domain.model

data class Rating(
    private val relativeScore: Double,
    val maxScore: Double,
    val count: Int?
) {
    val normalizedRelativeScore = relativeScore
        .coerceAtMost(maximumValue = maxScore)
}
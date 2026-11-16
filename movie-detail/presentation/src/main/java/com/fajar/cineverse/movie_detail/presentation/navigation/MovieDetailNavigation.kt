package com.fajar.cineverse.movie_detail.presentation.navigation

import kotlinx.serialization.Serializable

sealed interface MovieDetailNavigation {
    @Serializable
    data class Graph(val movieId: Long)

    @Serializable
    data object MovieDetailRoute
}
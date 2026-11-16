package com.fajar.cineverse.movie_discovery.presentation.navigation

import kotlinx.serialization.Serializable

sealed interface MovieDiscoveryNavigation {
    @Serializable
    data object Graph

    @Serializable
    data object ListMovieGenres
}
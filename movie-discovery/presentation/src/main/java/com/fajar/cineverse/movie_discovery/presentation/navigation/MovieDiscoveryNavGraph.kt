package com.fajar.cineverse.movie_discovery.presentation.navigation

import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.fajar.cineverse.movie_discovery.presentation.ListMovieGenresScreen

fun NavGraphBuilder.movieDiscoveryNavGraph(
    openMovieDetail: (Long) -> Unit,
) {
    navigation<MovieDiscoveryNavigation.Graph>(
        startDestination = MovieDiscoveryNavigation.ListMovieGenres
    ) {
        composable<MovieDiscoveryNavigation.ListMovieGenres> {
            ListMovieGenresScreen(
                viewModel = hiltViewModel(),
                onNavigateToListPreview = { }
            )
        }
    }
}
package com.fajar.cineverse.movie_discovery.presentation.navigation

import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import androidx.navigation.toRoute
import com.fajar.cineverse.core.domain.model.MovieGenre
import com.fajar.cineverse.core.ui.utils.navigateSafely
import com.fajar.cineverse.movie_discovery.presentation.ListMovieGenresScreen
import com.fajar.cineverse.movie_discovery.presentation.ListMoviePreviewsScreen
import com.fajar.cineverse.movie_discovery.presentation.view_model.ListMoviePreviewsViewModel

fun NavGraphBuilder.movieDiscoveryNavGraph(
    navController: NavHostController,
    openMovieDetail: (Long) -> Unit,
) {
    navigation<MovieDiscoveryNavigation.Graph>(
        startDestination = MovieDiscoveryNavigation.ListMovieGenres
    ) {
        composable<MovieDiscoveryNavigation.ListMovieGenres> {
            ListMovieGenresScreen(
                viewModel = hiltViewModel(),
                onNavigateToListPreview = { selectedMovieGenre ->
                    navController.navigateSafely(
                        MovieDiscoveryNavigation.ListMoviePreviews(
                            selectedGenreId = selectedMovieGenre.id,
                            selectedGenreName = selectedMovieGenre.name
                        )
                    )
                }
            )
        }
        composable<MovieDiscoveryNavigation.ListMoviePreviews> { navBackStackEntry ->
            val route = navBackStackEntry.toRoute<MovieDiscoveryNavigation.ListMoviePreviews>()
            val viewModel = hiltViewModel(
                creationCallback = { factory: ListMoviePreviewsViewModel.Factory ->
                    factory.create(
                        selectedGenre = MovieGenre(
                            id = route.selectedGenreId,
                            name = route.selectedGenreName
                        )
                    )
                }
            )

            ListMoviePreviewsScreen(
                viewModel = viewModel,
                onNavigateToDetail = { openMovieDetail(it.id) }
            )
        }
    }
}
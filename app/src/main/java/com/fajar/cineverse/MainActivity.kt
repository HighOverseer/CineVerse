package com.fajar.cineverse

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.fajar.cineverse.core.ui.theme.CineVerseAppTheme
import com.fajar.cineverse.core.ui.utils.navigateSafely
import com.fajar.cineverse.movie_detail.presentation.navigation.MovieDetailNavigation
import com.fajar.cineverse.movie_detail.presentation.navigation.movieDetailNavGraph
import com.fajar.cineverse.movie_discovery.presentation.navigation.MovieDiscoveryNavigation
import com.fajar.cineverse.movie_discovery.presentation.navigation.movieDiscoveryNavGraph
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        setContent {
            CineVerseAppTheme {
                val navController = rememberNavController()
                NavHost(
                    navController = navController,
                    startDestination = MovieDiscoveryNavigation.Graph
                ) {
                    movieDiscoveryNavGraph(
                        navController = navController,
                        openMovieDetail = { selectedMovieId: Long ->
                            navController.navigateSafely(
                                MovieDetailNavigation.Graph(
                                    movieId = selectedMovieId
                                )
                            )
                        },
                    )

                    movieDetailNavGraph(
                        navController = navController
                    )
                }
            }
        }
    }
}

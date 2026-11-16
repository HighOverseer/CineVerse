package com.fajar.cineverse.movie_discovery.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fajar.cineverse.core.domain.model.MovieGenre
import com.fajar.cineverse.core.ui.components.GeneralDialog
import com.fajar.cineverse.core.ui.theme.Black10
import com.fajar.cineverse.core.ui.theme.CineVerseAppTheme
import com.fajar.cineverse.core.ui.theme.Grey90
import com.fajar.cineverse.core.ui.utils.shimmeringEffect
import com.fajar.cineverse.movie_discovery.presentation.components.SliverItemCard
import com.fajar.cineverse.movie_discovery.presentation.view_model.ListMovieGenresUIState
import com.fajar.cineverse.movie_discovery.presentation.view_model.ListMovieGenresViewModel
import kotlinx.collections.immutable.persistentListOf
import com.fajar.cineverse.core.ui.R as CoreR

@Composable
internal fun ListMovieGenresScreen(
    viewModel: ListMovieGenresViewModel = hiltViewModel(),
    onNavigateToListPreview: (MovieGenre) -> Unit = { }
) {
    LaunchedEffect(Unit) {
        viewModel.initializeIfNeeded()
    }

    val uiState by viewModel.uiState
        .collectAsStateWithLifecycle()

    ListMovieGenresScreen(
        modifier = Modifier,
        uiState = uiState,
        onNavigateToListPreview = onNavigateToListPreview,
        onErrorMessageHandled = viewModel::onErrorMessageHandled
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ListMovieGenresScreen(
    modifier: Modifier = Modifier,
    uiState: ListMovieGenresUIState = ListMovieGenresUIState(),
    onNavigateToListPreview: (MovieGenre) -> Unit = { },
    onErrorMessageHandled: () -> Unit = { },
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val focusManager = LocalFocusManager.current

    uiState.errorUserMessage?.let { errorUserMessage ->
        GeneralDialog(
            title = stringResource(CoreR.string.error),
            message = errorUserMessage.getValue(),
            onClick = onErrorMessageHandled
        )
    }

    Scaffold(
        containerColor = Color.White,
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .nestedScroll(scrollBehavior.nestedScrollConnection)
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() },
                onClick = { focusManager.clearFocus() }
            ),
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors().copy(
                    scrolledContainerColor = Color.White,
                    containerColor = Color.White
                ),
                title = {
                    Text(
                        modifier = Modifier,
                        text = stringResource(R.string.choose_a_movie_genre),
                        style = MaterialTheme.typography.headlineLarge,
                        color = Black10
                    )
                },
                scrollBehavior = scrollBehavior
            )

        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(color = Grey90),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            contentPadding = PaddingValues(start = 32.dp, end = 32.dp, top = 16.dp, bottom = 48.dp)
        ) {
            when {
                uiState.isLoading -> {
                    val placeholdersCount = 10
                    items(placeholdersCount) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp)
                                .height(16.dp)
                                .shimmeringEffect()
                        )

                        if (it != placeholdersCount.minus(1)) {
                            Spacer(Modifier.height(12.dp))
                            HorizontalDivider()
                            Spacer(Modifier.height(16.dp))
                        }
                    }
                }

                uiState.listOfMovieGenres.isEmpty() -> {
                    item {
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center,
                            text = stringResource(R.string.no_movie_genre_msg),
                            style = MaterialTheme.typography.labelMedium,
                            color = Black10
                        )
                    }
                }

                else -> {
                    itemsIndexed(
                        items = uiState.listOfMovieGenres,
                        key = { _, item -> item.id }) { index, currentMovieGenre ->

                        SliverItemCard(
                            modifier = Modifier
                                .padding(horizontal = 8.dp)
                                .heightIn(min = 48.dp),
                            text = currentMovieGenre.name,
                            onClicked = { onNavigateToListPreview(currentMovieGenre) }
                        )

                        if (index != uiState.listOfMovieGenres.lastIndex) {
                            HorizontalDivider()
                        }
                    }
                }
            }

        }
    }
}

@Preview
@Composable
private fun ListMovieGenresScreenPreview() {
    CineVerseAppTheme {
        ListMovieGenresScreen(
            uiState = ListMovieGenresUIState(
                isLoading = false,
                listOfMovieGenres = persistentListOf(
                    MovieGenre(1, "Mystery"),
                    MovieGenre(2, "Thrill"),
                    MovieGenre(3, "Horror"),
                )
            )
        )
    }
}
package com.fajar.cineverse.movie_discovery.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.fajar.cineverse.core.domain.model.MoviePreviewsItemData
import com.fajar.cineverse.core.ui.theme.Black10
import com.fajar.cineverse.core.ui.utils.rememberDummyPagingItems
import com.fajar.cineverse.movie_discovery.presentation.contents.ListMoviePreviewsMainContent
import com.fajar.cineverse.movie_discovery.presentation.view_model.ListMoviePreviewsViewModel

@Composable
internal fun ListMoviePreviewsScreen(
    viewModel: ListMoviePreviewsViewModel,
    onNavigateToDetail: (MoviePreviewsItemData) -> Unit = { },
) {
    val pagingItems = viewModel.pagingItemsOfMoviePreviews
        .collectAsLazyPagingItems()

    ListMoviePreviewsScreen(
        modifier = Modifier,
        pagingItems = pagingItems,
        onItemClicked = onNavigateToDetail,
        selectedMovieGenreName = viewModel.selectedGenreName,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ListMoviePreviewsScreen(
    modifier: Modifier = Modifier,
    pagingItems: LazyPagingItems<MoviePreviewsItemData> = rememberDummyPagingItems(),
    onItemClicked: (MoviePreviewsItemData) -> Unit = { },
    selectedMovieGenreName: String
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    val focusManager = LocalFocusManager.current
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
                title = {},
                scrollBehavior = scrollBehavior,
                actions = {
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .background(Color.White)
                    ) {
                        Text(
                            modifier = Modifier
                                .align(Alignment.Center),
                            text = stringResource(R.string.discover_movies, selectedMovieGenreName),
                            style = MaterialTheme.typography.headlineSmall,
                            color = Black10
                        )
                    }
                }
            )

        }
    ) { innerPadding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            ListMoviePreviewsMainContent(
                pagingItems = pagingItems,
                onItemClicked = {
                    onItemClicked(it)
                    focusManager.clearFocus()
                },
            )
        }

    }
}

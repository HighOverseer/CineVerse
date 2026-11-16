package com.fajar.cineverse.movie_discovery.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.fajar.cineverse.core.domain.model.MoviePreviewsItemData
import com.fajar.cineverse.core.ui.theme.Black10
import com.fajar.cineverse.core.ui.utils.rememberDummyPagingItems
import com.fajar.cineverse.movie_discovery.presentation.contents.ListMoviePreviewsMainContent
import com.fajar.cineverse.movie_discovery.presentation.view_model.ListMoviePreviewsViewModel
import com.fajar.cineverse.core.ui.R as CoreR

@Composable
internal fun ListMoviePreviewsScreen(
    viewModel: ListMoviePreviewsViewModel,
    onNavigateToDetail: (MoviePreviewsItemData) -> Unit = { },
    onNavigateUp: () -> Unit = { }
) {
    val pagingItems = viewModel.pagingItemsOfMoviePreviews
        .collectAsLazyPagingItems()

    ListMoviePreviewsScreen(
        modifier = Modifier,
        pagingItems = pagingItems,
        onItemClicked = onNavigateToDetail,
        selectedMovieGenreName = viewModel.selectedGenreName,
        onNavigateUp = onNavigateUp
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ListMoviePreviewsScreen(
    modifier: Modifier = Modifier,
    pagingItems: LazyPagingItems<MoviePreviewsItemData> = rememberDummyPagingItems(),
    onItemClicked: (MoviePreviewsItemData) -> Unit = { },
    onNavigateUp: () -> Unit = { },
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
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateUp
                    ) {
                        Icon(
                            modifier = Modifier
                                .width(21.dp)
                                .height(20.dp),
                            imageVector = ImageVector
                                .vectorResource(CoreR.drawable.ic_arrow_left),
                            contentDescription = null,
                            tint = Black10
                        )
                    }
                },
                actions = {
                    Box(
                        Modifier
                            .fillMaxWidth()
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

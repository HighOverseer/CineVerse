package com.fajar.cineverse.movie_discovery.presentation.contents

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemKey
import com.fajar.cineverse.core.domain.model.MoviePreviewsItemData
import com.fajar.cineverse.core.ui.components.CurrentlyLoadingSection
import com.fajar.cineverse.core.ui.components.RetryLoadingSection
import com.fajar.cineverse.core.ui.utils.appendErrorMessage
import com.fajar.cineverse.core.ui.utils.isAppendLoadError
import com.fajar.cineverse.core.ui.utils.isAppending
import com.fajar.cineverse.core.ui.utils.isRefreshing
import com.fajar.cineverse.core.ui.utils.rememberDummyPagingItems
import com.fajar.cineverse.movie_discovery.presentation.components.MoviePreviewItemPosterCard
import com.fajar.cineverse.movie_discovery.presentation.components.ShimmeringMoviePreviewItemPosterCard
import com.fajar.cineverse.core.ui.R as CoreR

@Composable
internal fun ListMoviePreviewsSection(
    modifier: Modifier = Modifier,
    lazyGridState: LazyGridState = rememberLazyGridState(),
    pagingItems: LazyPagingItems<MoviePreviewsItemData> = rememberDummyPagingItems(),
    onItemClicked: (MoviePreviewsItemData) -> Unit = { },
) {
    LazyVerticalGrid(
        modifier = modifier,
        state = lazyGridState,
        contentPadding = PaddingValues(
            top = 16.dp,
            bottom = 24.dp,
            start = 16.dp,
            end = 16.dp
        ),
        columns = GridCells.Fixed(2),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        if (pagingItems.isRefreshing)
            items(10) { ShimmeringMoviePreviewItemPosterCard() }
        else {
            items(
                count = pagingItems.itemCount,
                key = pagingItems.itemKey { item -> item.id }
            ) { index ->
                val pagingItem = pagingItems[index]
                pagingItem?.let {
                    MoviePreviewItemPosterCard(
                        dui = pagingItem,
                        onClick = onItemClicked
                    )
                }
            }

            if (pagingItems.isAppending)
                item(span = { GridItemSpan(maxLineSpan) }) {
                    CurrentlyLoadingSection(
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            else if (pagingItems.isAppendLoadError)
                item(span = { GridItemSpan(maxLineSpan) }) {
                    RetryLoadingSection(
                        modifier = Modifier.fillMaxWidth(),
                        errorMessage = pagingItems.appendErrorMessage
                            ?: stringResource(CoreR.string.an_error_occurred_please_try_again),
                        onRetry = pagingItems::retry
                    )
                }
        }
    }
}

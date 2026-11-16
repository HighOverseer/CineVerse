package com.fajar.cineverse.movie_detail.presentation.content

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemKey
import com.fajar.cineverse.core.ui.components.CurrentlyLoadingSection
import com.fajar.cineverse.core.ui.components.RetryLoadingSection
import com.fajar.cineverse.core.ui.utils.appendErrorMessage
import com.fajar.cineverse.core.ui.utils.initialLoadErrorMessage
import com.fajar.cineverse.core.ui.utils.isAppendLoadError
import com.fajar.cineverse.core.ui.utils.isAppending
import com.fajar.cineverse.core.ui.utils.isInitialLoadError
import com.fajar.cineverse.core.ui.utils.isListEmpty
import com.fajar.cineverse.core.ui.utils.isRefreshing
import com.fajar.cineverse.core.ui.utils.rememberDummyPagingItems
import com.fajar.cineverse.movie_detail.presentation.components.MovieReviewItemCard
import com.fajar.cineverse.movie_detail.presentation.components.ShimmeringMovieReviewItemCard
import com.fajar.cineverse.movie_detail.presentation.model.MovieReviewDUI
import com.fajar.cineverse.core.ui.R as CoreR


@Composable
internal fun ListMovieReviewsSection(
    modifier: Modifier = Modifier,
    lazyListState: LazyListState = rememberLazyListState(),
    pagingItems: LazyPagingItems<MovieReviewDUI> = rememberDummyPagingItems(),
) {
    val defaultTextModifier = Modifier
        .padding(horizontal = 32.dp, vertical = 16.dp)

    if (pagingItems.isInitialLoadError)
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            RetryLoadingSection(
                errorMessage = pagingItems.appendErrorMessage
                    ?: stringResource(CoreR.string.an_error_occurred_please_try_again),
                onRetry = pagingItems::retry
            )
        }
    else if (pagingItems.isListEmpty)
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                modifier = defaultTextModifier,
                text = pagingItems.initialLoadErrorMessage
                    ?: stringResource(CoreR.string.sorry_no_data_found),
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center
            )
        }
    else {
        LazyColumn(
            modifier = modifier,
            state = lazyListState,
            contentPadding = PaddingValues(
                top = 16.dp,
                bottom = 24.dp,
                start = 16.dp,
                end = 16.dp
            ),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            if (pagingItems.isRefreshing)
                items(10) {
                    ShimmeringMovieReviewItemCard()
                }
            else {
                items(
                    count = pagingItems.itemCount,
                    key = pagingItems.itemKey { item -> item.id }
                ) { index ->
                    val pagingItem = pagingItems[index]
                    pagingItem?.let {
                        MovieReviewItemCard(
                            review = pagingItem,
                        )
                    }
                }

                if (pagingItems.isAppending)
                    item {
                        CurrentlyLoadingSection(
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                else if (pagingItems.isAppendLoadError)
                    item {
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
}
package com.fajar.cineverse.movie_discovery.presentation.contents

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults.Indicator
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import com.fajar.cineverse.core.domain.model.MoviePreviewsItemData
import com.fajar.cineverse.core.ui.theme.Grey90
import com.fajar.cineverse.core.ui.utils.initialLoadErrorMessage
import com.fajar.cineverse.core.ui.utils.isInitialLoadError
import com.fajar.cineverse.core.ui.utils.isListEmpty
import com.fajar.cineverse.core.ui.utils.rememberDummyPagingItems
import com.fajar.cineverse.movie_discovery.presentation.components.FullMaxSizeScrollableColumn
import com.fajar.cineverse.core.ui.R as CoreR

@Composable
internal fun ListMoviePreviewsMainContent(
    modifier: Modifier = Modifier,
    pagingItems: LazyPagingItems<MoviePreviewsItemData> = rememberDummyPagingItems(),
    onItemClicked: (MoviePreviewsItemData) -> Unit = { },
) {
    val lazyGridState = rememberLazyGridState()
    val pullToRefreshState = rememberPullToRefreshState()

    Column(
        modifier = modifier.fillMaxSize()
    ) {

        PullToRefreshBox(
            modifier = Modifier
                .fillMaxSize()
                .background(color = Grey90),
            contentAlignment = Alignment.TopCenter,
            isRefreshing = false,
            state = pullToRefreshState,
            onRefresh = { pagingItems.refresh() },
            indicator = {
                Indicator(
                    modifier = Modifier
                        .align(Alignment.TopCenter),
                    state = pullToRefreshState,
                    isRefreshing = false,
                    containerColor = Color.White,
                    color = Color.Black
                )
            }
        ) {
            val defaultTextModifier = Modifier
                .padding(horizontal = 32.dp, vertical = 16.dp)

            if (pagingItems.isInitialLoadError)
                FullMaxSizeScrollableColumn {
                    Text(
                        modifier = defaultTextModifier,
                        text = pagingItems.formattedInitialLoadErrorMessage,
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.Center
                    )
                }
            else if (pagingItems.isListEmpty)
                FullMaxSizeScrollableColumn {
                    Text(
                        modifier = defaultTextModifier,
                        text = pagingItems.initialLoadErrorMessage
                            ?: stringResource(CoreR.string.sorry_no_data_found),
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.Center
                    )
                }
            else
                ListMoviePreviewsSection(
                    lazyGridState = lazyGridState,
                    pagingItems = pagingItems,
                    onItemClicked = onItemClicked
                )
        }
    }
}

private val <T : Any> LazyPagingItems<T>.formattedInitialLoadErrorMessage: AnnotatedString
    @Composable get() {
        return buildAnnotatedString {
            val baseMessage = this@formattedInitialLoadErrorMessage
                .initialLoadErrorMessage
                ?: stringResource(CoreR.string.an_error_occurred_please_try_again)
            append(baseMessage)

            withStyle(
                style = SpanStyle(
                    fontWeight = FontWeight.SemiBold
                )
            ) {
                appendLine()
                append(stringResource(CoreR.string.please_pull_to_refresh_again))
            }
        }
    }
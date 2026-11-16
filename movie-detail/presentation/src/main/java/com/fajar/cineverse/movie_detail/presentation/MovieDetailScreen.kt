package com.fajar.cineverse.movie_detail.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.fajar.cineverse.core.domain.model.MovieDetailData
import com.fajar.cineverse.core.domain.model.Rating
import com.fajar.cineverse.core.ui.components.AnimatedScrollUpButton
import com.fajar.cineverse.core.ui.components.BaseAsyncImage
import com.fajar.cineverse.core.ui.components.CurrentlyLoadingSection
import com.fajar.cineverse.core.ui.components.GeneralDialog
import com.fajar.cineverse.core.ui.components.RetryLoadingSection
import com.fajar.cineverse.core.ui.theme.Black10
import com.fajar.cineverse.core.ui.theme.Black50
import com.fajar.cineverse.core.ui.theme.CineVerseAppTheme
import com.fajar.cineverse.core.ui.theme.Grey90
import com.fajar.cineverse.core.ui.utils.appendErrorMessage
import com.fajar.cineverse.core.ui.utils.formatDecimalPlaces
import com.fajar.cineverse.core.ui.utils.initialLoadErrorMessage
import com.fajar.cineverse.core.ui.utils.isAppendLoadError
import com.fajar.cineverse.core.ui.utils.isAppending
import com.fajar.cineverse.core.ui.utils.isInitialLoadError
import com.fajar.cineverse.core.ui.utils.isListEmpty
import com.fajar.cineverse.core.ui.utils.isRefreshing
import com.fajar.cineverse.core.ui.utils.rememberDummyPagingItems
import com.fajar.cineverse.movie_detail.presentation.components.MovieRatingSection
import com.fajar.cineverse.movie_detail.presentation.components.MovieReviewItemCard
import com.fajar.cineverse.movie_detail.presentation.components.ShimmeringMovieReviewItemCard
import com.fajar.cineverse.movie_detail.presentation.components.YoutubeVideoPlayer
import com.fajar.cineverse.movie_detail.presentation.components.rememberYoutubePlayerView
import com.fajar.cineverse.movie_detail.presentation.content.ShimmeringMovieDetailContent
import com.fajar.cineverse.movie_detail.presentation.model.MovieReviewDUI
import kotlinx.coroutines.launch
import com.fajar.cineverse.core.ui.R as CoreR

@Composable
internal fun MovieDetailScreen(
    viewModel: MovieDetailViewModel,
    onNavigateUp: () -> Unit = { }
) {
    LaunchedEffect(Unit) {
        viewModel.initializeIfNeeded()
    }

    val uiState by viewModel.uiState
        .collectAsStateWithLifecycle()
    val reviewPagingItems = viewModel.pagingItemsOfMovieReviews
        .collectAsLazyPagingItems()

    MovieDetailScreen(
        modifier = Modifier,
        uiState = uiState,
        reviewPagingItems = reviewPagingItems,
        onNavigateUp = onNavigateUp,
    )
}

@Composable
private fun MovieDetailScreen(
    modifier: Modifier = Modifier,
    uiState: MovieDetailUIState,
    reviewPagingItems: LazyPagingItems<MovieReviewDUI> = rememberDummyPagingItems(),
    onNavigateUp: () -> Unit = { },
) {
    val lazyListState = rememberLazyListState()
    val movieDetail = uiState.movieDetail
    val isScrollUpButtonVisible by remember {
        derivedStateOf {
            lazyListState.firstVisibleItemIndex > 0
        }
    }
    val coroutineScope = rememberCoroutineScope()

    uiState.errorUserMessage?.let { errorUserMessage ->
        GeneralDialog(
            title = stringResource(CoreR.string.error),
            message = errorUserMessage.getValue(),
            onClick = onNavigateUp
        )
    }

    val videoPlayerView = movieDetail?.associatedYTVideoKey?.let {
        rememberYoutubePlayerView(it)
    }

    Box(Modifier.fillMaxSize()) {
        LazyColumn(
            state = lazyListState,
            modifier = modifier
                .fillMaxSize()
                .background(color = Grey90),
        ) {
            item {
                Spacer(Modifier.height(32.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
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

                    Spacer(Modifier.width(16.dp))

                    Text(
                        stringResource(R.string.movie_detail),
                        style = MaterialTheme.typography.titleLarge,
                        color = Black10,
                        fontSize = 18.sp
                    )
                }

                Spacer(Modifier.height(16.dp))
            }

            if (uiState.isLoading || movieDetail == null) {
                item {
                    ShimmeringMovieDetailContent()
                }
            } else {
                item {
                    val imageAspectRatio = 1.652f
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .aspectRatio(imageAspectRatio),
                        contentAlignment = Alignment.Center
                    ) {
                        BaseAsyncImage(
                            modifier = Modifier,
                            imageUrlOrPath = uiState.movieDetail.imageUrl,
                            contentScale = ContentScale.FillBounds,
                            contentDescription = stringResource(R.string.movie_backdrop_image)
                        )
                    }
                    Spacer(Modifier.height(16.dp))
                }

                val commonComponentModifier = Modifier
                    .padding(start = 16.dp, end = 16.dp)

                item {
                    Text(
                        modifier = commonComponentModifier,
                        text = movieDetail.title,
                        style = MaterialTheme.typography.titleLarge,
                        color = Black10,
                        fontSize = 24.sp,
                        lineHeight = 24.sp
                    )

                    Spacer(Modifier.height(8.dp))
                }
                item {
                    movieDetail.rating?.let { rating ->
                        MovieRatingSection(
                            modifier = commonComponentModifier,
                            relativeScore = rating.normalizedRelativeScore,
                            maxScore = rating.maxScore,
                            displayRating = rating.normalizedRelativeScore.formatDecimalPlaces()
                        )
                    }

                    Spacer(Modifier.height(16.dp))
                }

                item {
                    Text(
                        modifier = commonComponentModifier,
                        textAlign = TextAlign.Justify,
                        text = movieDetail.overview,
                        style = MaterialTheme.typography.bodyLarge,
                        color = Black50
                    )

                    Spacer(Modifier.height(24.dp))
                }

                videoPlayerView?.let {
                    item {
                        YoutubeVideoPlayer(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            playerView = it
                        )

                        Spacer(Modifier.height(16.dp))
                    }
                }

                item {
                    Column(modifier = commonComponentModifier) {
                        Text(
                            text = stringResource(R.string.reviews),
                            style = MaterialTheme.typography.titleLarge,
                            color = Black10,
                            fontSize = 18.sp
                        )
                        Spacer(Modifier.height(8.dp))
                    }
                }

                if (reviewPagingItems.isInitialLoadError)
                    item {
                        Column(
                            modifier = commonComponentModifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            RetryLoadingSection(
                                errorMessage = reviewPagingItems.appendErrorMessage
                                    ?: stringResource(CoreR.string.an_error_occurred_please_try_again),
                                onRetry = reviewPagingItems::retry
                            )
                        }
                    }
                else if (reviewPagingItems.isListEmpty)
                    item {
                        Column(
                            modifier = commonComponentModifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            val defaultTextModifier = Modifier
                                .padding(horizontal = 32.dp, vertical = 16.dp)

                            Text(
                                modifier = defaultTextModifier,
                                text = reviewPagingItems.initialLoadErrorMessage
                                    ?: stringResource(CoreR.string.sorry_no_data_found),
                                style = MaterialTheme.typography.bodySmall,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                else {
                    if (reviewPagingItems.isRefreshing)
                        items(3) {
                            ShimmeringMovieReviewItemCard(modifier = commonComponentModifier)
                            Spacer(Modifier.height(8.dp))
                        }
                    else {
                        items(
                            count = reviewPagingItems.itemCount,
                            key = reviewPagingItems.itemKey { item -> item.id }
                        ) { index ->
                            val pagingItem = reviewPagingItems[index]
                            pagingItem?.let {
                                MovieReviewItemCard(
                                    modifier = commonComponentModifier,
                                    review = pagingItem,
                                )

                                Spacer(Modifier.height(4.dp))
                            }
                        }

                        if (reviewPagingItems.isAppending)
                            item {
                                CurrentlyLoadingSection(
                                    modifier = commonComponentModifier.fillMaxWidth()
                                )
                            }
                        else if (reviewPagingItems.isAppendLoadError)
                            item {
                                RetryLoadingSection(
                                    modifier = commonComponentModifier.fillMaxWidth(),
                                    errorMessage = reviewPagingItems.appendErrorMessage
                                        ?: stringResource(CoreR.string.an_error_occurred_please_try_again),
                                    onRetry = reviewPagingItems::retry
                                )
                            }
                    }
                }

                item {
                    Box(Modifier.height(48.dp))
                }
            }
        }

        Row(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(start = 16.dp, end = 24.dp, bottom = 48.dp),
        ) {
            AnimatedScrollUpButton(
                isScrollUpButtonVisibleProvider = { isScrollUpButtonVisible },
                onClick = {
                    coroutineScope.launch {
                        lazyListState.scrollToItem(0)
                    }
                }
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
private fun MovieDetailScreenPreview() {
    CineVerseAppTheme() {
        MovieDetailScreen(
            uiState = MovieDetailUIState(
                isLoading = false,
                errorUserMessage = null,
                movieDetail = MovieDetailData(
                    id = 1,
                    title = "Blade Runner 2049",
                    overview = "Lorem ipsum Lorem ipsum Lorem ipsum Lorem ipsum Lorem ipsum Lorem ipsum Lorem ipsum Lorem ipsum  Lorem ipsum Lorem ipsum",
                    rating = Rating(8.5, 10.0, null),
                    imageUrl = "image.com",
                    associatedYTVideoKey = null
                )
            )
        )
    }
}
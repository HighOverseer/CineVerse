package com.fajar.cineverse.movie_detail.presentation.content

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.paging.compose.LazyPagingItems
import com.fajar.cineverse.core.domain.model.MovieDetailData
import com.fajar.cineverse.core.ui.components.BaseAsyncImage
import com.fajar.cineverse.core.ui.theme.Black10
import com.fajar.cineverse.core.ui.theme.Black50
import com.fajar.cineverse.core.ui.utils.formatDecimalPlaces
import com.fajar.cineverse.core.ui.utils.rememberDummyPagingItems
import com.fajar.cineverse.movie_detail.presentation.R
import com.fajar.cineverse.movie_detail.presentation.components.MovieRatingSection
import com.fajar.cineverse.movie_detail.presentation.model.MovieReviewDUI

@Composable
internal fun MovieDetailMainContent(
    modifier: Modifier = Modifier,
    movieDetail: MovieDetailData,
    reviewPagingItems: LazyPagingItems<MovieReviewDUI> = rememberDummyPagingItems()
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val imageAspectRatio = 1.652f
        Box(
            Modifier
                .fillMaxWidth()
                .aspectRatio(imageAspectRatio),
            contentAlignment = Alignment.Center
        ) {
            BaseAsyncImage(
                modifier = Modifier,
                imageUrlOrPath = movieDetail.imageUrl,
                contentScale = ContentScale.FillBounds,
                contentDescription = stringResource(R.string.movie_backdrop_image)
            )
        }
        Spacer(Modifier.height(16.dp))

        Column(
            modifier = Modifier
                .padding(start = 16.dp, end = 16.dp, bottom = 40.25.dp)

        ) {
            Text(
                movieDetail.title,
                style = MaterialTheme.typography.titleLarge,
                color = Black10,
                fontSize = 24.sp,
                lineHeight = 24.sp
            )

            Spacer(Modifier.height(8.dp))

            movieDetail.rating?.let { rating ->
                MovieRatingSection(
                    relativeScore = rating.normalizedRelativeScore,
                    maxScore = rating.maxScore,
                    displayRating = rating.normalizedRelativeScore.formatDecimalPlaces()
                )
            }

            Spacer(Modifier.height(16.dp))

            Text(
                textAlign = TextAlign.Justify,
                text = movieDetail.overview,
                style = MaterialTheme.typography.bodyLarge,
                color = Black50
            )

            Spacer(Modifier.height(24.dp))

            Text(
                text = stringResource(R.string.reviews),
                style = MaterialTheme.typography.titleLarge,
                color = Black10,
                fontSize = 18.sp
            )
            Spacer(Modifier.height(8.dp))

            ListMovieReviewsSection(pagingItems = reviewPagingItems)
        }
    }
}
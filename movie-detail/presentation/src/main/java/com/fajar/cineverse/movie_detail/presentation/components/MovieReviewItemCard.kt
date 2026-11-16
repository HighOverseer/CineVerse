package com.fajar.cineverse.movie_detail.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.fajar.cineverse.core.ui.theme.Black10
import com.fajar.cineverse.core.ui.theme.CineVerseAppTheme
import com.fajar.cineverse.core.ui.theme.Grey65
import com.fajar.cineverse.core.ui.utils.shimmeringEffect
import com.fajar.cineverse.movie_detail.presentation.model.MovieReviewDUI

@Composable
internal fun MovieReviewItemCard(
    modifier: Modifier = Modifier,
    review: MovieReviewDUI
) {
    Card(
        modifier = modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White,
            contentColor = Color.White
        ),
        onClick = { }
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    review.reviewerUsername,
                    style = MaterialTheme.typography.titleMedium,
                    color = Black10,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    review.displayPostedDate,
                    style = MaterialTheme.typography.labelMedium,
                    color = Grey65
                )
            }

            Spacer(
                Modifier.height(8.dp)
            )

            Text(
                text = "\"${review.comment}\"",
                style = MaterialTheme.typography.labelMedium,
                color = Black10,
                fontStyle = FontStyle.Italic
            )
        }
    }
    Spacer(Modifier.height(12.dp))
}

@Composable
internal fun ShimmeringMovieReviewItemCard(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.White, shape = RoundedCornerShape(8.dp))
            .padding(16.dp)
    ) {
        Box(
            Modifier
                .fillMaxWidth(0.2f)
                .height(12.dp)
                .clip(RoundedCornerShape(2.dp))
                .shimmeringEffect()
        )

        Spacer(Modifier.height(8.dp))

        Box(
            Modifier
                .fillMaxWidth()
                .height(12.dp)
                .clip(RoundedCornerShape(2.dp))
                .shimmeringEffect()
        )

        Spacer(Modifier.height(4.dp))

        Box(
            Modifier
                .fillMaxWidth(0.5f)
                .height(12.dp)
                .clip(RoundedCornerShape(2.dp))
                .shimmeringEffect()
        )
    }
}

@Preview
@Composable
private fun MovieReviewItemCardPreview() {
    CineVerseAppTheme {
        MovieReviewItemCard(
            review = MovieReviewDUI(
                id = "12312jnfkf",
                comment = "Lorem ipsum Lorem ipsum Lorem ipsum Lorem ipsum Lorem ipsum Lorem ipsum",
                displayPostedDate = "20 July 2026",
                reviewerUsername = "Fajar"
            )
        )
    }
}

@Preview
@Composable
private fun ShimmeringMovieReviewItemCardPreview() {
    CineVerseAppTheme {
        ShimmeringMovieReviewItemCard()
    }
}
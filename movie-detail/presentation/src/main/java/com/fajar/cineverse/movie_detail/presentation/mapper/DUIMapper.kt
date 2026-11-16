package com.fajar.cineverse.movie_detail.presentation.mapper

import com.fajar.cineverse.core.domain.model.MovieReviewData
import com.fajar.cineverse.core.ui.utils.formatter.DefaultDUIDateFormatter
import com.fajar.cineverse.movie_detail.presentation.model.MovieReviewDUI

internal class DUIMapper(
    private val duiDateFormatter: DefaultDUIDateFormatter
) {
    fun mapReviewToDUI(
        movieReviewData: MovieReviewData
    ): MovieReviewDUI {
        movieReviewData.run {
            return MovieReviewDUI(
                id = id,
                comment = comment,
                displayPostedDate = duiDateFormatter
                    .formatToDisplayDate(postedTimestamp),
                reviewerUsername = reviewerUsername,
            )
        }
    }
}
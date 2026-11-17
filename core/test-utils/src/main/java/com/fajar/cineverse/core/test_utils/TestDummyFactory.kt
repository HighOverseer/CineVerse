package com.fajar.cineverse.core.test_utils

import com.fajar.cineverse.core.domain.model.MovieDetailData
import com.fajar.cineverse.core.domain.model.MovieGenre
import com.fajar.cineverse.core.domain.model.MoviePreviewsItemData
import com.fajar.cineverse.core.domain.model.MovieReviewData
import com.fajar.cineverse.core.domain.model.Page
import com.fajar.cineverse.core.domain.model.Rating

object TestDummyFactory {

    fun createDummyMovieGenre(
        id: Long = 28L,
        name: String = "Mystery"
    ): MovieGenre = MovieGenre(
        id = id,
        name = name
    )

    fun createDummyMovieGenres(): List<MovieGenre> = listOf(
        MovieGenre(id = 28L, name = "Mystery"),
        MovieGenre(id = 12L, name = "Thrill"),
        MovieGenre(id = 16L, name = "Horror")
    )

    fun createDummyRating(
        relativeScore: Double = 8.5,
        maxScore: Double = 10.0,
        count: Int? = 1500
    ): Rating = Rating(
        relativeScore = relativeScore,
        maxScore = maxScore,
        count = count
    )

    fun createDummyMoviePreviewsItem(
        id: Long = 101L,
        title: String = "Blade Runner 2049",
        thumbnailImageUrl: String = "https://image/poster.jpg",
        rating: Rating? = createDummyRating()
    ): MoviePreviewsItemData = MoviePreviewsItemData(
        id = id,
        title = title,
        thumbnailImageUrl = thumbnailImageUrl,
        rating = rating
    )

    fun createDummyMoviePreviewsList(): List<MoviePreviewsItemData> = listOf(
        createDummyMoviePreviewsItem(id = 101L, title = "Blade Runner 2049"),
        createDummyMoviePreviewsItem(id = 102L, title = "Interstellar"),
        createDummyMoviePreviewsItem(id = 103L, title = "The Dark Knight")
    )

    fun createDummyMovieDetailData(
        id: Long = 101L,
        title: String = "Blade Runner 2049",
        imageUrl: String = "https://image/backdrop.jpg",
        overview: String = "Lorem Ipsum Lorem Ipsum Lorem Ipsum Lorem Ipsum Lorem Ipsum...",
        rating: Rating? = createDummyRating(),
        associatedYTVideoKey: String? = "1235jkfsd"
    ): MovieDetailData = MovieDetailData(
        id = id,
        title = title,
        imageUrl = imageUrl,
        overview = overview,
        rating = rating,
        associatedYTVideoKey = associatedYTVideoKey
    )

    fun createDummyMovieReviewData(
        id: String = "rev_1",
        comment: String = "Cool movie",
        postedTimestamp: Long = 1600000000000L,
        reviewerUsername: String = "Fajar"
    ): MovieReviewData = MovieReviewData(
        id = id,
        comment = comment,
        postedTimestamp = postedTimestamp,
        reviewerUsername = reviewerUsername
    )

    fun createDummyMovieReviewsList(): List<MovieReviewData> = listOf(
        createDummyMovieReviewData(id = "rev_1", comment = "Great plot twist!"),
        createDummyMovieReviewData(id = "rev_2", comment = "Masterpiece by Nolan")
    )

    fun <T> createDummyPage(
        pagedList: List<T>,
        hasNext: Boolean = false
    ): Page<T> = Page(
        pagedList = pagedList,
        hasNext = hasNext
    )
}

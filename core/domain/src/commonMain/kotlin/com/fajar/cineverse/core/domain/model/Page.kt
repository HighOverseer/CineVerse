package com.fajar.cineverse.core.domain.model

/**
 * Represents each individual Paged Item of Pagination
 */
data class Page<T>(
    val pagedList: List<T>,
    val hasNext: Boolean
)
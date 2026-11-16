package com.fajar.cineverse.core.data.datasource.remote.implementation.network.mapper

class MovieImageUrlResolver(
    private val baseImageUrl: String
) {
    fun resolve(
        path: String?
    ): String? {
        return path?.let { "${baseImageUrl}$it" }
    }
}
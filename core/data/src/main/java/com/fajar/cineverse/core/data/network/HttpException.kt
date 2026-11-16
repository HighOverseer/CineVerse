package com.fajar.cineverse.core.data.network

internal class HttpException(
    val statusCode: Int
) : Exception()
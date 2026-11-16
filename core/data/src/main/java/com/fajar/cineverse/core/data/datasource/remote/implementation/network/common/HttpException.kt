package com.fajar.cineverse.core.data.datasource.remote.implementation.network.common

internal class HttpException(
    val statusCode: Int
) : Exception()
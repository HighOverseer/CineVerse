package com.fajar.cineverse.core.data.dependency_injection

import com.fajar.cineverse.core.data.BuildConfig
import com.fajar.cineverse.core.data.datasource.remote.implementation.network.mapper.MapperToDomain
import com.fajar.cineverse.core.data.datasource.remote.implementation.network.mapper.MovieImageUrlResolver
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object MapperModule {

    @Provides
    @Singleton
    fun provideRemoteDataMapperToDomain(
        movieImageUrlResolver: MovieImageUrlResolver
    ): MapperToDomain =
        MapperToDomain(movieImageUrlResolver)

    @Provides
    @Singleton
    fun provideMovieImageResolver(): MovieImageUrlResolver {
        return MovieImageUrlResolver(baseImageUrl = BuildConfig.BASE_IMAGE_URL)
    }

}
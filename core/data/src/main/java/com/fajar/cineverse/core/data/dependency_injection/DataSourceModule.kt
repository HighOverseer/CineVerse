package com.fajar.cineverse.core.data.dependency_injection

import com.fajar.cineverse.core.data.datasource.remote.MovieRemoteDataSource
import com.fajar.cineverse.core.data.datasource.remote.implementation.MovieRemoteDataSourceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal abstract class DataSourceModule {

    @Binds
    abstract fun bindMovieRemoteDataSource(
        remoteDataSourceImpl: MovieRemoteDataSourceImpl
    ): MovieRemoteDataSource

}
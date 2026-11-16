package com.fajar.cineverse.core.data.dependency_injection

import com.fajar.cineverse.core.data.datasource.remote.implementation.network.mapper.MapperToDomain
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
    fun provideRemoteDataMapperToDomain(): MapperToDomain =
        MapperToDomain()

}
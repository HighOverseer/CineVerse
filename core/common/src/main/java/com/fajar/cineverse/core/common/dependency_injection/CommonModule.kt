package com.fajar.cineverse.core.common.dependency_injection

import com.fajar.cineverse.core.common.DispatcherProvider
import com.fajar.cineverse.core.common.DispatcherProviderImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal object CommonModule {
    @Provides
    fun provideDispatcherProvider(): DispatcherProvider =
        DispatcherProviderImpl
}
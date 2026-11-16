package com.fajar.cineverse.movie_detail.presentation.dependency_injection

import com.fajar.cineverse.core.ui.utils.formatter.DefaultDUIDateFormatter
import com.fajar.cineverse.movie_detail.presentation.mapper.DUIMapper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
internal object MapperModule {

    @Provides
    @ViewModelScoped
    fun provideDUIMapper(
        duiDateFormatter: DefaultDUIDateFormatter
    ): DUIMapper = DUIMapper(duiDateFormatter = duiDateFormatter)
}
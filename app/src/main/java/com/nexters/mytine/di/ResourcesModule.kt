package com.nexters.mytine.di

import com.nexters.mytine.utils.ResourcesProvider
import com.nexters.mytine.utils.ResourcesProviderImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal interface ResourcesModule {
    @Binds
    @Singleton
    fun bindResourcesProvider(resourcesProvider: ResourcesProviderImpl): ResourcesProvider
}

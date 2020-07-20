package com.nexters.mytine.di

import com.nexters.mytine.data.repository.RoutineRepository
import com.nexters.mytine.data.repository.RoutineRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.scopes.ActivityRetainedScoped

@Module
@InstallIn(ActivityRetainedComponent::class)
internal interface RepositoryModule {
    @Binds
    @ActivityRetainedScoped
    fun bindRoutineRepository(routineRepositoryImpl: RoutineRepositoryImpl): RoutineRepository
}

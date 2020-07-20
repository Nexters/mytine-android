package com.nexters.mytine.di

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.nexters.mytine.data.MyTineRoomDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal class TestDatabaseModule {
    @Provides
    @Singleton
    fun provideMyTineRoomDatabase(): MyTineRoomDatabase {
        return Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            MyTineRoomDatabase::class.java
        )
            .allowMainThreadQueries()
            .build()
    }
}

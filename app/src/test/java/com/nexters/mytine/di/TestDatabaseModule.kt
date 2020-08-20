package com.nexters.mytine.di

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.nexters.mytine.data.MyTineRoomDatabase
import com.nexters.mytine.data.dao.RoutineDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import java.util.concurrent.Executors
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
            .setTransactionExecutor(Executors.newSingleThreadExecutor())
            .build()
    }

    @Provides
    @Singleton
    fun provideRoutineDao(roomDatabase: MyTineRoomDatabase): RoutineDao = roomDatabase.routineDao()

    @Provides
    @Singleton
    fun provideSharedPreference(app: Application): SharedPreferences = app.getSharedPreferences("is_visited", Context.MODE_PRIVATE)
}

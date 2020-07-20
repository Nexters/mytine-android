package com.nexters.mytine.di

import android.content.Context
import androidx.room.Room
import com.nexters.mytine.data.MyTineRoomDatabase
import com.nexters.mytine.data.migration.MIGRAITONS
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal class DatabaseModule {
    companion object {
        private const val DB_NAME = "mytine_db"
    }

    @Provides
    @Singleton
    @SuppressWarnings("SpreadOperator")
    fun provideRoomDataBase(@ApplicationContext context: Context): MyTineRoomDatabase {
        return Room.databaseBuilder(context, MyTineRoomDatabase::class.java, DB_NAME)
            .addMigrations(*MIGRAITONS)
            .build()
    }
}

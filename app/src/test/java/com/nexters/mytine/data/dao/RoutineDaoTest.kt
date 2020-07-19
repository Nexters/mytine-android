package com.nexters.mytine.data.dao

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth.assertThat
import com.nexters.mytine.data.MyTineRoomDatabase
import com.nexters.mytine.data.entity.Routine
import com.nexters.mytine.di.DatabaseModule
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import javax.inject.Inject

@HiltAndroidTest
@UninstallModules(DatabaseModule::class)
@RunWith(RobolectricTestRunner::class)
internal class RoutineDaoTest {
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var database: MyTineRoomDatabase

    private lateinit var dao: RoutineDao

    @Before
    fun setup() {
        hiltRule.inject()

        dao = database.routineDao()
    }

    @After
    fun teardown() {
        database.close()
    }

    @Test
    fun routine_gets_test() = runBlocking {
        assertThat(dao.gets().first()).isEmpty()
        val routine = Routine(id = 1, title = "title", content = "content")
        dao.upsert(routine)
        assertThat(dao.gets().first()).isEqualTo(listOf(routine))
    }
}

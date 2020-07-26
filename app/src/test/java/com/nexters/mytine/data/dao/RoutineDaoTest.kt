package com.nexters.mytine.data.dao

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth.assertThat
import com.nexters.mytine.MainCoroutinesRule
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
import java.time.DayOfWeek
import java.time.LocalDate
import javax.inject.Inject

@HiltAndroidTest
@UninstallModules(DatabaseModule::class)
@RunWith(RobolectricTestRunner::class)
internal class RoutineDaoTest {
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val couroutinesRule = MainCoroutinesRule()

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var database: MyTineRoomDatabase

    private lateinit var dao: RoutineDao

    private lateinit var routine: Routine

    @Before
    fun setup() {
        hiltRule.inject()

        dao = database.routineDao()

        routine = Routine(date = LocalDate.now(), emoji = "emoji", name = "name", goal = "goal", status = Routine.Status.ENABLE)
    }

    @After
    fun teardown() {
        database.close()
    }

    @Test
    fun `gets 테스트`() = runBlocking {
        assertThat(dao.gets().first()).isEmpty()
        val routine = routine
        dao.upsert(routine)
        assertThat(dao.gets().first()).isEqualTo(listOf(routine))
    }

    @Test
    fun `getsSync 테스트`() = runBlocking {
        assertThat(dao.getsSync()).isEmpty()
        val routine = routine
        dao.upsert(routine)
        assertThat(dao.getsSync()).isEqualTo(listOf(routine))
    }

    @Test
    fun `deleteAndUpdate 테스트`() = runBlocking {
        val routineMon = routine.copy(date = routine.date.with(DayOfWeek.MONDAY))
        val routineTue = routine.copy(date = routine.date.with(DayOfWeek.TUESDAY))
        val routineWed = routine.copy(date = routine.date.with(DayOfWeek.WEDNESDAY))
        val routineThu = routine.copy(date = routine.date.with(DayOfWeek.THURSDAY))
        val routineFri = routine.copy(date = routine.date.with(DayOfWeek.FRIDAY))
        val routineSat = routine.copy(date = routine.date.with(DayOfWeek.SATURDAY))
        val routineSun = routine.copy(date = routine.date.with(DayOfWeek.SUNDAY))

        val routines = listOf(routineMon, routineTue, routineWed, routineThu, routineFri, routineSat, routineSun)

        dao.upserts(routines)
        assertThat(dao.getsSync()).isEqualTo(routines)

        val newRoutines = routines.mapIndexed { index, routine ->
            routine.copy(status = Routine.Status.values()[index % Routine.Status.values().size])
        }

        dao.deleteAndUpdate(newRoutines)
        assertThat(dao.getsSync()).isEqualTo(newRoutines)
    }
}

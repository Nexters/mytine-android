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
    val coroutinesRule = MainCoroutinesRule()

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

        routine = Routine(date = LocalDate.now(), emoji = "emoji", name = "name", goal = "goal", status = Routine.Status.ENABLE, order = 0)
    }

    @After
    fun teardown() {
        database.close()
    }

    @Test
    fun `flowRoutines 테스트`() = runBlocking {
        assertThat(dao.flowRoutines(routine.date).first()).isEmpty()
        dao.upsert(routine)
        assertThat(dao.flowRoutines(routine.date).first()).isEqualTo(listOf(routine))
    }

    @Test
    fun `gets 테스트`() = runBlocking {
        assertThat(dao.gets()).isEmpty()
        val routine = routine
        dao.upsert(routine)
        assertThat(dao.gets()).isEqualTo(listOf(routine))
    }

    @Test
    fun `getsById 테스트`() = runBlocking {
        val routineAMon = routine.copy(name = "A", date = routine.date.with(DayOfWeek.MONDAY))
        val routineATue = routine.copy(name = "A", date = routine.date.with(DayOfWeek.TUESDAY))

        val routineBMon = routine.copy(name = "B", date = routine.date.with(DayOfWeek.MONDAY))
        val routineBTue = routine.copy(name = "B", date = routine.date.with(DayOfWeek.TUESDAY))

        dao.upserts(listOf(routineAMon, routineATue, routineBMon, routineBTue))

        assertThat(dao.getsById(routineAMon.id)).isEqualTo(listOf(routineAMon, routineATue))
        assertThat(dao.getsById(routineATue.id)).isEqualTo(listOf(routineAMon, routineATue))

        assertThat(dao.getsById(routineBMon.id)).isEqualTo(listOf(routineBMon, routineBTue))
        assertThat(dao.getsById(routineBTue.id)).isEqualTo(listOf(routineBMon, routineBTue))
    }

    @Test
    fun `getsByDate 테스트`() = runBlocking {
        val routineMon = routine.copy(date = routine.date.with(DayOfWeek.MONDAY))
        val routineTue = routine.copy(date = routine.date.with(DayOfWeek.TUESDAY))
        val routineWed = routine.copy(date = routine.date.with(DayOfWeek.WEDNESDAY))
        val routineThu = routine.copy(date = routine.date.with(DayOfWeek.THURSDAY))
        val routineFri = routine.copy(date = routine.date.with(DayOfWeek.FRIDAY))
        val routineSat = routine.copy(date = routine.date.with(DayOfWeek.SATURDAY))
        val routineSun = routine.copy(date = routine.date.with(DayOfWeek.SUNDAY))

        val routines = listOf(routineMon, routineTue, routineWed, routineThu, routineFri, routineSat, routineSun)
        dao.upserts(routines)

        assertThat(dao.getsByDate(routine.date.with(DayOfWeek.MONDAY), routine.date.with(DayOfWeek.SUNDAY))).isEqualTo(listOf(routineMon, routineTue, routineWed, routineThu, routineFri, routineSat, routineSun))
        assertThat(dao.getsByDate(routine.date.with(DayOfWeek.MONDAY), routine.date.with(DayOfWeek.WEDNESDAY))).isEqualTo(listOf(routineMon, routineTue, routineWed))
        assertThat(dao.getsByDate(routine.date.with(DayOfWeek.TUESDAY), routine.date.with(DayOfWeek.THURSDAY))).isEqualTo(listOf(routineTue, routineWed, routineThu))
        assertThat(dao.getsByDate(routine.date.with(DayOfWeek.FRIDAY), routine.date.with(DayOfWeek.SUNDAY))).isEqualTo(listOf(routineFri, routineSat, routineSun))
    }

    @Test
    fun `getStartDate 테스트`() = runBlocking {
        assertThat(dao.getStartDate()).isNull()
        val routine = routine
        dao.upsert(routine)
        assertThat(dao.getStartDate()).isEqualTo(routine.date)
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

        assertThat(dao.gets()).isEqualTo(routines)

        val newRoutines = routines.mapIndexed { index, routine ->
            routine.copy(status = Routine.Status.values()[index % Routine.Status.values().size])
        }

        dao.deleteAndUpdate(routine.id, newRoutines)
        assertThat(dao.gets()).isEqualTo(newRoutines)
    }
}

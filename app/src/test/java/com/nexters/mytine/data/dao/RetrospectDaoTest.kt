package com.nexters.mytine.data.dao

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth.assertThat
import com.nexters.mytine.MainCoroutinesRule
import com.nexters.mytine.data.MyTineRoomDatabase
import com.nexters.mytine.data.entity.Retrospect
import com.nexters.mytine.di.DatabaseModule
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.time.LocalDate
import javax.inject.Inject

@HiltAndroidTest
@UninstallModules(DatabaseModule::class)
@RunWith(RobolectricTestRunner::class)
internal class RetrospectDaoTest {
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val coroutinesRule = MainCoroutinesRule()

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var database: MyTineRoomDatabase

    private lateinit var dao: RetrospectDao

    private lateinit var retrospect: Retrospect

    @Before
    fun setup() {
        hiltRule.inject()

        dao = database.retrospectDao()

        retrospect = Retrospect(date = LocalDate.now(), contents = "오늘의 회고.. 어제 막걸리를 먹어서 일찍 잠에 들었다 그래서 그런지 오늘 눈이 일찍 떠졌다. 일어나자마자 알고리즘 문제를 풀겠다고 다짐했었는데... 실패했다ㅠㅠ 다음주는 무조건 성공하고 말것이다! 나의 미래가 달려있다!!! 승희 파이팅 ㅜㅜㅜ")
    }

    @After
    fun teardown() {
        database.close()
    }

    @Test
    fun `get 테스트`() = runBlocking {
        assertThat(dao.get(LocalDate.now())).isNull()
        val retrospect = retrospect
        dao.upsert(retrospect)
        assertThat(dao.get(LocalDate.now())).isEqualTo(retrospect)
    }
}

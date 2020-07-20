package com.nexters.mytine.ui.home

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.nexters.mytine.base.viewmodel.BaseViewModel
import com.nexters.mytine.data.entity.Routine
import com.nexters.mytine.data.repository.RoutineRepository
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

internal class HomeViewModel @ViewModelInject constructor(
    private val routineRepository: RoutineRepository
) : BaseViewModel() {
    val homeItems = MutableLiveData<List<HomeItems>>()

    init {
        viewModelScope.launch {
            routineRepository.getRoutines()
                .collect { homeItems.value = createHomeItems(it) }
        }
    }

    fun onClickWrite() {
        navDirections.value = HomeFragmentDirections.actionHomeFragmentToWriteFragment()
    }

    private fun createHomeItems(routines: List<Routine>): List<HomeItems> {
        return routines.map { HomeItems.RoutineItem(it) }
    }
}

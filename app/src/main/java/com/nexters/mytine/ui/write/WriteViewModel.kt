package com.nexters.mytine.ui.write

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.nexters.mytine.base.viewmodel.BaseViewModel
import com.nexters.mytine.data.entity.Routine
import com.nexters.mytine.data.repository.RoutineRepository
import com.nexters.mytine.utils.navigation.BackDirections
import kotlinx.coroutines.launch

internal class WriteViewModel @ViewModelInject constructor(
    private val routineRepository: RoutineRepository
) : BaseViewModel() {
    val title = MutableLiveData<String>()
    val content = MutableLiveData<String>()

    fun onClickWrite() {
        val title = title.value
        val content = content.value

        if (title.isNullOrBlank() || content.isNullOrBlank()) {
            return
        }

        viewModelScope.launch {
            routineRepository.updateRoutine(Routine(title = title, content = content))
        }

        navDirections.value = BackDirections()
    }
}

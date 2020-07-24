package com.nexters.mytine.ui.home.routine

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.nexters.mytine.R
import com.nexters.mytine.base.fragment.BaseFragment
import com.nexters.mytine.databinding.FragmentRoutineBinding

internal class RoutineFragment : BaseFragment<FragmentRoutineBinding, RoutineViewModel>() {
    override val layoutResId = R.layout.fragment_routine
    override val viewModelClass = RoutineViewModel::class

    private val routineAdapter = RoutineAdapter()

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        initializeRecyclerView()
    }

    private fun initializeRecyclerView() {
        binding.rvRoutine.run {
            layoutManager = LinearLayoutManager(context)
            adapter = routineAdapter
        }

        routineAdapter.setViewModel(viewModel)
        routineAdapter.submitList(createRoutineItems(tempData()))
    }

    private fun tempData(): List<Routine> {
        return listOf(
            Routine(0, "루키케어", ""),
            Routine(0, "루키케어", ""),
            Routine(0, "루키케어", ""),
            Routine(0, "루키케어", "")
        )
    }

    private fun createRoutineItems(routines: List<Routine>): List<RoutineItems> {
        return routines.map { RoutineItems.RoutineItem(it) }
    }
}

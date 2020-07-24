package com.nexters.mytine.ui.home.routine

import com.nexters.mytine.R
import com.nexters.mytine.base.fragment.BaseFragment
import com.nexters.mytine.databinding.FragmentRoutineBinding

internal class RoutineFragment : BaseFragment<FragmentRoutineBinding, RoutineViewModel>() {
    override val layoutResId = R.layout.fragment_routine
    override val viewModelClass = RoutineViewModel::class
}
package com.nexters.mytine.ui.home

import com.nexters.mytine.R
import com.nexters.mytine.base.BaseFragment
import com.nexters.mytine.databinding.FragmentHomeBinding

internal class HomeFragment : BaseFragment<FragmentHomeBinding, HomeViewModel>() {
    override val layoutResId = R.layout.fragment_home
    override val viewModelClass = HomeViewModel::class
}

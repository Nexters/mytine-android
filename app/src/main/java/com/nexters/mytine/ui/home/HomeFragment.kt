package com.nexters.mytine.ui.home

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.tabs.TabLayoutMediator
import com.nexters.mytine.R
import com.nexters.mytine.base.fragment.BaseFragment
import com.nexters.mytine.databinding.FragmentHomeBinding
import com.nexters.mytine.utils.extensions.observe
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
internal class HomeFragment : BaseFragment<FragmentHomeBinding, HomeViewModel>() {
    override val layoutResId = R.layout.fragment_home
    override val viewModelClass = HomeViewModel::class

    private val homeAdapter = HomeAdapter()

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        initializeRecyclerView()
        setViewPager()

        observe(viewModel.homeItems) { homeAdapter.submitList(it) }
    }

    private fun initializeRecyclerView() {
        binding.rvRoutine.run {
            layoutManager = LinearLayoutManager(context)
            adapter = homeAdapter
        }

        homeAdapter.setViewModel(viewModel)
    }

    private fun setViewPager() {

        val homePagerAdapter = HomePagerAdapter(this)
        val routine = listOf("루틴", "회고")

        binding.viewPager.run {
            adapter = homePagerAdapter
        }

        TabLayoutMediator(binding.tabs, binding.viewPager) { tab, position ->
            tab.text = routine[position]
        }.attach()
    }
}

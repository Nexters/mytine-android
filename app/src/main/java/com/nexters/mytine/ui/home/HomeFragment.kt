package com.nexters.mytine.ui.home

import android.os.Bundle
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
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

        observe(viewModel.homeItems) { homeAdapter.submitList(it) }
    }

    private fun initializeRecyclerView() {
        binding.rvRoutine.run {
            layoutManager = LinearLayoutManager(context)
            adapter = homeAdapter
        }

        val itemTouchHelper = ItemTouchHelper(
            ItemTouchHelperCallback(object : ItemTouchHelperListener {
                override fun onItemSwipe(position: Int, direction: Int) {
                    viewModel.successRoutine(homeAdapter.getItemByPosition(position) as HomeItems.RoutineItem, direction)
                }
            })
        )
        itemTouchHelper.attachToRecyclerView(binding.rvRoutine)
        homeAdapter.setViewHolderViewModel(viewModel)
    }
}

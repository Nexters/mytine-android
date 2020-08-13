package com.nexters.mytine.ui.home

import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.nexters.mytine.R
import com.nexters.mytine.base.fragment.BaseFragment
import com.nexters.mytine.databinding.FragmentHomeBinding
import com.nexters.mytine.ui.home.icongroup.IconGroupAdapter
import com.nexters.mytine.ui.home.week.WeekAdapter
import com.nexters.mytine.utils.extensions.observe
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
internal class HomeFragment : BaseFragment<FragmentHomeBinding, HomeViewModel>() {
    companion object {
        const val SPAN_SIZE = 7
    }

    override val layoutResId = R.layout.fragment_home
    override val viewModelClass = HomeViewModel::class

    private val weekAdapter = WeekAdapter()
    private val iconGroupAdapter = IconGroupAdapter()
    private val homeAdapter = HomeAdapter()

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        initializeRecyclerView()

        observe(viewModel.weekItems) { weekAdapter.submitList(it) }
        observe(viewModel.iconGroupItems) { iconGroupAdapter.submitList(it) }
        observe(viewModel.homeItems) { homeAdapter.submitList(it) }
    }

    private fun initializeRecyclerView() {
        binding.spinner.run {
            adapter = DateSpinnerAdapter(context, viewModel)
        }

        binding.rvWeek.run {
            layoutManager = FlexboxLayoutManager(context, FlexDirection.ROW, FlexWrap.NOWRAP).apply {
                justifyContent = JustifyContent.SPACE_BETWEEN
            }
            adapter = weekAdapter
        }
        weekAdapter.setViewHolderViewModel(viewModel)

        binding.rvIconGroup.run {
            layoutManager = GridLayoutManager(context, SPAN_SIZE)
            adapter = iconGroupAdapter
        }
        iconGroupAdapter.setViewHolderViewModel(viewModel)

        binding.rvRoutine.run {
            layoutManager = LinearLayoutManager(context)
            adapter = homeAdapter
        }

        val itemTouchHelper = ItemTouchHelper(
            ItemTouchHelperCallback(object : ItemTouchHelperListener {
                override fun onItemSwipe(position: Int, direction: Int) {
                    viewModel.swipeRoutine(homeAdapter.getItemByPosition(position), direction)
                }

                override fun isRightSwipeable(position: Int): Boolean {
                    return homeAdapter.getItemByPosition(position) is HomeItems.RoutineItem.CompletedRoutineItem
                }

                override fun isLeftSwipeable(position: Int): Boolean {
                    return homeAdapter.getItemByPosition(position) is HomeItems.RoutineItem.EnabledRoutineItem
                }
            })
        )
        itemTouchHelper.attachToRecyclerView(binding.rvRoutine)
        homeAdapter.setViewHolderViewModel(viewModel)
    }
}

package com.nexters.mytine.ui.home

import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.Spinner
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.google.android.material.appbar.AppBarLayout
import com.nexters.mytine.R
import com.nexters.mytine.base.fragment.BaseFragment
import com.nexters.mytine.databinding.FragmentHomeBinding
import com.nexters.mytine.ui.home.icongroup.IconGroupAdapter
import com.nexters.mytine.ui.home.week.WeekAdapter
import com.nexters.mytine.ui.home.weekofmonth.WeekOfMonthMenu
import com.nexters.mytine.ui.home.weekrate.WeekRateAdapter
import com.nexters.mytine.utils.extensions.observe
import dagger.hilt.android.AndroidEntryPoint
import java.lang.reflect.Method
import java.time.LocalDate

@AndroidEntryPoint
internal class HomeFragment : BaseFragment<FragmentHomeBinding, HomeViewModel>() {
    companion object {
        const val SPAN_SIZE = 7
        const val WEEK_OF_MONTH_OFFSET_X = 0
        const val WEEK_OF_MONTH_OFFSET_Y = 16
    }

    override val layoutResId = R.layout.fragment_home
    override val viewModelClass = HomeViewModel::class

    private val weekRateAdapter = WeekRateAdapter()
    private val weekAdapter = WeekAdapter()
    private val iconGroupAdapter = IconGroupAdapter()
    private val homeAdapter = HomeAdapter()
    private var weekOfMonthMenu: WeekOfMonthMenu? = null
    private var dateSpinnerAdapter: DateSpinnerAdapter? = null
    private var isExpanded = true

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        initializeRecyclerView()

        observe(viewModel.weekRateItems) { weekRateAdapter.submitList(it) }
        observe(viewModel.weekItems) { weekAdapter.submitList(it) }
        observe(viewModel.iconGroupItems) { iconGroupAdapter.submitList(it) }
        observe(viewModel.homeItems) { homeAdapter.submitList(it) }
        observe(viewModel.weekOfMonth) {
            weekOfMonthMenu?.submitList(it)
//            dateSpinnerAdapter?.run {
//                clear()
//                addAll(it)
//                binding.spinner.setSelection(count - 1)
//            }
        }
        observe(viewModel.isExpanded) {
            isExpanded = !isExpanded
            binding.appbar.setExpanded(isExpanded, true)
        }
    }

    private fun initializeRecyclerView() {
//        binding.spinner.run {
//            dateSpinnerAdapter = DateSpinnerAdapter(context) { pos: Int, item: WeekOfMonth ->
//                viewModel.sendWeekRoutines(item.startDate)
//                setSelection(pos)
//                hideSpinnerDropDown(this)
//            }.apply { viewModel.getStartDate() }
//            adapter = dateSpinnerAdapter
//        }

        binding.spinnerLayout.setOnClickListener { showWeekOfMonthSpinner(it) }

        binding.rvWeekRate.run {
            layoutManager = FlexboxLayoutManager(context, FlexDirection.ROW, FlexWrap.NOWRAP).apply {
                justifyContent = JustifyContent.SPACE_BETWEEN
            }
            adapter = weekRateAdapter
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

        binding.appbar.addOnOffsetChangedListener(
            AppBarLayout.OnOffsetChangedListener { _, verticalOffset ->
                isExpanded = (verticalOffset == 0)
            }
        )
    }

    private fun showWeekOfMonthSpinner(v: View) {
        viewModel.getStartDate()
        context?.let {
            weekOfMonthMenu = WeekOfMonthMenu(it, viewModel).apply {
                showAsDropDown(v, WEEK_OF_MONTH_OFFSET_X, getPxFromDp(WEEK_OF_MONTH_OFFSET_Y), Gravity.CENTER_HORIZONTAL)
                viewModel.itemSelectedListener = { item: LocalDate ->
                    viewModel.sendWeekRoutines(item)
                    dismiss()
                }
            }
        }
    }

    private fun getPxFromDp(dp: Int): Int {
        return (dp * resources.displayMetrics.density).toInt()
    }

    private fun hideSpinnerDropDown(spinner: Spinner?) {
        val method: Method = Spinner::class.java.getDeclaredMethod("onDetachedFromWindow")
        method.isAccessible = true
        method.invoke(spinner)
    }
}

package com.nexters.mytine.ui.home.weekofmonth

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.PopupWindow
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.nexters.mytine.R
import com.nexters.mytine.ui.home.HomeViewModel

internal class WeekOfMonthMenu(val context: Context, private val vm: HomeViewModel) : PopupWindow(context) {
    private val weekOfMonthAdapter = WeekOfMonthAdapter()

    init {
        height = WindowManager.LayoutParams.WRAP_CONTENT
        width = WindowManager.LayoutParams.WRAP_CONTENT
        isOutsideTouchable = true
        isFocusable = true
        setBackgroundDrawable(AppCompatResources.getDrawable(context, android.R.color.transparent))
        setupView()
    }

    fun submitList(list: List<WeekOfMonthItem>) {
        weekOfMonthAdapter.submitList(list)
    }

    private fun setupView() {
        val view: View = LayoutInflater.from(context).inflate(R.layout.layout_popup_week_of_month, null)
        view.findViewById<RecyclerView>(R.id.week_of_month_rv)?.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = weekOfMonthAdapter.apply { setViewHolderViewModel(vm) }
        }
        contentView = view
    }
}

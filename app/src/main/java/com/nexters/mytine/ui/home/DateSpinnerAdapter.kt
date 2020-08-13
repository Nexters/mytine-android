package com.nexters.mytine.ui.home

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.nexters.mytine.R
import java.time.LocalDate

internal class DateSpinnerAdapter(
    context: Context,
    val viewModel: HomeViewModel
) : ArrayAdapter<WeekOfMonth>(context, R.layout.item_home_week_of_month) {
    private val inflater = LayoutInflater.from(context)

    init {
        addAll(viewModel.getStartDate())
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: inflater.inflate(R.layout.item_home_week_of_month, parent, false)
        getItem(position)?.let { setItem(view, it) }
        return view
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: inflater.inflate(R.layout.item_home_week_of_month, parent, false)
        getItem(position)?.let {
            setItem(view, it)
            view.setOnClickListener { _ -> viewModel.sendWeekRoutines(it.startDate) }
        }
        return view
    }

    private fun setItem(view: View, item: WeekOfMonth) {
        view.run {
            findViewById<TextView>(R.id.start_date_tv).text = getDateStr(item.startDate)
            findViewById<TextView>(R.id.end_date_tv).text = getDateStr(item.endDate)
        }
    }

    private fun getDateStr(date: LocalDate) = "${date.monthValue}월 ${date.dayOfMonth}일"
}

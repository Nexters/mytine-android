package com.nexters.mytine.ui.home

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.TextView
import com.nexters.mytine.R
import java.time.LocalDate

internal class DateSpinnerAdapter(
    context: Context,
    val onClickListener: (Int, WeekOfMonth) -> Unit
) : ArrayAdapter<WeekOfMonth>(context, R.layout.item_home_week_of_month) {
    private val inflater = LayoutInflater.from(context)
    private var selectedView: View? = null

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: inflater.inflate(R.layout.item_home_week_of_month, parent, false)
        getItem(position)?.let { setItem(view, it) }
        return view
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: inflater.inflate(R.layout.item_home_week_of_month, parent, false)
        getItem(position)?.let {
            setItem(view, it)
            view.setOnClickListener { _ -> onClickListener(position, it) }
        }
        return view
    }

    private fun setItem(view: View, item: WeekOfMonth) {
        view.run {
            selectedView?.isSelected = false
            findViewById<TextView>(R.id.start_date_tv).text = getDateStr(item.startDate)
            findViewById<TextView>(R.id.end_date_tv).text = getDateStr(item.endDate)
            selectedView = findViewById<LinearLayout>(R.id.layout)
            selectedView?.isSelected = true
        }
    }

    private fun getDateStr(date: LocalDate) = "${date.monthValue}월 ${date.dayOfMonth}일"
}

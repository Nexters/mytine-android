package com.nexters.mytine.ui.report.month

import android.os.Bundle
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.nexters.mytine.R
import com.nexters.mytine.base.fragment.BaseFragment
import com.nexters.mytine.databinding.FragmentReportMonthBinding
import com.nexters.mytine.ui.report.routine.ReportRoutineAdapter
import com.nexters.mytine.utils.extensions.observe
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
internal class ReportMonthFragment : BaseFragment<FragmentReportMonthBinding, ReportMonthViewModel>() {
    override val layoutResId = R.layout.fragment_report_month
    override val viewModelClass = ReportMonthViewModel::class
    override val navArgs by navArgs<ReportMonthFragmentArgs>()

    private val maxAchieveRoutineAdapter = ReportRoutineAdapter()
    private val minAchieveRoutineAdapter = ReportRoutineAdapter()

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        binding.maxAchieveRoutineReportLayout.routineRankLayout.achieveRoutineRv.run {
            layoutManager = LinearLayoutManager(context)
            adapter = maxAchieveRoutineAdapter
        }
        binding.minAchieveRoutineReportLayout.routineRankLayout.achieveRoutineRv.run {
            layoutManager = LinearLayoutManager(context)
            adapter = minAchieveRoutineAdapter
        }

        observe(viewModel.maxAchieveRoutineList, { maxAchieveRoutineAdapter.submitList(it) })
        observe(viewModel.minAchieveRoutineList, { minAchieveRoutineAdapter.submitList(it) })
    }
}

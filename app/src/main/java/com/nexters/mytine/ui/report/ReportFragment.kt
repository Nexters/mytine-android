package com.nexters.mytine.ui.report

import android.os.Bundle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.nexters.mytine.R
import com.nexters.mytine.base.fragment.BaseFragment
import com.nexters.mytine.databinding.FragmentReportBinding
import com.nexters.mytine.utils.extensions.observe
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
internal class ReportFragment : BaseFragment<FragmentReportBinding, ReportViewModel>() {

    override val layoutResId = R.layout.fragment_report
    override val viewModelClass = ReportViewModel::class

    private val reportAdapter = ReportAdapter()

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        initializeToolbar()
        initializeRecyclerView()

        observe(viewModel.reportItems) { reportAdapter.submitList(it) }
    }

    private fun initializeToolbar() {
        binding.toolbar.setNavigationOnClickListener { findNavController().popBackStack() }
    }

    private fun initializeRecyclerView() {
        binding.rvReport.run {
            layoutManager = LinearLayoutManager(context)
            adapter = reportAdapter
        }
        reportAdapter.setViewHolderViewModel(viewModel)
    }
}

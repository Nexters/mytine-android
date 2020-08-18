package com.nexters.mytine.ui.report

import android.os.Bundle
import androidx.navigation.fragment.findNavController
import com.nexters.mytine.R
import com.nexters.mytine.base.fragment.BaseFragment
import com.nexters.mytine.databinding.FragmentReportBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
internal class ReportFragment : BaseFragment<FragmentReportBinding, ReportViewModel>() {

    override val layoutResId = R.layout.fragment_report
    override val viewModelClass = ReportViewModel::class

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        initializeToolbar()
    }

    private fun initializeToolbar() {
        binding.toolbar.setNavigationOnClickListener { findNavController().popBackStack() }
    }
}

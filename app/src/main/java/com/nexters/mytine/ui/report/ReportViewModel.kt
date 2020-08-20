package com.nexters.mytine.ui.report

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import com.nexters.mytine.base.viewmodel.BaseViewModel

internal class ReportViewModel @ViewModelInject constructor() : BaseViewModel() {
    val reportItems = MutableLiveData<List<ReportItems>>()

    init {
        reportItems.value = listOf(ReportItems.EmptyItems())
    }
}

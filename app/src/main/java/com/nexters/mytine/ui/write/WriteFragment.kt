package com.nexters.mytine.ui.write

import com.nexters.mytine.R
import com.nexters.mytine.base.fragment.BaseFragment
import com.nexters.mytine.databinding.FragmentHomeBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
internal class WriteFragment : BaseFragment<FragmentHomeBinding, WriteViewModel>() {
    override val layoutResId = R.layout.fragment_write
    override val viewModelClass = WriteViewModel::class
}

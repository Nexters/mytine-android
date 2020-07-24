package com.nexters.mytine.ui.home.restrospect

import com.nexters.mytine.R
import com.nexters.mytine.base.fragment.BaseFragment
import com.nexters.mytine.databinding.FragmentRetrospectBinding

internal class RetrospectFragment : BaseFragment<FragmentRetrospectBinding, RetrospectViewModel>() {
    override val layoutResId = R.layout.fragment_retrospect
    override val viewModelClass = RetrospectViewModel::class
}

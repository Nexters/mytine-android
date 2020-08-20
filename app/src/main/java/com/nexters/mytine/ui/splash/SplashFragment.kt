package com.nexters.mytine.ui.splash

import com.nexters.mytine.R
import com.nexters.mytine.base.fragment.BaseFragment
import com.nexters.mytine.databinding.FragmentSplashBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
internal class SplashFragment : BaseFragment<FragmentSplashBinding, SplashViewModel>() {

    override val layoutResId = R.layout.fragment_splash
    override val viewModelClass = SplashViewModel::class
}

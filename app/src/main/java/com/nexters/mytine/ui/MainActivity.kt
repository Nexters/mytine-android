package com.nexters.mytine.ui

import com.nexters.mytine.R
import com.nexters.mytine.base.activity.BaseActivity
import com.nexters.mytine.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
internal class MainActivity : BaseActivity<ActivityMainBinding, MainActivityViewModel>() {
    override val layoutResId = R.layout.activity_main
    override val viewModelClass = MainActivityViewModel::class
}

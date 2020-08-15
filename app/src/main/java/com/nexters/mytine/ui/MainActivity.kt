package com.nexters.mytine.ui

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import com.nexters.mytine.R
import com.nexters.mytine.base.activity.BaseActivity
import com.nexters.mytine.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
internal class MainActivity : BaseActivity<ActivityMainBinding, MainActivityViewModel>() {
    override val layoutResId = R.layout.activity_main
    override val viewModelClass = MainActivityViewModel::class

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initializeWindow()
    }

    private fun initializeWindow() {
        window.apply {
            clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)

            decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY

            statusBarColor = Color.TRANSPARENT
        }
    }
}

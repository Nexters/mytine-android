package com.nexters.mytine.ui.onboarding

import android.os.Bundle
import com.nexters.mytine.R
import com.nexters.mytine.base.fragment.BaseFragment
import com.nexters.mytine.databinding.FragmentOnBoardingBinding
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
internal class OnBoardingFragment : BaseFragment<FragmentOnBoardingBinding, OnBoardingViewModel>() {

    override val layoutResId = R.layout.fragment_on_boarding
    override val viewModelClass = OnBoardingViewModel::class

    private val adapter = PagerRecyclerViewAdapter()

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        initViewPager()
    }

    private fun initViewPager() {
        val list = arrayListOf<OnBoardingItem>(
            OnBoardingItem.OnBoarding(Guide("", "첫번째")),
            OnBoardingItem.OnBoarding(Guide("", "두번째")),
            OnBoardingItem.OnBoarding(Guide("", "세번째"))
        )
        adapter.submitList(list)

        binding.viewPager.adapter = adapter

        binding.viewPager.setPageTransformer { page, position ->
            Timber.e("position $position")
            Timber.e("position $page")
            viewModel.isLast.value = position.toInt() == 2
        }

        binding.indicator.apply {
            setViewPager(binding.viewPager)
            createIndicators(pageCnt, 0)
        }
    }

    companion object {
        const val pageCnt = 3
    }
}

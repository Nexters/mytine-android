package com.nexters.mytine.ui.onboarding

import android.os.Bundle
import com.nexters.mytine.R
import com.nexters.mytine.base.fragment.BaseFragment
import com.nexters.mytine.databinding.FragmentOnBoardingBinding
import com.nexters.mytine.utils.extensions.observe
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
internal class OnBoardingFragment : BaseFragment<FragmentOnBoardingBinding, OnBoardingViewModel>() {

    override val layoutResId = R.layout.fragment_on_boarding
    override val viewModelClass = OnBoardingViewModel::class

    private val adapter = PagerRecyclerViewAdapter()

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.startOnBoarding()

        initViewPager()

        observe(viewModel.isSkip) {
            binding.viewPager.currentItem = binding.viewPager.currentItem + 1
        }
    }

    private fun initViewPager() {

        val list = arrayListOf<OnBoardingItem>(
            OnBoardingItem.OnBoarding(Guide("data01.json", "루틴 생성하기", "원하는 이모지로\n나만의 루틴을 만들어보세요!")),
            OnBoardingItem.OnBoarding(Guide("data02.json", "루틴 완료하기", "좌우 스와이프로 루틴을\n완료, 취소해보세요!")),
            OnBoardingItem.OnBoarding(Guide("data03.json", "월간회고 확인하기", "매달 나의 루틴 순위를 확인하며\n더 나은 다음 달을 계획해보세요!"))
        )
        adapter.submitList(list)
        binding.viewPager.adapter = adapter

        binding.viewPager.setPageTransformer { _, _ ->
            viewModel.isLast.value = binding.viewPager.currentItem == 2
            viewModel.setSkipContentsValue()
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

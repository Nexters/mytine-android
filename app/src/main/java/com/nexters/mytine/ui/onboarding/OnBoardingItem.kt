package com.nexters.mytine.ui.onboarding

import com.nexters.mytine.base.recyclerview.BaseItem

internal sealed class OnBoardingItem(override val itemId: String) : BaseItem {
    class OnBoarding(val guide: Guide) : OnBoardingItem("onboarding")
}

data class Guide(
    var guideImage: String,
    var guideText: String,
    val guideDescription: String
)

package com.nexters.mytine.ui.write

import android.os.Bundle
import androidx.navigation.fragment.findNavController
import com.nexters.mytine.R
import com.nexters.mytine.base.fragment.BaseFragment
import com.nexters.mytine.databinding.FragmentWriteBinding
import com.nexters.mytine.utils.extensions.hideKeyboard
import com.vanniktech.emoji.EmojiPopup
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
internal class WriteFragment : BaseFragment<FragmentWriteBinding, WriteViewModel>() {
    override val layoutResId = R.layout.fragment_write
    override val viewModelClass = WriteViewModel::class

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        initializeToolbar()
        initializeEmoji()
    }

    private fun initializeToolbar() {
        binding.toolbar.setNavigationOnClickListener { findNavController().popBackStack() }
    }

    private fun initializeEmoji() {
        val emojiPopup = EmojiPopup.Builder.fromRootView(binding.root)
            .setKeyboardAnimationStyle(R.style.emoji_fade_animation_style)
            .setOnEmojiClickListener { _, _ ->
                binding.root.hideKeyboard()
            }
            .build(binding.etEmoji)

        binding.etEmoji.disableKeyboardInput(emojiPopup)
        binding.etEmoji.forceSingleEmoji()
    }
}

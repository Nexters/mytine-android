package com.nexters.mytine.ui.write

import android.os.Bundle
import android.view.MenuItem
import androidx.activity.OnBackPressedCallback
import androidx.navigation.fragment.navArgs
import com.afollestad.materialdialogs.MaterialDialog
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.nexters.mytine.R
import com.nexters.mytine.base.fragment.BaseFragment
import com.nexters.mytine.databinding.FragmentWriteBinding
import com.nexters.mytine.utils.extensions.hideKeyboard
import com.nexters.mytine.utils.extensions.observe
import com.vanniktech.emoji.EmojiPopup
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
internal class WriteFragment : BaseFragment<FragmentWriteBinding, WriteViewModel>() {

    override val layoutResId = R.layout.fragment_write
    override val viewModelClass = WriteViewModel::class
    override val navArgs by navArgs<WriteFragmentArgs>()

    private val weekAdapter = WeekAdapter()

    private val menuWrite: MenuItem
        get() = binding.toolbar.menu.findItem(R.id.action_write)

    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            viewModel.onBackPressed()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requireActivity().onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        initializeToolbar()
        initializeEmoji()
        initializeRecyclerView()

        observe(viewModel.weekItems) { weekAdapter.submitList(it) }
        observe(viewModel.enableWrite) { menuWrite.isEnabled = it }
        observe(viewModel.showBackDialog) {
            MaterialDialog(requireContext())
                .message(R.string.write_back_dialog_message)
                .positiveButton(R.string.leave) {
                    viewModel.onClickLeave()
                }
                .negativeButton(R.string.cancel)
                .show()
        }
    }

    private fun initializeToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            viewModel.onBackPressed()
        }
        binding.toolbar.run {
            setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.action_write -> {
                        viewModel.onClickSave()
                        hideKeyboard()
                        true
                    }
                    else -> super.onOptionsItemSelected(it)
                }
            }
        }
    }

    private fun initializeEmoji() {
        val emojiPopup = EmojiPopup.Builder.fromRootView(binding.root)
            .setKeyboardAnimationStyle(R.style.emoji_fade_animation_style)
            .setOnEmojiClickListener { _, _ ->
                binding.root.hideKeyboard()
            }
            .build(binding.layoutEmoji.editEmoji)

        binding.layoutEmoji.editEmoji.disableKeyboardInput(emojiPopup)
        binding.layoutEmoji.editEmoji.forceSingleEmoji()
    }

    private fun initializeRecyclerView() {
        binding.rvWeek.run {
            layoutManager = FlexboxLayoutManager(context, FlexDirection.ROW, FlexWrap.NOWRAP).apply {
                justifyContent = JustifyContent.SPACE_BETWEEN
            }
            adapter = weekAdapter
        }
        weekAdapter.setViewHolderViewModel(viewModel)
    }
}

package com.nexters.mytine.base.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavArgs
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import com.nexters.mytine.BR
import com.nexters.mytine.base.viewmodel.BaseViewModel
import com.nexters.mytine.ui.EmptyNavArgs
import com.nexters.mytine.utils.extensions.observe
import com.nexters.mytine.utils.extensions.toast
import com.nexters.mytine.utils.navigation.BackDirections
import kotlin.reflect.KClass

internal abstract class BaseFragment<VB : ViewDataBinding, VM : BaseViewModel> : Fragment() {

    protected abstract val layoutResId: Int
    protected abstract val viewModelClass: KClass<VM>

    lateinit var binding: VB

    protected val viewModel by lazy(LazyThreadSafetyMode.NONE) {
        ViewModelProvider(viewModelStore, defaultViewModelProviderFactory).get(viewModelClass.java)
    }

    protected open val navArgs: NavArgs = EmptyNavArgs

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, layoutResId, container, false)

        binding.lifecycleOwner = viewLifecycleOwner
        binding.setVariable(BR.viewModel, viewModel)

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        observe(viewModel.navDirections) { navigate(it) }
        observe(viewModel.toast) { toast(it) }

        viewModel.navArgs(navArgs)
    }

    private fun navigate(navDirections: NavDirections) {
        if (navDirections is BackDirections) {
            if (navDirections.destinationId != -1) {
                findNavController().popBackStack(navDirections.destinationId, navDirections.inclusive)
            } else {
                findNavController().popBackStack()
            }

            return
        }
        findNavController().navigate(navDirections)
    }
}

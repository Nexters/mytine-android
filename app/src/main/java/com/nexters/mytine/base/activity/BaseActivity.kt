package com.nexters.mytine.base.activity

import android.os.Bundle
import androidx.annotation.CallSuper
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModelProvider
import com.nexters.mytine.BR
import com.nexters.mytine.base.viewmodel.BaseViewModel
import kotlin.reflect.KClass

internal abstract class BaseActivity<VB : ViewDataBinding, VM : BaseViewModel> : AppCompatActivity() {

    protected abstract val layoutResId: Int
    protected abstract val viewModelClass: KClass<VM>

    lateinit var binding: VB

    protected val viewModel: VM by lazy(LazyThreadSafetyMode.NONE) {
        ViewModelProvider(viewModelStore, defaultViewModelProviderFactory).get(viewModelClass.java)
    }

    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, layoutResId)

        binding.lifecycleOwner = this

        binding.setVariable(BR.viewModel, viewModel)
    }
}

package com.nexters.mytine.utils.extensions

import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Observer

internal fun <T : Any, L : LiveData<T>> LifecycleOwner.observe(liveData: L, action: (T) -> Unit) = liveData.observe(
    this,
    Observer { action(it) }
)

internal fun <T : Any, L : LiveData<T>> Fragment.observe(liveData: L, action: (T) -> Unit) = liveData.observe(
    viewLifecycleOwner,
    Observer { action(it) }
)

internal fun <T1, T2, R> combineLatest(
    source1: LiveData<T1>,
    source2: LiveData<T2>,
    combiner: (T1, T2) -> R
): LiveData<R> {
    val mediator = MediatorLiveData<R>()

    val combinerFunction = {
        val source1Value = source1.value
        val source2Value = source2.value
        if (source1Value != null && source2Value != null) {
            mediator.value = combiner.invoke(source1Value, source2Value)
        }
    }

    mediator.addSource(source1) { combinerFunction() }
    mediator.addSource(source2) { combinerFunction() }

    return mediator
}

internal fun <T1, T2, T3, R> combineLatest(
    source1: LiveData<T1>,
    source2: LiveData<T2>,
    source3: LiveData<T3>,
    combiner: (T1, T2, T3) -> R
): LiveData<R> {
    val mediator = MediatorLiveData<R>()

    val combinerFunction = {
        val source1Value = source1.value
        val source2Value = source2.value
        val source3Value = source3.value
        if (source1Value != null && source2Value != null && source3Value != null) {
            mediator.value = combiner.invoke(source1Value, source2Value, source3Value)
        }
    }

    mediator.addSource(source1) { combinerFunction() }
    mediator.addSource(source2) { combinerFunction() }
    mediator.addSource(source3) { combinerFunction() }

    return mediator
}

internal fun <T1, T2, T3, T4, R> combineLatest(
    source1: LiveData<T1>,
    source2: LiveData<T2>,
    source3: LiveData<T3>,
    source4: LiveData<T4>,
    combiner: (T1, T2, T3, T4) -> R
): LiveData<R> {
    val mediator = MediatorLiveData<R>()

    val combinerFunction = {
        val source1Value = source1.value
        val source2Value = source2.value
        val source3Value = source3.value
        val source4Value = source4.value
        if (source1Value != null && source2Value != null && source3Value != null && source4Value != null) {
            mediator.value = combiner.invoke(source1Value, source2Value, source3Value, source4Value)
        }
    }

    mediator.addSource(source1) { combinerFunction() }
    mediator.addSource(source2) { combinerFunction() }
    mediator.addSource(source3) { combinerFunction() }
    mediator.addSource(source4) { combinerFunction() }

    return mediator
}

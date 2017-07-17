package com.kamer.orny.utils

import android.arch.lifecycle.*

@Suppress("UNCHECKED_CAST")
inline fun <VM : ViewModel> createFactory(crossinline creator: () -> VM): ViewModelProvider.Factory =
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return creator.invoke() as T
            }
        }

inline fun <T> LiveData<T>.safeObserve(owner: LifecycleOwner, crossinline observer: (T) -> Unit) {
    observe(owner, Observer<T> { t ->
        if (t != null) {
            observer.invoke(t)
        }
    })
}

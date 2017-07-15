package com.kamer.orny.utils

import android.annotation.SuppressLint
import android.arch.core.executor.AppToolkitTaskExecutor
import android.arch.core.executor.TaskExecutor
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Observer
import java.util.concurrent.CountDownLatch

class TestUtils {

    companion object {
        @SuppressLint("RestrictedApi")
        fun setupLiveDataExecutor() {
            val executor = object : TaskExecutor() {
                override fun executeOnDiskIO(p0: Runnable?) {
                    p0?.run()
                }

                override fun isMainThread(): Boolean = true

                override fun postToMainThread(p0: Runnable?) {
                    p0?.run()
                }
            }
            AppToolkitTaskExecutor.getInstance().setDelegate(executor)
        }
    }
}

@Suppress("UNCHECKED_CAST")
@Throws(InterruptedException::class)
fun <T> LiveData<T>.getResultValue(): T {
    val data = arrayOfNulls<Any>(1)
    val latch = CountDownLatch(1)
    val observer = object : Observer<T> {
        override fun onChanged(o: T?) {
            data[0] = o
            latch.countDown()
            removeObserver(this)
        }
    }
    observeForever(observer)

    return data[0] as T
}

@Throws(InterruptedException::class)
fun <T> LiveData<T>.getResultValues(count: Int, postElements: () -> Unit = {}): List<T> {
    val data = mutableListOf<T>()
    val latch = CountDownLatch(count)
    val observer = object : Observer<T> {
        override fun onChanged(o: T?) {
            if (o == null) throw Exception("Null value in LiveData")
            data.add(o)
            latch.countDown()
            if (latch.count == 0L) {
                removeObserver(this)
            }
        }
    }
    observeForever(observer)
    postElements.invoke()

    return data
}

@Throws(InterruptedException::class)
fun <T> LiveData<T>.hasValue(): Boolean {
    var hasValue = false
    val latch = CountDownLatch(1)
    val observer = object : Observer<T> {
        override fun onChanged(o: T?) {
            hasValue = true
            latch.countDown()
            removeObserver(this)
        }
    }
    observeForever(observer)

    return hasValue
}

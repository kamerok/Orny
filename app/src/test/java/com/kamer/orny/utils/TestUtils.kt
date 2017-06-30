package com.kamer.orny.utils

import android.annotation.SuppressLint
import android.arch.core.executor.AppToolkitTaskExecutor
import android.arch.core.executor.TaskExecutor
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Observer
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

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
    latch.await(2, TimeUnit.SECONDS)

    return data[0] as T
}

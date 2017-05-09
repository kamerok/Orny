package com.kamer.orny.presentation.editexpense

import android.app.Activity
import java.lang.ref.WeakReference


class EditExpenseRouterImpl : EditExpenseRouter {

    private lateinit var activityRef: WeakReference<Activity>

    fun setActivity(activity: Activity) {
        activityRef = WeakReference(activity)
    }

    override fun closeScreen() {
        activityRef.get()?.apply {
            finish()
        }
    }
}
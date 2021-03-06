package com.kamer.orny.data.android

import android.app.Activity
import android.content.Intent
import com.kamer.orny.di.app.ApplicationScope
import java.lang.ref.WeakReference
import javax.inject.Inject

@ApplicationScope
class ActivityHolderImpl @Inject constructor() : ActivityHolder, ActivityHolderSetter {

    private var activityRef: WeakReference<Activity>? = null
    private var resultHandlers = mutableListOf<ActivityHolder.ActivityResultHandler>()

    override fun onActivityCreated(activity: Activity) {
        if (activityRef == null || activityRef?.get() == null) {
            activityRef = WeakReference(activity)
        }
    }

    override fun onActivityResumed(activity: Activity) {
        if (activityRef?.get() != activity) {
            activityRef = WeakReference(activity)
        }
    }

    override fun onActivityDestroyed(activity: Activity) {
        if (activityRef?.get() == activity) {
            activityRef = null
        }
    }

    override fun passActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        resultHandlers
                .filter { it.handleActivityResult(requestCode, resultCode, data) }
                .forEach { return }
    }

    override fun getActivity(): Activity? = activityRef?.get()

    override fun addActivityResultHandler(activityResultHandler: ActivityHolder.ActivityResultHandler) {
        resultHandlers.add(activityResultHandler)
    }

    override fun removeActivityResultHandler(activityResultHandler: ActivityHolder.ActivityResultHandler) {
        resultHandlers.remove(activityResultHandler)
    }
}
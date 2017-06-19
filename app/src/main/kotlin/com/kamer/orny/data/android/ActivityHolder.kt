package com.kamer.orny.data.android


interface ActivityHolder {

    fun onActivityResumed(activity: android.app.Activity)

    fun onActivityDestroyed(activity: android.app.Activity)

    fun passActivityResult(requestCode: Int, resultCode: Int, data: android.content.Intent?)

    fun getActivity(): android.app.Activity?

    fun addActivityResultHandler(activityResultHandler: com.kamer.orny.data.android.ActivityHolder.ActivityResultHandler)

    fun removeActivityResultHandler(activityResultHandler: com.kamer.orny.data.android.ActivityHolder.ActivityResultHandler)

    interface ActivityResultHandler {

        fun handleActivityResult(requestCode: Int, resultCode: Int, data: android.content.Intent?): Boolean

    }

}
package com.kamer.orny.data.android


interface ActivityHolder {

    fun getActivity(): android.app.Activity?

    fun addActivityResultHandler(activityResultHandler: com.kamer.orny.data.android.ActivityHolder.ActivityResultHandler)

    fun removeActivityResultHandler(activityResultHandler: com.kamer.orny.data.android.ActivityHolder.ActivityResultHandler)

    interface ActivityResultHandler {

        fun handleActivityResult(requestCode: Int, resultCode: Int, data: android.content.Intent?): Boolean

    }

}
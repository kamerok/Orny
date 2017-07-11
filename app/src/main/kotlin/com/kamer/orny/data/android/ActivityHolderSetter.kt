package com.kamer.orny.data.android


interface ActivityHolderSetter {

    fun onActivityResumed(activity: android.app.Activity)

    fun onActivityDestroyed(activity: android.app.Activity)

    fun passActivityResult(requestCode: Int, resultCode: Int, data: android.content.Intent?)

}
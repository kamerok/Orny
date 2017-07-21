package com.kamer.orny.data.android

import android.app.Activity
import android.content.Intent


interface ActivityHolderSetter {

    fun onActivityCreated(activity: Activity)

    fun onActivityResumed(activity: Activity)

    fun onActivityDestroyed(activity: Activity)

    fun passActivityResult(requestCode: Int, resultCode: Int, data: Intent?)

}
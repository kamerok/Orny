package com.kamer.orny.data.android

import android.app.Activity
import android.content.Intent
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException
import io.reactivex.Completable
import io.reactivex.schedulers.Schedulers


class ReactiveActivitiesImpl(val activityHolder: ActivityHolder)
    : ReactiveActivities, ActivityHolder.ActivityResultHandler {

    companion object {
        private const val REQUEST_RECOVER_AUTH = 1001
    }

    private var recoverGoogleAuthListener: ReactiveListener? = null

    init {
        activityHolder.addActivityResultHandler(this)
    }

    override fun recoverGoogleAuthException(exception: UserRecoverableAuthIOException): Completable = Completable
            .create { e ->
                recoverGoogleAuthListener = object : ReactiveListener {
                    override fun onError(t: Throwable) = e.onError(t)

                    override fun onSuccess() = e.onComplete()
                }
                activityHolder.getActivity()?.startActivityForResult(exception.intent, REQUEST_RECOVER_AUTH)
            }
            .observeOn(Schedulers.io())

    override fun handleActivityResult(requestCode: Int, resultCode: Int, data: Intent?): Boolean =
            when (requestCode) {
                REQUEST_RECOVER_AUTH -> {
                    if (resultCode == Activity.RESULT_OK) {
                        recoverGoogleAuthListener?.onSuccess()
                    } else {
                        recoverGoogleAuthListener?.onError(Exception("No permission"))
                    }
                    true
                }
                else -> false
            }

    private interface ReactiveListener {

        fun onError(t: Throwable)

        fun onSuccess()

    }
}
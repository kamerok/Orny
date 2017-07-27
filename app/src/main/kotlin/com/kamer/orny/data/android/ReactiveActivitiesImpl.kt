package com.kamer.orny.data.android

import android.accounts.AccountManager
import android.app.Activity
import android.content.Intent
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException
import com.kamer.orny.di.app.ApplicationScope
import com.kamer.orny.presentation.launch.LoginActivity
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject


@ApplicationScope
class ReactiveActivitiesImpl @Inject constructor(
        val activityHolder: ActivityHolder
) : ReactiveActivities, ActivityHolder.ActivityResultHandler {

    companion object {
        private const val REQUEST_LOGIN = 1000
        private const val REQUEST_ACCOUNT_PICKER = 1001
        private const val REQUEST_RECOVER_AUTH = 1002
    }

    private var loginListener: CompletableListener? = null
    private var recoverGoogleAuthListener: CompletableListener? = null
    private var chooseAccountListener: SingleListener<String>? = null

    init {
        activityHolder.addActivityResultHandler(this)
    }

    override fun login(): Completable = Completable
            .create { e ->
                loginListener = object : CompletableListener {
                    override fun onError(t: Throwable) = e.onError(t)

                    override fun onSuccess() {
                        e.onComplete()
                        loginListener = null
                    }
                }
                val activity = activityHolder.getActivity()
                activity?.startActivityForResult(LoginActivity.getIntent(activity), REQUEST_LOGIN)
            }
            .observeOn(Schedulers.io())

    override fun recoverGoogleAuthException(exception: UserRecoverableAuthIOException): Completable = Completable
            .create { e ->
                recoverGoogleAuthListener = object : CompletableListener {
                    override fun onError(t: Throwable) = e.onError(t)

                    override fun onSuccess() {
                        e.onComplete()
                        recoverGoogleAuthListener = null
                    }
                }
                activityHolder.getActivity()?.startActivityForResult(exception.intent, REQUEST_RECOVER_AUTH)
            }
            .observeOn(Schedulers.io())

    override fun chooseGoogleAccount(credential: GoogleAccountCredential): Single<String> = Single
            .create<String> { e ->
                chooseAccountListener = object : SingleListener<String> {

                    override fun onError(t: Throwable) = e.onError(t)

                    override fun onSuccess(value: String) = e.onSuccess(value)

                }
                activityHolder.getActivity()?.startActivityForResult(credential.newChooseAccountIntent(), REQUEST_ACCOUNT_PICKER)
            }
            .observeOn(Schedulers.io())

    override fun handleActivityResult(requestCode: Int, resultCode: Int, data: Intent?): Boolean =
            when (requestCode) {
                REQUEST_LOGIN -> {
                    if (resultCode == Activity.RESULT_OK) {
                        loginListener?.onSuccess()
                    } else {
                        loginListener?.onError(Exception("Login failed"))
                    }
                    true
                }
                REQUEST_RECOVER_AUTH -> {
                    if (resultCode == Activity.RESULT_OK) {
                        recoverGoogleAuthListener?.onSuccess()
                    } else {
                        recoverGoogleAuthListener?.onError(Exception("No permission"))
                    }
                    true
                }
                REQUEST_ACCOUNT_PICKER -> {
                    if (resultCode == Activity.RESULT_OK && data != null && data.extras != null) {
                        val accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME)
                        if (accountName != null) {
                            chooseAccountListener?.onSuccess(accountName)
                        } else {
                            chooseAccountListener?.onError(Exception("No name"))
                        }
                    } else {
                        chooseAccountListener?.onError(Exception("Pick account failed"))
                    }
                    true
                }
                else -> false
            }

    private interface CompletableListener {

        fun onError(t: Throwable)

        fun onSuccess()

    }

    private interface SingleListener<in T> {

        fun onError(t: Throwable)

        fun onSuccess(value: T)

    }
}
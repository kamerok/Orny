package com.kamer.orny.data.android

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException
import io.reactivex.Completable
import io.reactivex.Single


interface ReactiveActivities {

    fun login(): Completable

    fun recoverGoogleAuthException(exception: UserRecoverableAuthIOException): Completable

    fun chooseGoogleAccount(credential: GoogleAccountCredential): Single<String>

}
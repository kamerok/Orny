package com.kamer.orny.data.android

import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException
import io.reactivex.Completable


interface ReactiveActivities {

    fun recoverGoogleAuthException(exception: UserRecoverableAuthIOException): Completable

}
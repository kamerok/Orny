package com.kamer.orny.data.google

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import io.reactivex.Completable
import io.reactivex.Single


interface GoogleAuthHolder {

    fun login(): Completable

    fun logout(): Completable

    fun getActiveCredentials(): Single<GoogleAccountCredential>

}
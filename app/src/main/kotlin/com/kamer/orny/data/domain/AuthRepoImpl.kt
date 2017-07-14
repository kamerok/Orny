package com.kamer.orny.data.domain

import com.kamer.orny.data.google.GoogleAuthHolder
import com.kamer.orny.di.app.ApplicationScope
import io.reactivex.Completable
import javax.inject.Inject


@ApplicationScope
class AuthRepoImpl @Inject constructor(
        private val googleAuthHolder: GoogleAuthHolder
) : AuthRepo {

//    override fun isAuthorized(): Observable<Boolean> = googleAuthHolder.isAuthorized()

    override fun login(): Completable = googleAuthHolder.login()

    override fun logout(): Completable = googleAuthHolder.logout()

}
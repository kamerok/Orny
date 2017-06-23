package com.kamer.orny.data

import com.kamer.orny.data.google.GoogleAuthHolder
import io.reactivex.Completable
import io.reactivex.Observable


class AuthRepoImpl(private val googleAuthHolder: GoogleAuthHolder) : AuthRepo {

//    override fun isAuthorized(): Observable<Boolean> = googleAuthHolder.isAuthorized()

    override fun login(): Completable = googleAuthHolder.login()

    override fun logout(): Completable = googleAuthHolder.logout()

}
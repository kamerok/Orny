package com.kamer.orny.data.domain


class AuthRepoImpl(private val googleAuthHolder: com.kamer.orny.data.google.GoogleAuthHolder) : AuthRepo {

//    override fun isAuthorized(): Observable<Boolean> = googleAuthHolder.isAuthorized()

    override fun login(): io.reactivex.Completable = googleAuthHolder.login()

    override fun logout(): io.reactivex.Completable = googleAuthHolder.logout()

}
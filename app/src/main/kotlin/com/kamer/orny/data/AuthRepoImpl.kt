package com.kamer.orny.data

import com.kamer.orny.data.google.GoogleRepo
import io.reactivex.Completable
import io.reactivex.Observable


class AuthRepoImpl(private val googleRepo: GoogleRepo) : AuthRepo {

    override fun isAuthorized(): Observable<Boolean> = googleRepo.isAuthorized()

    override fun login(): Completable = googleRepo.login()

    override fun logout(): Completable = googleRepo.logout()

}
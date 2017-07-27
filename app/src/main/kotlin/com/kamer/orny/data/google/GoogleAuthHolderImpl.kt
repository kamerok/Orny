package com.kamer.orny.data.google

import android.Manifest
import android.content.Context
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.util.ExponentialBackOff
import com.google.api.services.sheets.v4.SheetsScopes
import com.kamer.orny.data.android.ActivityHolder
import com.kamer.orny.data.android.Prefs
import com.kamer.orny.data.android.ReactiveActivities
import com.kamer.orny.di.app.ApplicationScope
import com.kamer.orny.utils.hasPermission
import com.tbruyelle.rxpermissions2.RxPermissions
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.*
import javax.inject.Inject


@ApplicationScope
class GoogleAuthHolderImpl @Inject constructor(
        private val context: Context,
        private val prefs: Prefs,
        private val activityHolder: ActivityHolder,
        private val reactiveActivities: ReactiveActivities)
    : GoogleAuthHolder {

    companion object {
        private val SCOPES = arrayOf(SheetsScopes.SPREADSHEETS)
    }

    override fun login(): Completable {
        if (prefs.accountName.isNotEmpty()) {
            return Completable.complete()
        }
        return checkPermission()
                .andThen(createCredentials())
                .flatMap { reactiveActivities.chooseGoogleAccount(it) }
                .flatMapCompletable { accountName ->
                    Completable.fromAction {
                        prefs.accountName = accountName
                    }
                }
    }

    override fun logout(): Completable = Completable.fromAction {
        prefs.accountName = ""
    }

    override fun getActiveCredentials(): Single<GoogleAccountCredential> =
            checkAuth()
                    .andThen(checkPermission())
                    .andThen(createCredentials())
                    .flatMap { credential ->
                        checkIfAccountExist(credential)
                                .toSingle { credential }
                    }

    private fun createCredentials(): Single<GoogleAccountCredential> = Single.fromCallable {
        val accountCredential = GoogleAccountCredential.usingOAuth2(
                context, Arrays.asList<String>(*SCOPES))
                .setBackOff(ExponentialBackOff())
        accountCredential.selectedAccountName = prefs.accountName
        return@fromCallable accountCredential
    }

    private fun checkAuth(): Completable = Completable
            .fromAction {
                if (prefs.accountName.isEmpty()) {
                    throw Exception("Not logged")
                }
            }
            .retryWhen { errorStream ->
                errorStream.flatMap {
                    reactiveActivities.login().toSingleDefault("").toFlowable()
                }
            }

    private fun checkIfAccountExist(credential: GoogleAccountCredential): Completable = Completable
            .fromAction {
                if (context.hasPermission(Manifest.permission.GET_ACCOUNTS) && credential.selectedAccountName.isNullOrEmpty()) {
                    prefs.accountName = ""
                    throw Exception("Account not exist")
                }
            }
            .retryWhen { errorStream ->
                errorStream.flatMap {
                    reactiveActivities.login().toSingleDefault("").toFlowable()
                }
            }

    private fun checkPermission(): Completable = Single.just("").flatMapCompletable {
        val activity = activityHolder.getActivity()
        return@flatMapCompletable when {
            context.hasPermission(Manifest.permission.GET_ACCOUNTS) -> Completable.complete()
            activity != null -> Observable
                    .just("")
                    .observeOn(AndroidSchedulers.mainThread())
                    //create RxPermissions() instance only when needed
                    .flatMap { Observable.just("").compose(RxPermissions(activity).ensure(Manifest.permission.GET_ACCOUNTS)) }
                    .observeOn(Schedulers.io())
                    .flatMapCompletable { granted ->
                        if (granted) {
                            Completable.complete()
                        } else {
                            Completable.error(SecurityException("Permission denied"))
                        }
                    }
            else -> Completable.error(Exception("No activity to check permission"))
        }
    }


}
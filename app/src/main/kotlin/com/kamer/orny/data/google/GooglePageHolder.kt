package com.kamer.orny.data.google

import com.kamer.orny.data.google.model.GooglePage
import io.reactivex.Observable


interface GooglePageHolder {

    fun getPage(): Observable<GooglePage>

}
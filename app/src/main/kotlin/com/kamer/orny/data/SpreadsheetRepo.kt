package com.kamer.orny.data

import io.reactivex.Single


interface SpreadsheetRepo {

    fun getData(): Single<List<String>>

}
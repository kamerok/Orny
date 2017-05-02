package com.kamer.orny.data

import com.kamer.orny.data.google.GoogleRepo
import io.reactivex.Single


class SpreadsheetRepoImpl(private val googleRepo: GoogleRepo) : SpreadsheetRepo {

    override fun getData(): Single<List<String>> = googleRepo.getData()

}
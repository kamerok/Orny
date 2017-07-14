package com.kamer.orny.data.domain

import com.kamer.orny.data.google.GoogleRepo
import com.kamer.orny.di.app.ApplicationScope
import javax.inject.Inject


@ApplicationScope
class SpreadsheetRepoImpl @Inject constructor(
        private val googleRepo: GoogleRepo
) : SpreadsheetRepo {


}
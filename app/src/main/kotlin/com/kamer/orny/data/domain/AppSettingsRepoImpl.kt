package com.kamer.orny.data.domain

import com.kamer.orny.data.android.Prefs
import com.kamer.orny.data.domain.model.Author
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.functions.BiFunction
import io.reactivex.subjects.BehaviorSubject
import javax.inject.Inject


class AppSettingsRepoImpl @Inject constructor(
        val prefs: Prefs,
        val pageRepo: PageRepo
) : AppSettingsRepo {

    private val selectedIdSubject = BehaviorSubject.create<String>().apply { onNext(prefs.defaultAuthorId) }

    override fun getDefaultAuthor(): Observable<Author> = pageRepo
            .getPageAuthors()
            .zipWith(
                    selectedIdSubject,
                    BiFunction { authors, id ->
                        return@BiFunction if (id.isEmpty()) {
                            Author.EMPTY_AUTHOR
                        } else {
                            authors.filter { it.id == id }.first()
                        }
                    })

    override fun setDefaultAuthor(author: Author): Completable = Completable
            .fromAction {
                prefs.defaultAuthorId = author.id
                selectedIdSubject.onNext(author.id)
            }

}
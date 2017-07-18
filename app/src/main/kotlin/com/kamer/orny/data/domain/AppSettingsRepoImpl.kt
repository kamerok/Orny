package com.kamer.orny.data.domain

import com.kamer.orny.data.android.Prefs
import com.kamer.orny.data.domain.model.Author
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import javax.inject.Inject


class AppSettingsRepoImpl @Inject constructor(
        val prefs: Prefs,
        val pageRepo: PageRepo
) : AppSettingsRepo {

    private val subject = BehaviorSubject.create<Author>().apply {
        pageRepo
                .getPageAuthors()
                .subscribe { authors ->
                    val selectedId = prefs.defaultAuthorId
                    if (selectedId.isEmpty()) {
                        onNext(Author.EMPTY_AUTHOR)
                    } else {
                        onNext(authors.filter { it.id == selectedId }.first())
                    }
                }
    }

    override fun getDefaultAuthor(): Observable<Author> = subject

    override fun setDefaultAuthor(author: Author): Completable = Completable
            .fromAction {
                prefs.defaultAuthorId = author.id
                subject.onNext(author)
            }

}
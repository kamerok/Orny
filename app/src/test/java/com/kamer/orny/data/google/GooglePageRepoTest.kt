package com.kamer.orny.data.google

import com.kamer.orny.data.google.model.GooglePage
import com.nhaarman.mockito_kotlin.verifyNoMoreInteractions
import com.nhaarman.mockito_kotlin.whenever
import io.reactivex.Single
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import java.util.*
import java.util.concurrent.TimeUnit

@RunWith(MockitoJUnitRunner::class)
class GooglePageRepoTest {

    @Mock lateinit var googleRepo: GoogleRepo

    lateinit var repo: GooglePageRepo

    @Before
    fun setUp() {
        whenever(googleRepo.getPage()).thenReturn(Single.just(googlePage()))
        repo = GooglePageRepoImpl(googleRepo)
    }

    @Test
    fun noInteractions() {
        verifyNoMoreInteractions(googleRepo)
    }

    @Test
    fun twoSimultaneouslyUpdatesShouldCallRepoOneTime() {
        var subscribeCount = 0
        whenever(googleRepo.getPage()).thenReturn(Single.just(googlePage()).delay(1, TimeUnit.SECONDS).doOnSubscribe { subscribeCount++ })

        repo.updatePage().subscribe()
        repo.updatePage().subscribe()

        assertThat(subscribeCount).isEqualTo(1)
    }

    @Test
    fun twoUpdatesShouldCallRepoTwoTimes() {
        var subscribeCount = 0
        whenever(googleRepo.getPage()).thenReturn(Single.just(googlePage()).doOnSubscribe { subscribeCount++ })

        repo.updatePage().subscribe()
        repo.updatePage().subscribe()

        assertThat(subscribeCount).isEqualTo(2)
    }

    @Test
    fun updateAndGetPageShouldCallRepoOneTime() {
        var subscribeCount = 0
        whenever(googleRepo.getPage()).thenReturn(Single.just(googlePage()).delay(1, TimeUnit.SECONDS).doOnSubscribe { subscribeCount++ })

        repo.getPage().subscribe()
        repo.updatePage().subscribe()

        assertThat(subscribeCount).isEqualTo(1)
    }

    @Test
    fun getPageShouldCallRepoFirstTime() {
        var subscribeCount = 0
        whenever(googleRepo.getPage()).thenReturn(Single.just(googlePage()).doOnSubscribe { subscribeCount++ })

        repo.getPage().subscribe()

        assertThat(subscribeCount).isEqualTo(1)
    }

    @Test
    fun getPageShouldNotCallRepoSecondTime() {
        var subscribeCount = 0
        whenever(googleRepo.getPage()).thenReturn(Single.just(googlePage()).doOnSubscribe { subscribeCount++ })

        repo.getPage().subscribe()
        repo.getPage().subscribe()

        assertThat(subscribeCount).isEqualTo(1)
    }

    @Test
    fun getPageShouldNotCallRepoAfterUpdate() {
        var subscribeCount = 0
        whenever(googleRepo.getPage()).thenReturn(Single.just(googlePage()).doOnSubscribe { subscribeCount++ })

        repo.updatePage().subscribe()
        repo.getPage().subscribe()

        assertThat(subscribeCount).isEqualTo(1)
    }

    @Test
    fun updatePageValues() {
        val page1 = GooglePage(0.1, 0, Date(), emptyList())
        val page2 = GooglePage(0.2, 0, Date(), emptyList())
        var first = true
        whenever(googleRepo.getPage()).thenReturn(Single.fromCallable {
            if (first) {
                first = false
                page1
            } else {
                page2
            }
        })

        val testObserver = repo.getPage().test()
        repo.updatePage().subscribe()

        testObserver.assertValues(page1, page2)
    }

    fun googlePage() = GooglePage(0.0, 0, Date(), emptyList())
}
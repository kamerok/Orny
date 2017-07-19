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
class GooglePageHolderTest {

    @Mock lateinit var googleRepo: GoogleRepo

    private lateinit var holder: GooglePageHolder

    @Before
    fun setUp() {
        whenever(googleRepo.getPage()).thenReturn(Single.just(googlePage()))
        holder = GooglePageHolderImpl(googleRepo)
    }

    @Test
    fun noInteractions() {
        verifyNoMoreInteractions(googleRepo)
    }

    @Test
    fun twoSimultaneouslyUpdatesShouldCallRepoOneTime() {
        var subscribeCount = 0
        whenever(googleRepo.getPage()).thenReturn(Single.just(googlePage()).delay(1, TimeUnit.SECONDS).doOnSubscribe { subscribeCount++ })

        holder.updatePage().subscribe()
        holder.updatePage().subscribe()

        assertThat(subscribeCount).isEqualTo(1)
    }

    @Test
    fun twoUpdatesShouldCallRepoTwoTimes() {
        var subscribeCount = 0
        whenever(googleRepo.getPage()).thenReturn(Single.just(googlePage()).doOnSubscribe { subscribeCount++ })

        holder.updatePage().subscribe()
        holder.updatePage().subscribe()

        assertThat(subscribeCount).isEqualTo(2)
    }

    @Test
    fun updateAndGetPageShouldCallRepoOneTime() {
        var subscribeCount = 0
        whenever(googleRepo.getPage()).thenReturn(Single.just(googlePage()).delay(1, TimeUnit.SECONDS).doOnSubscribe { subscribeCount++ })

        holder.getPage().subscribe()
        holder.updatePage().subscribe()

        assertThat(subscribeCount).isEqualTo(1)
    }

    @Test
    fun getPageShouldCallRepoFirstTime() {
        var subscribeCount = 0
        whenever(googleRepo.getPage()).thenReturn(Single.just(googlePage()).doOnSubscribe { subscribeCount++ })

        holder.getPage().subscribe()

        assertThat(subscribeCount).isEqualTo(1)
    }

    @Test
    fun getPageShouldNotCallRepoSecondTime() {
        var subscribeCount = 0
        whenever(googleRepo.getPage()).thenReturn(Single.just(googlePage()).doOnSubscribe { subscribeCount++ })

        holder.getPage().subscribe()
        holder.getPage().subscribe()

        assertThat(subscribeCount).isEqualTo(1)
    }

    @Test
    fun getPageShouldNotCallRepoAfterUpdate() {
        var subscribeCount = 0
        whenever(googleRepo.getPage()).thenReturn(Single.just(googlePage()).doOnSubscribe { subscribeCount++ })

        holder.updatePage().subscribe()
        holder.getPage().subscribe()

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

        val testObserver = holder.getPage().test()
        holder.updatePage().subscribe()

        testObserver.assertValues(page1, page2)
    }

    private fun googlePage() = GooglePage(0.0, 0, Date(), emptyList())
}
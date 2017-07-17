package com.kamer.orny.presentation.settings

import com.kamer.orny.interaction.model.DefaultAuthor
import com.kamer.orny.interaction.settings.AppSettingsInteractor
import com.kamer.orny.presentation.core.ErrorMessageParser
import com.nhaarman.mockito_kotlin.any
import io.reactivex.Completable
import io.reactivex.Single
import org.junit.Before
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner


@RunWith(MockitoJUnitRunner::class)
class AppSettingsViewModelTest {

    companion object {
        const val PARSED_ERROR = "Error message"
    }

    @Mock lateinit var interactor: AppSettingsInteractor
    @Mock lateinit var errorParser: ErrorMessageParser

    private lateinit var viewModel: AppSettingsViewModel

    @Before
    fun setUp() {
        `when`(interactor.getDefaultAuthor()).thenReturn(Single.just(DefaultAuthor()))
        `when`(interactor.saveDefaultAuthor(any())).thenReturn(Completable.complete())

        viewModel = AppSettingsViewModelImpl(errorParser, interactor)
    }


}
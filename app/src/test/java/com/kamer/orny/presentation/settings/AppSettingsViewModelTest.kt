package com.kamer.orny.presentation.settings

import android.arch.lifecycle.LiveData
import com.kamer.orny.data.domain.model.Author
import com.kamer.orny.interaction.model.AuthorsWithDefault
import com.kamer.orny.interaction.settings.AppSettingsInteractor
import com.kamer.orny.presentation.core.ErrorMessageParser
import com.kamer.orny.presentation.settings.errors.LoadAuthorException
import com.kamer.orny.presentation.settings.errors.SaveAuthorException
import com.kamer.orny.utils.TestUtils
import com.kamer.orny.utils.getResultValue
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.argumentCaptor
import com.nhaarman.mockito_kotlin.verify
import io.reactivex.Completable
import io.reactivex.Single
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner
import kotlin.reflect.KClass


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
        TestUtils.setupLiveDataExecutor()
        `when`(errorParser.getMessage(any())).thenReturn(PARSED_ERROR)
        `when`(interactor.getAuthorsWithDefault()).thenReturn(Single.just(AuthorsWithDefault()))
        `when`(interactor.saveDefaultAuthor(any())).thenReturn(Completable.complete())
    }

    @Test
    fun requestDefaultAuthorOnStart() {
        createViewModel()

        verify(interactor).getAuthorsWithDefault()
    }

    @Test
    fun showLoadingProgress() {
        `when`(interactor.getAuthorsWithDefault()).thenReturn(Single.never())
        createViewModel()

        assertThat(viewModel.loadingStream.getResultValue()).isTrue()
    }

    @Test
    fun hideLoadingProgressOnSuccess() {
        createViewModel()

        assertThat(viewModel.loadingStream.getResultValue()).isFalse()
    }

    @Test
    fun hideLoadingProgressOnError() {
        `when`(interactor.getAuthorsWithDefault()).thenReturn(Single.error(Exception()))
        createViewModel()

        assertThat(viewModel.loadingStream.getResultValue()).isFalse()
    }

    @Test
    fun postLoadedAuthor() {
        val author = AuthorsWithDefault()
        `when`(interactor.getAuthorsWithDefault()).thenReturn(Single.just(author))
        createViewModel()

        assertThat(viewModel.modelStream.getResultValue()).isEqualTo(author)
    }

    @Test
    fun showLoadingError() {
        `when`(interactor.getAuthorsWithDefault()).thenReturn(Single.error(Exception()))
        createViewModel()

        assertError(viewModel.errorStream, LoadAuthorException::class)
    }

    @Test
    fun saveAuthorWhenSelected() {
        val author = Author("", 0, "", "")
        val captor = argumentCaptor<Author>()

        createViewModel()
        viewModel.authorSelected(author)

        verify(interactor).saveDefaultAuthor(captor.capture())
        assertThat(captor.firstValue).isEqualTo(author)
    }

    @Test
    fun showSaveAuthorError() {
        `when`(interactor.saveDefaultAuthor(any())).thenReturn(Completable.error(Exception()))

        createViewModel()
        viewModel.authorSelected(Author("", 0, "", ""))

        assertError(viewModel.errorStream, SaveAuthorException::class)
    }

    private fun createViewModel() {
        viewModel = AppSettingsViewModelImpl(errorParser, interactor)
    }

    private fun assertError(errorStream: LiveData<String>, errorClass: KClass<out Exception>) {
        val captor = argumentCaptor<Exception>()
        val errorResult = errorStream.getResultValue()

        verify(errorParser).getMessage(captor.capture())
        assertThat(captor.firstValue).isInstanceOf(errorClass.java)
        assertThat(errorResult).isEqualTo(PARSED_ERROR)
    }
}
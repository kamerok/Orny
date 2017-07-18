package com.kamer.orny.presentation.settings

import android.arch.lifecycle.MutableLiveData
import com.kamer.orny.data.domain.model.Author
import com.kamer.orny.interaction.model.AuthorsWithDefault
import com.kamer.orny.interaction.settings.AppSettingsInteractor
import com.kamer.orny.presentation.core.BaseViewModel
import com.kamer.orny.presentation.core.ErrorMessageParser
import com.kamer.orny.presentation.core.SingleLiveEvent
import com.kamer.orny.presentation.settings.errors.LoadAuthorException
import com.kamer.orny.presentation.settings.errors.SaveAuthorException
import javax.inject.Inject


class AppSettingsViewModelImpl @Inject constructor(
        val errorParser: ErrorMessageParser,
        val interactor: AppSettingsInteractor
) : BaseViewModel(), AppSettingsViewModel {

    override val modelStream: MutableLiveData<AuthorsWithDefault> = MutableLiveData()
    override val loadingStream: MutableLiveData<Boolean> = MutableLiveData()
    override val errorStream: SingleLiveEvent<String> = SingleLiveEvent()

    init {
        interactor
                .getAuthorsWithDefault()
                .disposeOnDestroy()
                .doOnSubscribe { loadingStream.value = true }
                .doFinally { loadingStream.value = false }
                .subscribe({
                    modelStream.value = it
                }, {
                    errorStream.value = errorParser.getMessage(LoadAuthorException(it))
                })
    }

    override fun authorSelected(author: Author) {
        interactor
                .saveDefaultAuthor(author)
                .disposeOnDestroy()
                .subscribe({}, {
                    errorStream.value = errorParser.getMessage(SaveAuthorException(it))
                })
    }
}
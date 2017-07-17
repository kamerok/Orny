package com.kamer.orny.presentation.settings

import android.arch.lifecycle.MutableLiveData
import com.kamer.orny.data.domain.model.Author
import com.kamer.orny.interaction.model.DefaultAuthor
import com.kamer.orny.interaction.settings.AppSettingsInteractor
import com.kamer.orny.presentation.core.BaseViewModel
import com.kamer.orny.presentation.core.ErrorMessageParser
import com.kamer.orny.presentation.core.SingleLiveEvent
import javax.inject.Inject


class AppSettingsViewModelImpl @Inject constructor(
        val errorParser: ErrorMessageParser,
        val interactor: AppSettingsInteractor
) : BaseViewModel(), AppSettingsViewModel {

    override val modelStream: MutableLiveData<DefaultAuthor> = MutableLiveData()
    override val loadingStream: MutableLiveData<Boolean> = MutableLiveData()
    override val errorStream: SingleLiveEvent<String> = SingleLiveEvent()

    override fun authorSelected(author: Author) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
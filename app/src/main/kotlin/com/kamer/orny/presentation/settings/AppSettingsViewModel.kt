package com.kamer.orny.presentation.settings

import android.arch.lifecycle.LiveData
import com.kamer.orny.data.domain.model.Author
import com.kamer.orny.interaction.model.AuthorsWithDefault
import com.kamer.orny.presentation.core.SingleLiveEvent


interface AppSettingsViewModel {
    val modelStream: LiveData<AuthorsWithDefault>
    val loadingStream: LiveData<Boolean>
    val errorStream: SingleLiveEvent<String>

    fun authorSelected(author: Author)
}
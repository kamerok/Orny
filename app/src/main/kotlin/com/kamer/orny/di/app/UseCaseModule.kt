package com.kamer.orny.di.app

import com.kamer.orny.interaction.common.GetAuthorsWithDefaultSingleUseCase
import com.kamer.orny.interaction.common.GetAuthorsWithDefaultSingleUseCaseImpl
import dagger.Binds
import dagger.Module

@Module
abstract class UseCaseModule {

    @Binds
    @ApplicationScope
    abstract fun bindGetAuthorsUseCase(useCase: GetAuthorsWithDefaultSingleUseCaseImpl): GetAuthorsWithDefaultSingleUseCase

}
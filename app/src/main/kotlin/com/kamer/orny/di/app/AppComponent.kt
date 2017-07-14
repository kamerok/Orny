package com.kamer.orny.di.app

import com.kamer.orny.app.App
import dagger.BindsInstance
import dagger.Component
import dagger.android.support.AndroidSupportInjectionModule


@ApplicationScope
@Component(modules = arrayOf(
        AndroidSupportInjectionModule::class,
        BuildersModule::class,
        AppModule::class,
        UtilsModule::class,
        DataModule::class))
interface AppComponent {

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: App): Builder

        fun build(): AppComponent
    }

    fun inject(app: App)

}

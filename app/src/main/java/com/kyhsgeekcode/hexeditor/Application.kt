package com.kyhsgeekcode.hexeditor

import android.app.Application
import com.kyhsgeekcode.hexeditor.di.RepositoryModule.repositoryModule
import com.kyhsgeekcode.hexeditor.di.ViewModelModule.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import timber.log.Timber

class Application : Application() {
    override fun onCreate() {
        super.onCreate()


        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        // Start Koin
        startKoin {
            androidContext(this@Application)
            modules(repositoryModule)
            modules(viewModelModule)
        }
    }
}

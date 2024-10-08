package com.example.avcsocketconnectionexample

import android.app.Application
import com.example.avcsocketconnectionexample.di.appModule
import com.example.avcsocketconnectionexample.socket.AvcSdkManagerProvider
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class SampleApp: Application() {

    override fun onCreate() {
        super.onCreate()

        AvcSdkManagerProvider.init(this@SampleApp)
        AvcSdkManagerProvider.getManager().prepare("https://vcapp.nites.rs/", true)

        startKoin {
            androidLogger()
            androidContext(this@SampleApp)
            modules(appModule)
        }
    }
}
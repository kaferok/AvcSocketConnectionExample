package com.example.avcsocketconnectionexample.di

import com.app.democall.sdk.manager.api.AvcSdkManager
import com.example.avcsocketconnectionexample.socket.AvcSdkManagerProvider
import org.koin.dsl.module

val appModule = module {
    single { AvcSdkManagerProvider.getManager() }

    single { get<AvcSdkManager>().container.socketManager }
    single { get<AvcSdkManager>().container.chatSdk }
    single { get<AvcSdkManager>().container.userSdk }

    includes(viewModelModule)
}
package com.example.avcsocketconnectionexample.di

import com.example.avcsocketconnectionexample.MainViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

val viewModelModule = module {
    viewModelOf(::MainViewModel)
}
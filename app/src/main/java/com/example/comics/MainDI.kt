package com.example.comics

import com.example.comics.model.Repository
import com.example.comics.viewmodel.ComicsViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module


val repositoryModule = module {
    single{
        Repository()
    }
}

val viewModelModule = module {

    viewModel {
        ComicsViewModel(androidApplication(), get())
    }
}
package com.dubproductions.sudokuchallenge.game.di

import com.dubproductions.sudokuchallenge.game.data.remote.PuzzleApiService
import com.dubproductions.sudokuchallenge.game.data.repository.PuzzleRepositoryImpl
import com.dubproductions.sudokuchallenge.game.domain.repository.PuzzleRepository
import com.dubproductions.sudokuchallenge.game.ui.puzzle.PuzzleScreenViewModel
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

val gameModule = module {
    singleOf(::PuzzleRepositoryImpl) { bind<PuzzleRepository>() }

    viewModelOf(::PuzzleScreenViewModel)

    single {
        Moshi
            .Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
    }

    single {
        Retrofit
            .Builder()
            .baseUrl("https://you-do-sudoku-api.vercel.app/")
            .addConverterFactory(MoshiConverterFactory.create(get()))
            .build()
    }

    single {
        get<Retrofit>().create(PuzzleApiService::class.java)
    }
}
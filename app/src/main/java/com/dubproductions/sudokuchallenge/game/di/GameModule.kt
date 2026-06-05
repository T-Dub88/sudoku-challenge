package com.dubproductions.sudokuchallenge.game.di

import com.dubproductions.sudokuchallenge.game.data.remote.PuzzleApiService
import com.dubproductions.sudokuchallenge.game.data.repository.PuzzleRepositoryImpl
import com.dubproductions.sudokuchallenge.game.domain.repository.PuzzleRepository
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.create
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object GameModule {
    @Provides
    @Singleton
    fun provideMoshi(): Moshi {
        return Moshi
            .Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(moshi: Moshi): Retrofit {
        return Retrofit
            .Builder()
            .baseUrl("https://you-do-sudoku-api.vercel.app/")
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }

    @Provides
    @Singleton
    fun providePuzzleApiService(retrofit: Retrofit): PuzzleApiService {
        return retrofit.create<PuzzleApiService>()
    }

    @Provides
    @Singleton
    fun providePuzzleRepository(puzzleApiService: PuzzleApiService): PuzzleRepository {
        return PuzzleRepositoryImpl(
            puzzleApiService = puzzleApiService
        )
    }
}

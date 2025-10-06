package com.dubproductions.sudokuchallenge

import android.app.Application
import com.dubproductions.sudokuchallenge.game.di.gameModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@MainApplication)
            modules(gameModule)
        }
    }
}
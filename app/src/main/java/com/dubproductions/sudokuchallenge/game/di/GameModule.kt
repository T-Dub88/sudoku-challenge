package com.dubproductions.sudokuchallenge.game.di

import com.dubproductions.sudokuchallenge.game.data.repository.PuzzleRepositoryImpl
import com.dubproductions.sudokuchallenge.game.domain.repository.PuzzleRepository
import com.dubproductions.sudokuchallenge.game.ui.puzzle.PuzzleScreenViewModel
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val gameModule = module {
    singleOf(::PuzzleRepositoryImpl) { bind<PuzzleRepository>() }
    viewModelOf(::PuzzleScreenViewModel)
}
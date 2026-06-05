package com.dubproductions.sudokuchallenge.game.ui.nav

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.dubproductions.sudokuchallenge.core.ui.nav.Navigator
import com.dubproductions.sudokuchallenge.game.ui.complete.CompleteScreen
import com.dubproductions.sudokuchallenge.game.ui.menu.MenuScreen
import com.dubproductions.sudokuchallenge.game.ui.puzzle.PuzzleScreen

fun EntryProviderScope<NavKey>.gameNavEntryProvider(navigator: Navigator) {
    entry<GameNavRoute.MenuScreen> {
        MenuScreen(
            navigateToPuzzleScreen = { difficulty ->
                navigator.navigate(GameNavRoute.PuzzleScreen(difficulty))
            }
        )
    }

    entry<GameNavRoute.PuzzleScreen> { navArguments ->
        PuzzleScreen(
            difficulty = navArguments.difficulty,
            onGameComplete = { playTime, mistakeCount ->
                navigator.navigate(GameNavRoute.CompleteScreen(playTime, mistakeCount))
            },
            onError = {}
        )
    }

    entry<GameNavRoute.CompleteScreen> { navArguments ->
        CompleteScreen(
            playTime = navArguments.playTime,
            mistakeCount = navArguments.mistakeCount
        )
    }
}
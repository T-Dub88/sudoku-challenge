package com.dubproductions.sudokuchallenge.game.ui.nav

import androidx.navigation3.runtime.NavKey
import com.dubproductions.sudokuchallenge.game.domain.puzzle.Difficulty
import kotlinx.serialization.Serializable

@Serializable
sealed interface GameNavRoute : NavKey {
    @Serializable
    data object MenuScreen : GameNavRoute

    @Serializable
    data class PuzzleScreen(
        val difficulty: Difficulty
    ) : GameNavRoute

    @Serializable
    data class CompleteScreen(
        val playTime: Long,
        val mistakeCount: Int
    ) : GameNavRoute
}

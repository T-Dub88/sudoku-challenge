package com.dubproductions.sudokuchallenge.game.ui.menu

import androidx.lifecycle.ViewModel
import com.dubproductions.sudokuchallenge.game.domain.puzzle.Difficulty
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class MenuScreenViewModel : ViewModel() {
    private val _selectedDifficulty = MutableStateFlow(Difficulty.EASY)
    val selectedDifficulty = _selectedDifficulty.asStateFlow()

    private val _showNewGameDialog = MutableStateFlow(false)
    val showNewGameDialog = _showNewGameDialog.asStateFlow()

    fun updateSelectedDifficultyState(newDifficulty: Difficulty) {
        _selectedDifficulty.value = newDifficulty
    }

    fun updateNewGameDialogVisibility(visible: Boolean) {
        _showNewGameDialog.value = visible
    }
}
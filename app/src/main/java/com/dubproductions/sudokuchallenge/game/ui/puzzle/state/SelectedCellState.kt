package com.dubproductions.sudokuchallenge.game.ui.puzzle.state

data class SelectedCellState(
    val selectedNumber: Int? = null,
    val selectedRow: Int? = null,
    val selectedColumn: Int? = null
)

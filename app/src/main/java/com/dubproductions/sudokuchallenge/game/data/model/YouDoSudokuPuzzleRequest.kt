package com.dubproductions.sudokuchallenge.game.data.model

data class YouDoSudokuPuzzleRequest(
    val difficulty: String,
    val solution: Boolean = true,
    val array: Boolean = true
)
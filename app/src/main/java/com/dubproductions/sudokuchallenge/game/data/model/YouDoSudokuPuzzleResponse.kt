package com.dubproductions.sudokuchallenge.game.data.model

data class YouDoSudokuPuzzleResponse(
    val puzzle: List<List<Int>>,
    val solution: List<List<Int>>,
    val difficulty: String
)
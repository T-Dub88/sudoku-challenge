package com.dubproductions.sudokuchallenge.game.domain.board

import com.dubproductions.sudokuchallenge.game.domain.puzzle.Difficulty

data class Board(
    val puzzleGrid: List<List<Cell>>,
    val solutionGrid: List<List<Cell>>,
    val difficulty: Difficulty
)
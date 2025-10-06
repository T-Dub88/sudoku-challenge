package com.dubproductions.sudokuchallenge.game.domain.repository

import com.dubproductions.sudokuchallenge.game.domain.board.Board
import com.dubproductions.sudokuchallenge.game.domain.puzzle.Difficulty

interface PuzzleRepository {
    suspend fun fetchPuzzle(difficulty: Difficulty): Board
}
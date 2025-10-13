package com.dubproductions.sudokuchallenge.game.domain.repository

import com.dubproductions.sudokuchallenge.game.domain.board.Board
import com.dubproductions.sudokuchallenge.game.domain.puzzle.Difficulty
import com.dubproductions.sudokuchallenge.game.domain.util.Result

interface PuzzleRepository {
    suspend fun fetchPuzzle(difficulty: Difficulty): Result<Board>
}
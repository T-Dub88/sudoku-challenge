package com.dubproductions.sudokuchallenge.game.data.repository

import com.dubproductions.sudokuchallenge.game.domain.board.Board
import com.dubproductions.sudokuchallenge.game.domain.board.Cell
import com.dubproductions.sudokuchallenge.game.domain.board.CellValue
import com.dubproductions.sudokuchallenge.game.domain.puzzle.Difficulty
import com.dubproductions.sudokuchallenge.game.domain.repository.PuzzleRepository

class PuzzleRepositoryImpl : PuzzleRepository {
    override suspend fun fetchPuzzle(difficulty: Difficulty): Board {
        return Board(
            puzzleGrid = listOf(
                listOf(Cell(), Cell(), Cell(), Cell(), Cell(), Cell(), Cell(), Cell(), Cell()),
                listOf(Cell(), Cell(), Cell(), Cell(), Cell(), Cell(), Cell(), Cell(), Cell()),
                listOf(Cell(), Cell(), Cell(), Cell(), Cell(), Cell(), Cell(), Cell(), Cell()),
                listOf(Cell(), Cell(), Cell(), Cell(), Cell(), Cell(), Cell(), Cell(), Cell()),
                listOf(Cell(), Cell(), Cell(), Cell(), Cell(), Cell(), Cell(), Cell(), Cell()),
                listOf(Cell(), Cell(), Cell(), Cell(), Cell(), Cell(), Cell(), Cell(), Cell()),
                listOf(Cell(), Cell(), Cell(), Cell(), Cell(), Cell(), Cell(), Cell(), Cell()),
                listOf(Cell(), Cell(), Cell(), Cell(), Cell(), Cell(), Cell(), Cell(), Cell()),
                listOf(Cell(), Cell(), Cell(), Cell(), Cell(), Cell(), Cell(), Cell(), Cell()),
            ),
            solutionGrid = listOf(
                listOf(Cell(answer = CellValue.ONE), Cell(), Cell(), Cell(), Cell(), Cell(), Cell(), Cell(), Cell()),
                listOf(Cell(), Cell(), Cell(), Cell(), Cell(), Cell(), Cell(), Cell(), Cell()),
                listOf(Cell(), Cell(), Cell(), Cell(), Cell(), Cell(), Cell(), Cell(), Cell()),
                listOf(Cell(), Cell(), Cell(), Cell(), Cell(), Cell(), Cell(), Cell(), Cell()),
                listOf(Cell(), Cell(), Cell(), Cell(), Cell(), Cell(), Cell(), Cell(), Cell()),
                listOf(Cell(), Cell(), Cell(), Cell(), Cell(), Cell(), Cell(), Cell(), Cell()),
                listOf(Cell(), Cell(), Cell(), Cell(), Cell(), Cell(), Cell(), Cell(), Cell()),
                listOf(Cell(), Cell(), Cell(), Cell(), Cell(), Cell(), Cell(), Cell(), Cell()),
                listOf(Cell(), Cell(), Cell(), Cell(), Cell(), Cell(), Cell(), Cell(), Cell()),
            ),
            difficulty = Difficulty.EASY
        )
    }
}
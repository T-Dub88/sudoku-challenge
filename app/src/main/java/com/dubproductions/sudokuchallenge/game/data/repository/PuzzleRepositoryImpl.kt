package com.dubproductions.sudokuchallenge.game.data.repository

import android.util.Log
import com.dubproductions.sudokuchallenge.game.data.model.YouDoSudokuPuzzleRequest
import com.dubproductions.sudokuchallenge.game.data.model.YouDoSudokuPuzzleResponse
import com.dubproductions.sudokuchallenge.game.data.remote.PuzzleApiService
import com.dubproductions.sudokuchallenge.game.domain.board.Board
import com.dubproductions.sudokuchallenge.game.domain.board.Cell
import com.dubproductions.sudokuchallenge.game.domain.board.CellValue
import com.dubproductions.sudokuchallenge.game.domain.puzzle.Difficulty
import com.dubproductions.sudokuchallenge.game.domain.repository.PuzzleRepository

class PuzzleRepositoryImpl(
    val puzzleApiService: PuzzleApiService
) : PuzzleRepository {
    override suspend fun fetchPuzzle(difficulty: Difficulty): Board {
        val puzzleRequest = YouDoSudokuPuzzleRequest(
            difficulty.text.lowercase()
        )
        val response = puzzleApiService.getPuzzle(puzzleRequest)

        val boardFromResponse = translateServerResponseToBoard(response)

        Log.i("API response", "fetchPuzzle: $response")

        return boardFromResponse
    }

    private fun translateServerResponseToBoard(serverResponse: YouDoSudokuPuzzleResponse): Board {
        val newBoard = Board(
            puzzleGrid = createGrid(serverResponse.puzzle),
            solutionGrid = createGrid(serverResponse.solution),
            difficulty = Difficulty.entries.single { it.text.lowercase() == serverResponse.difficulty.lowercase() }
        )

        return newBoard
    }

    private fun createGrid(responsePuzzle: List<List<Int>>): List<List<Cell>> {
        return responsePuzzle.map { responseRow ->
            responseRow.map { responseCellValue ->
                Cell(
                    answer = CellValue.entries.single { it.numericValue == responseCellValue },
                )
            }
        }
    }
}
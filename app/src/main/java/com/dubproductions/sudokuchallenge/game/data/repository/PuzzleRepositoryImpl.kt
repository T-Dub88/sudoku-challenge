package com.dubproductions.sudokuchallenge.game.data.repository

import com.dubproductions.sudokuchallenge.game.data.model.YouDoSudokuPuzzleRequest
import com.dubproductions.sudokuchallenge.game.data.model.YouDoSudokuPuzzleResponse
import com.dubproductions.sudokuchallenge.game.data.remote.PuzzleApiService
import com.dubproductions.sudokuchallenge.game.domain.board.Board
import com.dubproductions.sudokuchallenge.game.domain.board.Cell
import com.dubproductions.sudokuchallenge.game.domain.board.CellValue
import com.dubproductions.sudokuchallenge.game.domain.puzzle.Difficulty
import com.dubproductions.sudokuchallenge.game.domain.repository.PuzzleRepository
import com.dubproductions.sudokuchallenge.game.domain.util.Result

class PuzzleRepositoryImpl(
    val puzzleApiService: PuzzleApiService
) : PuzzleRepository {
    override suspend fun fetchPuzzle(difficulty: Difficulty): Result<Board> {
        val puzzleRequest = YouDoSudokuPuzzleRequest(
            difficulty.text.lowercase()
        )

        return try {
            val response = puzzleApiService.getPuzzle(puzzleRequest)

            if (response.isSuccessful && response.body() != null) {
                val translatedPuzzle = translateServerResponseToBoard(response.body()!!)
                Result.Success(translatedPuzzle)
            } else {
                Result.Error(response.message())
            }
        } catch (e: Exception) {
            Result.Error(e.toString())
        }
    }

    private fun translateServerResponseToBoard(serverResponse: YouDoSudokuPuzzleResponse): Board {
        val newBoard = Board(
            puzzleGrid = createGrid(serverResponse.puzzle),
            solutionGrid = createGrid(serverResponse.solution),
            difficulty = Difficulty.entries.single { it.text.equals(serverResponse.difficulty, ignoreCase = true) }
        )

        return newBoard
    }

    private fun createGrid(responsePuzzle: List<List<Int>>): List<List<Cell>> {
        return responsePuzzle.map { responseRow ->
            responseRow.map { responseCellValue ->
                Cell(
                    isGiven = responseCellValue != 0,
                    answer = CellValue.entries.single { it.numericValue == responseCellValue },
                )
            }
        }
    }
}
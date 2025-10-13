package com.dubproductions.sudokuchallenge.game.data.remote

import com.dubproductions.sudokuchallenge.game.data.model.YouDoSudokuPuzzleRequest
import com.dubproductions.sudokuchallenge.game.data.model.YouDoSudokuPuzzleResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface PuzzleApiService {
    @POST("api")
    suspend fun getPuzzle(
        @Body request: YouDoSudokuPuzzleRequest
    ): Response<YouDoSudokuPuzzleResponse>
}
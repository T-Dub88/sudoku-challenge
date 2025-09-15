package com.dubproductions.sudokuchallenge.game.domain.board

data class Cell(
    val answer: CellValue = CellValue.EMPTY,
    val notes: List<CellValue> = listOf()
)
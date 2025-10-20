package com.dubproductions.sudokuchallenge.game.domain.board

data class Cell(
    val isGiven: Boolean = false,
    val answer: CellValue = CellValue.EMPTY,
    val notes: Set<CellValue> = setOf()
)
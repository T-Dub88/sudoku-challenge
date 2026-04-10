package com.dubproductions.sudokuchallenge.game.ui.puzzle

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dubproductions.sudokuchallenge.game.domain.board.Board
import com.dubproductions.sudokuchallenge.game.domain.board.Cell
import com.dubproductions.sudokuchallenge.game.domain.board.CellValue
import com.dubproductions.sudokuchallenge.game.domain.puzzle.Difficulty
import com.dubproductions.sudokuchallenge.game.domain.repository.PuzzleRepository
import com.dubproductions.sudokuchallenge.game.domain.util.Result
import com.dubproductions.sudokuchallenge.game.ui.puzzle.state.GameState
import com.dubproductions.sudokuchallenge.game.ui.puzzle.state.SelectedCellState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PuzzleScreenViewModel(
    private val puzzleRepository: PuzzleRepository
) : ViewModel() {
    private var timerStartTime = System.currentTimeMillis()
    private var savedTime = 0L

    private val previousBoardStateCache = ArrayDeque(listOf<Board>())
    private lateinit var solutionString: String

    private val _boardState = MutableStateFlow(
        Board(
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
            difficulty = Difficulty.EASY
        )
    )
    val boardState = _boardState.asStateFlow()

    private val _selectedCellCoordinates = MutableStateFlow(SelectedCellState())
    val selectedCellCoordinates = _selectedCellCoordinates.asStateFlow()

    private val _playTime = MutableStateFlow(0L)
    val playTime = _playTime.asStateFlow()

    private val _isPaused = MutableStateFlow(false)
    val isPaused = _isPaused.asStateFlow()

    private val _isInLockedMode = MutableStateFlow(false)
    val isInLockedMode = _isInLockedMode.asStateFlow()

    private val _selectedNumber = MutableStateFlow<Int?>(null)
    val selectedNumber = _selectedNumber.asStateFlow()

    private val _isInNotesMode = MutableStateFlow(false)
    val isInNotesMode  = _isInNotesMode.asStateFlow()

    private val _gameState = MutableStateFlow(GameState.LOADING)
    val gameState = _gameState.asStateFlow()

    private val _mistakeCount = MutableStateFlow(0)
    val mistakeCount = _mistakeCount.asStateFlow()

    private var timer: Job? = null

    init {
        viewModelScope.launch {
            val result = puzzleRepository.fetchPuzzle(Difficulty.EASY)

            when (result) {
                is Result.Success -> {
                    _boardState.update {
                        result.data
                    }

                    solutionString = convertBoardToString(result.data.solutionGrid)

                    _gameState.update {
                        GameState.PLAYING
                    }

                    startTimer()
                }
                is Result.Error -> {
                    _gameState.update {
                        GameState.ERROR
                    }
                }
            }
        }
    }

    fun toggleNotesMode() {
        _isInNotesMode.update {
            !it
        }
    }

    fun updatedSelectedCell(num: Int?, row: Int?, column: Int?, keepRowColumn: Boolean = false) {
        _selectedCellCoordinates.update {
            if (keepRowColumn) {
                it.copy(selectedNumber = num)
            } else {
                it.copy(
                    selectedNumber = num,
                    selectedRow = row,
                    selectedColumn = column
                )
            }
        }
    }

    fun updateSelectedNumber(newNum: Int?) {
        _selectedNumber.update {
            newNum
        }
        updatedSelectedCell(newNum, null, null, true)
    }

    fun resolveNumberPress(pressedNum: Int?) {
        if (isInLockedMode.value) {
            updateSelectedNumber(pressedNum)
            return
        }

        if (isInNotesMode.value) {
            updateCellNotes(pressedNum)
        } else {
            updatedCellNumber(pressedNum)
        }
    }

    fun resolveCellPress(num: Int?, row: Int?, colum: Int?) {
        updatedSelectedCell(num, row, colum)
        if (isInLockedMode.value && selectedNumber.value != null) {
            if (isInNotesMode.value) {
                updateCellNotes(selectedNumber.value)
            } else {
                updatedCellNumber(selectedNumber.value)
            }
        }
    }

    fun toggleLockedMode() {
        _isInLockedMode.update {
            if (it) updateSelectedNumber(null)
            !it
        }
    }

    fun updatedCellNumber(newNum: Int?) {
        _boardState.update { oldBoard ->
            cacheBoardForUndo(oldBoard)

            val modifiedRowNum = selectedCellCoordinates.value.selectedRow
            val modifiedColumnNum = selectedCellCoordinates.value.selectedColumn

            modifiedRowNum?.let { oldRowNum ->
                modifiedColumnNum?.let { oldColumnNum ->
                    val oldRow = oldBoard.puzzleGrid[oldRowNum]
                    val oldCell = oldRow[oldColumnNum]

                    if (oldCell.isGiven) return

                    val solutionRow = oldBoard.solutionGrid[oldRowNum]
                    val solutionCell = solutionRow[oldColumnNum]

                    if (solutionCell.answer.numericValue != newNum) updateMistakeCount()

                    val newCell = oldCell.copy(
                        answer = convertNumToCellValue(newNum)
                    )

                    val newRow = oldRow.mapIndexed { column, cell ->
                        val newNotesList = cell.notes.toMutableSet()
                        newNotesList.remove(convertNumToCellValue(newNum))

                        if (column == oldColumnNum) {
                            newCell
                        } else {
                            cell.copy(
                                notes = newNotesList
                            )
                        }
                    }

                    val newBoard = oldBoard.copy(
                        puzzleGrid = oldBoard.puzzleGrid.mapIndexed { row, unchangedRow ->
                            if (row == oldRowNum) {
                                newRow
                            } else {
                                unchangedRow.mapIndexed { column, unchangedCell ->
                                    if (column == oldColumnNum) {
                                        val newNotesList = unchangedCell.notes.toMutableSet()
                                        newNotesList.remove(convertNumToCellValue(newNum))
                                        unchangedCell.copy(
                                            notes = newNotesList
                                        )
                                    } else {
                                        unchangedCell
                                    }
                                }
                            }
                        }
                    )
                    newBoard
                }
            } ?: oldBoard
        }
        updatedSelectedCell(newNum, null, null, true)
        checkForWin()
    }

    private fun checkForWin() {
        val currentBoard = convertBoardToString(boardState.value.puzzleGrid)

        if (solutionString == currentBoard) {
            timer?.cancel()
            _gameState.update {
                GameState.WIN
            }
        }
    }

    private fun convertBoardToString(grid: List<List<Cell>>): String {
        return grid.joinToString(separator = "") { row ->
            row.joinToString(separator = "") { cell ->
                cell.answer.toString()
            }
        }
    }

    private fun updateMistakeCount() {
        _mistakeCount.update {
            it + 1
        }
    }

    fun updateCellNotes(newNum: Int?) {
        _boardState.update { oldBoard ->
            cacheBoardForUndo(oldBoard)

            val modifiedRowNum = selectedCellCoordinates.value.selectedRow
            val modifiedColumnNum = selectedCellCoordinates.value.selectedColumn

            modifiedRowNum?.let { oldRowNum ->
                modifiedColumnNum?.let { oldColumnNum ->
                    val oldRow = oldBoard.puzzleGrid[oldRowNum]
                    val oldCell = oldRow[oldColumnNum]

                    val notesList = if (newNum == null) {
                        setOf()
                    } else {
                        oldCell.notes
                    }.toMutableSet()

                    if (newNum != null) {
                        val noteToAdd = CellValue.entries.first { it.numericValue == newNum }
                        val noteAdded = notesList.add(noteToAdd)

                        if (!noteAdded) {
                            notesList.remove(noteToAdd)
                        }
                    }

                    val newCell = oldCell.copy(
                        notes = notesList
                    )

                    val newRow = oldRow.mapIndexed { column, cell ->
                        if (column == oldColumnNum) {
                            newCell
                        } else {
                            cell
                        }
                    }

                    val newBoard = oldBoard.copy(
                        puzzleGrid = oldBoard.puzzleGrid.mapIndexed { row, unchangedRow ->
                            if (row == oldRowNum) {
                                newRow
                            } else {
                                unchangedRow
                            }
                        }
                    )

                    newBoard
                }
            } ?: oldBoard
        }
        updatedSelectedCell(newNum, null, null, true)
    }

    private fun convertNumToCellValue(num: Int?): CellValue {
        return num?.let { notNullNum ->
            val possibleCellValue = CellValue.entries
            possibleCellValue.find { it.numericValue == notNullNum }
        } ?: CellValue.EMPTY
    }

    private fun updateTimerState() {
        _playTime.update {
            savedTime + (System.currentTimeMillis() - timerStartTime) / 1000
        }
    }

    private fun updateIsPausedState() {
        _isPaused.update {
            !it
        }
    }

    fun toggleGamePause() {
        val isCurrentlyPaused = isPaused.value

        updateIsPausedState()

        if (isCurrentlyPaused) {
            startTimer()
        } else {
            stopTimer()
        }
    }

    private fun startTimer() {
        timer?.cancel()
        timerStartTime = System.currentTimeMillis()
        timer = viewModelScope.launch {
            while (true) {
                delay(1000)
                updateTimerState()
            }
        }
    }

    private fun stopTimer() {
        timer?.cancel()
        savedTime += (System.currentTimeMillis() - timerStartTime) / 1000
    }

    private fun cacheBoardForUndo(oldBoard: Board) {
        if (previousBoardStateCache.size == 10) {
            previousBoardStateCache.removeFirst()
        }

        previousBoardStateCache.add(oldBoard)
    }

    fun undoPreviousAction() {
        if (previousBoardStateCache.isEmpty()) return

        val previousBoard = previousBoardStateCache.removeLast()

        _boardState.update {
            previousBoard
        }
    }

    fun calculateRegionSelection(
        selectedRow: Int?,
        selectedColumn: Int?,
        cellRow: Int,
        cellColumn: Int
    ): Boolean {
        val selectedRegionRow = selectedRow?.div(3)
        val selectedRegionColumn = selectedColumn?.div(3)

        val cellsRegionRow = cellRow / 3
        val cellsRegionColumn = cellColumn / 3

        return selectedRegionRow == cellsRegionRow && selectedRegionColumn == cellsRegionColumn
    }
}
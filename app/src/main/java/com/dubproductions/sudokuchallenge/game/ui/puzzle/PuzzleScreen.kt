package com.dubproductions.sudokuchallenge.game.ui.puzzle

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Notes
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.CheckCircleOutline
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonGroupDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dubproductions.sudokuchallenge.game.domain.board.Board
import com.dubproductions.sudokuchallenge.game.domain.board.CellValue
import com.dubproductions.sudokuchallenge.game.ui.puzzle.state.GameState
import com.dubproductions.sudokuchallenge.game.ui.puzzle.state.SelectedCellState
import org.koin.androidx.compose.koinViewModel

@Composable
fun PuzzleScreen() {
    val viewModel = koinViewModel<PuzzleScreenViewModel>()

    val board by viewModel.boardState.collectAsStateWithLifecycle()
    val selectedCellCoordinates by viewModel.selectedCellCoordinates.collectAsStateWithLifecycle()
    val playTime by viewModel.playTime.collectAsStateWithLifecycle()
    val isPaused by viewModel.isPaused.collectAsStateWithLifecycle()
    val selectedNumber by viewModel.selectedNumber.collectAsStateWithLifecycle()
    val isInLockedMode by viewModel.isInLockedMode.collectAsStateWithLifecycle()
    val isInNotesMode by viewModel.isInNotesMode.collectAsStateWithLifecycle()
    val gameState by viewModel.gameState.collectAsStateWithLifecycle()
    val mistakeCount by viewModel.mistakeCount.collectAsStateWithLifecycle()

    when (gameState) {
        GameState.LOADING -> MyLoadingIndicator()
        GameState.PLAYING -> {
            PuzzleScreenContent(
                board = board,
                selectedCell = selectedCellCoordinates,
                playTime = playTime,
                isPaused = isPaused,
                selectedNumber = selectedNumber,
                isInLockedMode = isInLockedMode,
                isInNotesMode = isInNotesMode,
                onCellSelected = viewModel::resolveCellPress,
                onNumberPressed = viewModel::resolveNumberPress,
                onPausePressed = viewModel::toggleGamePause,
                calculateRegionSelection = viewModel::calculateRegionSelection,
                onDeletePressed = {
                    viewModel.updatedCellNumber(null)
                    viewModel.updateCellNotes(null)
                },
                onUndoPressed = viewModel::undoPreviousAction,
                onLockPressed = viewModel::toggleLockedMode,
                onNotesPressed = viewModel::toggleNotesMode
            )
        }
        GameState.WIN -> WinIndicator(
            mistakeCount = mistakeCount,
            playTime = playTime
        )
        GameState.ERROR -> TODO()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WinIndicator(
    mistakeCount: Int,
    playTime: Long
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterVertically)
    ) {
        Text(
            fontSize = 30.sp,
            text = "Puzzle Complete!",
            color = MaterialTheme.colorScheme.onSurface
        )

        Icon(
            modifier = Modifier
                .size(100.dp),
            imageVector = Icons.Default.CheckCircleOutline,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary
        )

        Text(
            fontSize = 20.sp,
            text = playTimeString(playTime, Locale.current.platformLocale),
            color = MaterialTheme.colorScheme.onSurface
        )

        Text(
            fontSize = 20.sp,
            text = "Mistake Count: $mistakeCount",
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun MyLoadingIndicator() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LoadingIndicator()
        Text(
            text = "Loading Puzzle",
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

private fun playTimeString(playTime: Long, locale: java.util.Locale): String {
    val minutesElapsed = playTime / 60
    val secondsElapsed = playTime % 60

    return "Play time: ${String.format(
        locale,
        "%02d:%02d",
        minutesElapsed,
        secondsElapsed
    )}"
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun PuzzleScreenContent(
    board: Board,
    selectedCell: SelectedCellState,
    playTime: Long,
    isPaused: Boolean,
    isInLockedMode: Boolean,
    isInNotesMode: Boolean,
    selectedNumber: Int?,
    onCellSelected: (num: Int, row: Int, column: Int) -> Unit,
    onNumberPressed: (Int) -> Unit,
    onPausePressed: () -> Unit,
    calculateRegionSelection: (Int?, Int?, Int, Int) -> Boolean,
    onDeletePressed: () -> Unit,
    onUndoPressed: () -> Unit,
    onLockPressed: () -> Unit,
    onNotesPressed: () -> Unit
) {
    Scaffold { scaffoldPadding ->
        Column(
            modifier = Modifier
                .padding(scaffoldPadding)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = playTimeString(playTime, Locale.current.platformLocale),
                    color = MaterialTheme.colorScheme.onSurface
                )
                IconButton(
                    onClick = onPausePressed,
                ) {
                    Icon(
                        imageVector = if (isPaused) {
                            Icons.Default.PlayArrow
                        } else {
                            Icons.Default.Pause
                        },
                        contentDescription = "Pause or resume the game.",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            SudokuBoard(
                board = board,
                selectedCell = selectedCell,
                onCellSelected = onCellSelected,
                isPaused = isPaused,
                isInLockedMode = isInLockedMode,
                calculateRegionSelection = calculateRegionSelection
            )

            OptionsButtonContainer(
                isInLockedMode = isInLockedMode,
                isInNotesMode = isInNotesMode,
                onDeletePressed = onDeletePressed,
                onUndoPressed = onUndoPressed,
                onLockPressed = onLockPressed,
                onNotesPressed = onNotesPressed
            )

            TestButtonGroup(
                onNumberPressed = onNumberPressed,
                selectedNumber = selectedNumber
            )
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun TestButtonGroup(
    onNumberPressed: (Int) -> Unit,
    selectedNumber: Int?
) {
    val numberOptions = (1..9).toList()

    FlowRow(
        modifier = Modifier
            .padding(horizontal = 8.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(ButtonGroupDefaults.ConnectedSpaceBetween),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        numberOptions.forEach { label ->
            ToggleButton(
                checked = selectedNumber == label,
                onCheckedChange = { onNumberPressed(label) },
                shapes =
                    when (label) {
                        0 ->  ButtonGroupDefaults.connectedLeadingButtonShapes()
                        9 -> ButtonGroupDefaults.connectedTrailingButtonShapes()
                        else -> ButtonGroupDefaults.connectedMiddleButtonShapes()
                    },
                modifier = Modifier.semantics { role = Role.RadioButton },
            ) {
                Text(
                    text = label.toString(),
                    fontSize = 36.sp
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun OptionsButtonContainer(
    isInLockedMode: Boolean,
    isInNotesMode: Boolean,
    onDeletePressed: () -> Unit,
    onUndoPressed: () -> Unit,
    onLockPressed: () -> Unit,
    onNotesPressed: () -> Unit
) {
    Row(
        modifier = Modifier
            .padding(horizontal = 8.dp, vertical = 16.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Button(
            onClick = onDeletePressed
        ) {
            Icon(Icons.Default.Delete, null)
        }

        Button(
            onClick = onUndoPressed
        ) {
            Icon(Icons.AutoMirrored.Filled.Undo, null)
        }

        ToggleButton(
            checked = isInLockedMode,
            onCheckedChange = { onLockPressed() }
        ) {
            Icon(
                imageVector = if (isInLockedMode){
                    Icons.Default.LockOpen
                } else {
                    Icons.Default.Lock
                },
                null
            )
        }

        ToggleButton(
            checked = isInNotesMode,
            onCheckedChange = { onNotesPressed() }
        ) {
            Icon(
                imageVector = if (isInNotesMode){
                    Icons.AutoMirrored.Filled.Notes
                } else {
                    Icons.Default.EditNote
                },
                null
            )
        }
    }
}

@Composable
fun SudokuBoard(
    board: Board,
    selectedCell: SelectedCellState,
    isPaused: Boolean,
    isInLockedMode: Boolean,
    onCellSelected: (num: Int, row: Int, column: Int) -> Unit,
    calculateRegionSelection: (Int?, Int?, Int, Int) -> Boolean
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(9),
        modifier = Modifier
            .padding(8.dp)
            .border(2.dp, MaterialTheme.colorScheme.onSurface)
    ) {
        board.puzzleGrid.forEachIndexed { rowNum, row ->
            itemsIndexed(row) { columnNum, cell ->
                SudokuCell(
                    playerAnswer = cell.answer,
                    cellSolution = board.solutionGrid[rowNum][columnNum].answer,
                    notesSet = cell.notes,
                    row = rowNum,
                    column = columnNum,
                    selectedCellNum = selectedCell.selectedNumber,
                    isSameAsSelectedNumber = cell.answer != CellValue.EMPTY && selectedCell.selectedNumber == cell.answer.numericValue,
                    isInSelectedRow = selectedCell.selectedRow == rowNum,
                    isInSelectedColumn = selectedCell.selectedColumn == columnNum,
                    isInSelectedRegion = calculateRegionSelection(selectedCell.selectedRow, selectedCell.selectedColumn, rowNum, columnNum),
                    isPaused = isPaused,
                    isInLockedMode = isInLockedMode,
                    onCellSelected = onCellSelected
                )
            }
        }
    }
}

@Composable
fun SudokuCell(
    playerAnswer: CellValue,
    cellSolution: CellValue,
    notesSet: Set<CellValue>,
    row: Int,
    column: Int,
    selectedCellNum: Int?,
    isSameAsSelectedNumber: Boolean,
    isInSelectedRow: Boolean,
    isInSelectedColumn: Boolean,
    isInSelectedRegion: Boolean,
    isPaused: Boolean,
    isInLockedMode: Boolean,
    onCellSelected: (num: Int, row: Int, column: Int) -> Unit
) {
    val boarderLine = MaterialTheme.colorScheme.onSurface
    val isSelected = isInSelectedRow && isInSelectedColumn
    val isWrongAnswer = playerAnswer != CellValue.EMPTY && playerAnswer != cellSolution
    val highlightRegions = (isInSelectedRegion || isInSelectedColumn || isInSelectedRow) && !isInLockedMode

    val cellColor = when {
        isSelected && !isInLockedMode && isWrongAnswer -> MaterialTheme.colorScheme.error
        isWrongAnswer -> MaterialTheme.colorScheme.errorContainer
        isSelected && !isInLockedMode -> MaterialTheme.colorScheme.tertiaryContainer
        isSameAsSelectedNumber || highlightRegions -> MaterialTheme.colorScheme.secondaryContainer
        else -> MaterialTheme.colorScheme.surfaceContainer
    }

    BoxWithConstraints(
        modifier = Modifier
            .semantics {
                contentDescription = "Row: $row, Column: $column"
            }
            .fillMaxWidth()
            .aspectRatio(1f)
            .drawBehind {
                drawRect(
                    color = cellColor
                )

                // Top
                drawLine(
                    color = boarderLine,
                    start = Offset(0f, 0f),
                    end = Offset(size.width, 0f),
                    strokeWidth = if (row == 3 || row == 6) {
                        5f
                    } else 1f
                )

                // Right
                drawLine(
                    color = boarderLine,
                    start = Offset(size.width, 0f),
                    end = Offset(size.width, size.height),
                    strokeWidth = if (column == 2 || column == 5) {
                        5f
                    } else 1f
                )
            }
            .clickable {
                onCellSelected(playerAnswer.numericValue, row, column)
            },
        contentAlignment = Alignment.Center
    ) {
        if (playerAnswer == CellValue.EMPTY && !isPaused) {
            val miniCellSize = (maxWidth / 3f) - 2.dp

            NotesGrid(
                notesSet = notesSet,
                miniCellSize = miniCellSize,
                isSelected = isSelected,
                selectedCellNum = selectedCellNum,
                isInLockedMode = isInLockedMode,
                highlightRegions = highlightRegions
            )
        }

        if (playerAnswer != CellValue.EMPTY && !isPaused) {
            BasicText(
                modifier = Modifier
                    .padding(2.dp),
                text = playerAnswer.numericValue.toString(),
                autoSize = TextAutoSize.StepBased(),
                style = TextStyle(
                    color = when {
                        isSelected && !isInLockedMode && isWrongAnswer -> MaterialTheme.colorScheme.onError
                        isWrongAnswer -> MaterialTheme.colorScheme.onErrorContainer
                        isSelected && !isInLockedMode -> MaterialTheme.colorScheme.onTertiaryContainer
                        isSameAsSelectedNumber || highlightRegions -> MaterialTheme.colorScheme.onSecondaryContainer
                        else -> MaterialTheme.colorScheme.onSurface
                    }
                )
            )
        }
    }
}

@Composable
fun NotesGrid(
    notesSet: Set<CellValue>,
    miniCellSize: Dp,
    isSelected: Boolean,
    selectedCellNum: Int?,
    isInLockedMode: Boolean,
    highlightRegions: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.SpaceEvenly,
    ) {
        (0..2).forEach { row ->
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                (0..2).forEach { column ->
                    val noteNumber = row * 3 + column + 1
                    val noteExists = notesSet.any { it.numericValue == noteNumber }
                    val cellColor = when {
                        isSelected && !isInLockedMode -> MaterialTheme.colorScheme.tertiaryContainer
                        (noteNumber == selectedCellNum && noteExists) || highlightRegions -> MaterialTheme.colorScheme.secondaryContainer
                        else -> MaterialTheme.colorScheme.surfaceContainer
                    }

                    Box(
                        modifier = Modifier
                            .semantics {
                                contentDescription = "Row: $row, Column: $column"
                            }
                            .width(miniCellSize)
                            .aspectRatio(1f)
                            .drawBehind {
                                drawRect(
                                    color = cellColor,
                                )
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        BasicText(
                            text = if (noteExists) {
                                noteNumber.toString()
                            } else "",
                            autoSize = TextAutoSize.StepBased(),
                            style = TextStyle(
                                color = when {
                                    noteNumber == selectedCellNum -> MaterialTheme.colorScheme.onSecondaryContainer
                                    isSelected && !isInLockedMode -> MaterialTheme.colorScheme.onTertiaryContainer
                                    highlightRegions -> MaterialTheme.colorScheme.onSecondaryContainer
                                    else -> MaterialTheme.colorScheme.onSurface
                                }
                            )
                        )
                    }
                }
            }
        }
    }
}
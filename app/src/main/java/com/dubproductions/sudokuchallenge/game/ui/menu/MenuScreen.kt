package com.dubproductions.sudokuchallenge.game.ui.menu

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dubproductions.sudokuchallenge.game.domain.puzzle.Difficulty

@Composable
fun MenuScreen(
    navigateToPuzzleScreen: (Difficulty) -> Unit,
    menuScreenViewModel: MenuScreenViewModel = viewModel()
) {
    val selectedDifficulty by menuScreenViewModel.selectedDifficulty.collectAsStateWithLifecycle()
    val displayDialog by menuScreenViewModel.showNewGameDialog.collectAsStateWithLifecycle()

    MenuScreenContent(
        displayDifficultyDialog = displayDialog,
        selectedDifficulty = selectedDifficulty,
        onNewGameClick = { menuScreenViewModel.updateNewGameDialogVisibility(true) },
        onDismissDialog = { startGame ->
            menuScreenViewModel.updateNewGameDialogVisibility(false)
            if (startGame) navigateToPuzzleScreen(selectedDifficulty)
        },
        onDifficultySelected = menuScreenViewModel::updateSelectedDifficultyState
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MenuScreenContent(
    displayDifficultyDialog: Boolean,
    selectedDifficulty: Difficulty,
    onNewGameClick: () -> Unit,
    onDismissDialog: (Boolean) -> Unit,
    onDifficultySelected: (Difficulty) -> Unit,
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text("Sudoku Challenge", fontSize = 30.sp)
                },

            )
        }
    ) { scaffoldPadding ->
        Column(
            modifier = Modifier
                .padding(scaffoldPadding)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Best times")
            Text("Easy: 1223")
            Text("Medium: 2312")
            Text("Hard: 32243")
            Text("Expert: 54353")

            Button(
                onClick = onNewGameClick
            ) {
                Text("New Game")
            }
        }
    }

    if (displayDifficultyDialog) {
        AlertDialog(
            title = {
                Text("Select difficulty")
            },
            text = {
                DifficultyRadioGroup(
                    selectedDifficulty = selectedDifficulty,
                    onDifficultySelected = onDifficultySelected
                )
            },
            onDismissRequest = { onDismissDialog(false) },
            confirmButton = {
                TextButton(
                    onClick = { onDismissDialog(true) }
                ) {
                    Text("Confirm")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { onDismissDialog(false) }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun DifficultyRadioGroup(
    selectedDifficulty: Difficulty,
    onDifficultySelected: (Difficulty) -> Unit
) {
    Column(
        modifier = Modifier
            .selectableGroup(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Difficulty.entries.forEach { difficulty ->
            Row(
                modifier = Modifier
                    .selectable(
                        selected = difficulty == selectedDifficulty,
                        role = Role.RadioButton,
                        onClick = { onDifficultySelected(difficulty) }
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = difficulty == selectedDifficulty,
                    onClick = null,
                )

                Text(
                    modifier = Modifier
                        .padding(start = 16.dp),
                    text = difficulty.text
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MenuScreenPreview() {
    MenuScreenContent(
        displayDifficultyDialog = true,
        selectedDifficulty = Difficulty.EASY,
        onNewGameClick = {},
        onDismissDialog = {},
        onDifficultySelected = {}
    )
}
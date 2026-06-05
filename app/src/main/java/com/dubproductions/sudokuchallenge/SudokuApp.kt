package com.dubproductions.sudokuchallenge

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.dubproductions.sudokuchallenge.core.ui.nav.Navigator
import com.dubproductions.sudokuchallenge.game.ui.nav.GameNavRoute
import com.dubproductions.sudokuchallenge.game.ui.nav.gameNavEntryProvider
import kotlin.collections.listOf

@Composable
fun SudokuApp() {
    val navBackStack = rememberNavBackStack(GameNavRoute.MenuScreen)
    val navigator = remember { Navigator(navBackStack) }

    NavDisplay(
        backStack = navBackStack,
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator()
        ),
        entryProvider = entryProvider {
            gameNavEntryProvider(navigator)
        }
    )
}

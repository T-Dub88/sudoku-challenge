package com.dubproductions.sudokuchallenge.core.ui.nav

import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey

class Navigator(private val backStack: NavBackStack<NavKey>) {
    fun navigate(key: NavKey) { backStack.add(key) }

    fun goBack() { backStack.removeLastOrNull() }

    fun removeAndNavigate(key: NavKey) {
        backStack.removeLastOrNull()
        backStack.add(key)
    }
}

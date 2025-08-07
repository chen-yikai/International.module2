package dev.eliaschen.internationalmodule2.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class NavController : ViewModel() {
    private val initScreen = Screen.Home
    var currentNav by mutableStateOf(initScreen)
    var navStack = mutableStateListOf<Screen>()

    init {
        navStack.add(initScreen)
    }

    fun navTo(screen: Screen) {
        currentNav = screen
        navStack.add(screen)
    }

    fun pop() {
        navStack.removeLast()
        currentNav = navStack.last()
    }
}

enum class Screen {
    Home, Game, Setting, Rank
}
package dev.eliaschen.rebuildinternationalmodule2.models

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class NavController : ViewModel() {
    private val initScreen = Screen.Home

    var currentNav by mutableStateOf(initScreen)
    var navStack = mutableStateListOf(initScreen)

    fun navTo(screen: Screen) {
        currentNav = screen
        navStack.add(screen)
    }

    fun pop() {
        if (navStack.size > 1) {
            navStack.removeLast()
            currentNav = navStack.last()
        }
    }
}

enum class Screen {
    Home, Game, Setting, Rankings
}
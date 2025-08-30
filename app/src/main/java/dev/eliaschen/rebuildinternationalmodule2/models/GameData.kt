package dev.eliaschen.rebuildinternationalmodule2.models

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel

class GameData(private val context: Application) : AndroidViewModel(context) {
    var playerName by mutableStateOf("")
    var time by mutableIntStateOf(0)
    var coin by mutableIntStateOf(0)
    var gameSuspend by mutableStateOf(false)
}
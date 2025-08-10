package dev.eliaschen.internationalmodule2.model

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel

class GameData(private val context: Application) : AndroidViewModel(context) {
    val config: SharedPreferences = context.getSharedPreferences("app", Context.MODE_PRIVATE)

    var playerName by mutableStateOf("")
    var score by mutableIntStateOf(0)
    var time by mutableIntStateOf(0)
    var gameOver by mutableStateOf(false)

    init {
        playerName = config.getString(LocalConfig.PlayerName.name, "") ?: ""
    }

    fun save() {
        config.edit().putString(LocalConfig.PlayerName.name, playerName).apply()
    }

    fun resetGame() {
        gameOver = false
        score = 0
        time = 0
    }
}

enum class LocalConfig {
    PlayerName,
    Score,
    Time,
    GameOver
}
package dev.eliaschen.internationalmodule2.model

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel

class GameData(private val context: Application) : AndroidViewModel(context) {
    var playerName by mutableStateOf("Player Name")
}
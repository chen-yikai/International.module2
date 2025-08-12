package dev.eliaschen.internationalmodule2.model

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.AndroidViewModel
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.util.UUID

data class Rank(
    val id: Long,
    val createdAt: Long,
    val name: String,
    val coin: Int,
    val duration: Int
)

class GameData(private val context: Application) : AndroidViewModel(context) {
    private val config: SharedPreferences =
        context.getSharedPreferences("app", Context.MODE_PRIVATE)
    private val file = File(context.getExternalFilesDir(null), "rankings.json")
    val rankings = mutableStateListOf<Rank>()

    var playerColorHue by mutableFloatStateOf(0f)

    var playerName by mutableStateOf("")
    var score by mutableIntStateOf(10)
    var time by mutableIntStateOf(0)
    var gameOver by mutableStateOf(false)

    init {
        playerName = config.getString(LocalConfig.PlayerName.name, "") ?: ""
        playerColorHue = config.getFloat(LocalConfig.PlayerColor.name, 0f) ?: 0f
        if (!file.exists()) file.writeText("[]")
        getRank()
    }

    fun saveConfig() {
        config.edit().putString(LocalConfig.PlayerName.name, playerName).apply()
        config.edit().putFloat(LocalConfig.PlayerColor.name, playerColorHue).apply()
    }

    fun resetGame() {
        gameOver = false
        score = 10
        time = 0
    }

    fun addRank() {
        rankings.add(
            Rank(
                id = UUID.randomUUID().mostSignificantBits and Long.MAX_VALUE,
                createdAt = System.currentTimeMillis(),
                name = playerName,
                coin = score,
                duration = time
            )
        )
        saveRank()
    }

    private fun saveRank() {
        val ranks = JSONArray()
        rankings.forEach {
            val rank = JSONObject()
            rank.put("id", it.id)
            rank.put("createdAt", it.createdAt)
            rank.put("name", it.name)
            rank.put("coin", it.coin)
            rank.put("duration", it.duration)
            ranks.put(rank)
        }
        file.writeText(ranks.toString())
    }

    private fun getRank() {
        val jsonString = file.readText()
        val ranks = JSONArray(jsonString)
        rankings.clear()
        rankings.addAll(List(ranks.length()) {
            val rank = ranks.getJSONObject(it)

            return@List Rank(
                id = rank.getLong("id"),
                createdAt = rank.getLong("createdAt"),
                name = rank.getString("name"),
                coin = rank.getInt("coin"),
                duration = rank.getInt("duration")
            )
        })
    }

    fun adjustPlayerColor(originalBitmap: Bitmap, hue: Float, black: Boolean = false): Bitmap {
        val bitmap = originalBitmap.copy(Bitmap.Config.ARGB_8888, true)
        val targetColor = Color.hsv(hue, 0.54f, 0.97f)
        for (width in 0 until bitmap.width) {
            for (height in 0 until bitmap.height) {
                val pixelColor = bitmap.getPixel(width, height)
                if (pixelColor == android.graphics.Color.parseColor("#f87373")) {
                    bitmap.setPixel(
                        width,
                        height,
                        if (black) android.graphics.Color.BLACK else targetColor.toArgb()
                    )
                }
            }
        }
        return bitmap
    }
}

enum class LocalConfig {
    PlayerName,
    PlayerColor
}
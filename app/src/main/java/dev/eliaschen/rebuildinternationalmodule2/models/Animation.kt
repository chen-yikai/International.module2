package dev.eliaschen.rebuildinternationalmodule2.models

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class Animation(initValue: Float, private val velocity: Float) : ViewModel() {
    private var mainJob: Job? = null

    var currentPosition by mutableFloatStateOf(initValue)
    var isAnimating by mutableStateOf(false)

    fun start() {
        if (!isAnimating) {
            mainJob = viewModelScope.launch {
                isAnimating = true
                while (isActive) {
                    currentPosition += velocity * 16
                    delay(16)
                }
            }
        }
    }

    fun pause() {
        mainJob?.cancel()
        isAnimating = false
    }

    fun reset() {

    }
}
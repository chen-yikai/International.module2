package dev.eliaschen.rebuildinternationalmodule2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocal
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModelProvider
import dev.eliaschen.rebuildinternationalmodule2.models.NavController
import dev.eliaschen.rebuildinternationalmodule2.models.Screen
import dev.eliaschen.rebuildinternationalmodule2.screens.HomeScreen
import dev.eliaschen.rebuildinternationalmodule2.ui.theme.RebuildInternationalmodule2Theme

val LocalNavController = compositionLocalOf<NavController> { error("NavController") }

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RebuildInternationalmodule2Theme {
                val nav = ViewModelProvider(this)[NavController::class.java]

                CompositionLocalProvider(LocalNavController provides nav) {
                    when (nav.currentNav) {
                        Screen.Home -> HomeScreen()
                        else -> Box(
                            Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) { Text("Screen ${nav.currentNav} not found") }
                    }
                }
            }
        }
    }
}
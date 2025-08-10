package dev.eliaschen.internationalmodule2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.key
import androidx.lifecycle.ViewModelProvider
import dev.eliaschen.internationalmodule2.model.GameData
import dev.eliaschen.internationalmodule2.model.NavController
import dev.eliaschen.internationalmodule2.model.Screen
import dev.eliaschen.internationalmodule2.screen.GameScreen
import dev.eliaschen.internationalmodule2.screen.HomeScreen
import dev.eliaschen.internationalmodule2.screen.RankScreen
import dev.eliaschen.internationalmodule2.screen.SettingScreen
import dev.eliaschen.internationalmodule2.ui.theme.Internationalmodule2Theme

val LocalNavController = compositionLocalOf<NavController> { error("Nav Controller not found") }
val LocalGameData = compositionLocalOf<GameData> { error("Game Data not found") }

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Internationalmodule2Theme {
                val nav = ViewModelProvider(this)[NavController::class.java]
                val game = ViewModelProvider(this)[GameData::class.java]

                CompositionLocalProvider(
                    LocalNavController provides nav,
                    LocalGameData provides game
                ) {
                    Surface {
                        BackHandler {
                            if (nav.navStack.size > 1) {
                                nav.pop()
                            } else {
                                finish()
                            }
                        }
                        key(nav.reloadKey) {
                            when (nav.currentNav) {
                                Screen.Home -> HomeScreen()
                                Screen.Game -> GameScreen()
                                Screen.Rank -> RankScreen()
                                Screen.Setting -> SettingScreen()
                                else -> Text("error")
                            }
                        }
                    }
                }
            }
        }
    }
}
package dev.eliaschen.rebuildinternationalmodule2.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.eliaschen.rebuildinternationalmodule2.LocalGameData
import dev.eliaschen.rebuildinternationalmodule2.LocalNavController
import dev.eliaschen.rebuildinternationalmodule2.R
import dev.eliaschen.rebuildinternationalmodule2.models.Screen

@Composable
fun HomeScreen() {
    val nav = LocalNavController.current
    val game = LocalGameData.current
    var playerName by remember { mutableStateOf("") }
    var showErrorDialog by remember { mutableStateOf(false) }

    fun startGame() {
        if (playerName.isNotEmpty()) {
            game.playerName = playerName
            nav.navTo(Screen.Game)
        } else {
            showErrorDialog = true
        }
    }

    if (showErrorDialog) {
        AlertDialog(
            onDismissRequest = { showErrorDialog = false },
            title = { Text("Invalid") },
            text = { Text("Player name can't be empty") },
            confirmButton = { TextButton(onClick = { showErrorDialog = false }) { Text("Ok") } })
    }

    val navItems = listOf(
        Pair("Start Game") { startGame() },
        Pair("Rankings") { nav.navTo(Screen.Rankings) },
        Pair("Setting") { nav.navTo(Screen.Game) },
    )

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Image(
            painter = painterResource(R.drawable.bg),
            contentDescription = null,
            contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize(), alpha = 0.5f
        )
        Column(
            modifier = Modifier.padding(30.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text("Go Skiing", fontSize = 30.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(10.dp))
            OutlinedTextField(
                playerName,
                onValueChange = { playerName = it },
                label = { Text("Player name") },
                singleLine = true,
                keyboardActions = KeyboardActions(onDone = { startGame() })
            )
            Spacer(Modifier.height(20.dp))
            navItems.forEach { (label, onClick) ->
                FilledTonalButton(
                    onClick = onClick,
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = Color(0xff9DD4FA)
                    ), modifier = Modifier
                        .padding(vertical = 5.dp)
                        .width(150.dp),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(vertical = 15.dp)
                ) {
                    Text(label)
                }
            }
        }
    }
}
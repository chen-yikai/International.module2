package dev.eliaschen.internationalmodule2.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Button
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.eliaschen.internationalmodule2.LocalNavController
import dev.eliaschen.internationalmodule2.R
import dev.eliaschen.internationalmodule2.model.Screen

@Composable
fun HomeScreen() {
    val nav = LocalNavController.current
    var playerName by remember { mutableStateOf("") }
    val actionButtons = listOf(
        Pair("Start Game") { nav.navTo(Screen.Game) },
        Pair("Rankings") { nav.navTo(Screen.Rank) },
        Pair("Setting") { nav.navTo(Screen.Setting) }
    )

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Image(
            painter = painterResource(R.drawable.bg),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop
        )
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Color.White.copy(alpha = 0.3f)
                )
        ) {
            Text("Go Skiing", fontWeight = FontWeight.Bold, fontSize = 40.sp)
            Spacer(Modifier.height(20.dp))
            OutlinedTextField(
                value = playerName,
                onValueChange = { playerName = it },
                placeholder = { Text("Player Name") })
            Spacer(Modifier.height(30.dp))
            actionButtons.forEach {
                FilledTonalButton(
                    onClick = it.second,
                    shape = RectangleShape,
                    modifier = Modifier
                        .widthIn(max = 200.dp)
                        .fillMaxWidth()
                        .padding(vertical = 10.dp)
                ) { Text(it.first, modifier = Modifier.padding(10.dp)) }
            }
        }
    }
}
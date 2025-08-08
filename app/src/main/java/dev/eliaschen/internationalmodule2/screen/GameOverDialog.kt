package dev.eliaschen.internationalmodule2.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.eliaschen.internationalmodule2.LocalGameData
import dev.eliaschen.internationalmodule2.LocalNavController
import dev.eliaschen.internationalmodule2.R
import dev.eliaschen.internationalmodule2.model.Screen

@Composable
fun GameOverDialog(dismiss: () -> Unit) {
    val nav = LocalNavController.current
    val game = LocalGameData.current

    AlertDialog(onDismissRequest = dismiss, title = { Text("Game Over") }, text = {
        Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
            Text("Player name: ${game.playerName}", style = MaterialTheme.typography.titleMedium)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(R.drawable.coin),
                    contentDescription = null,
                    modifier = Modifier.size(25.dp)
                )
                Spacer(Modifier.width(10.dp))
                Text(
                    game.score.toString(),
                    fontWeight = FontWeight.Bold,
                    color = Color(0xffc9c249), style = MaterialTheme.typography.titleMedium
                )
            }
            Text("Time: ${game.time}s", style = MaterialTheme.typography.titleMedium)
        }
    }, confirmButton = {
        FilledTonalButton(onClick = { nav.navTo(Screen.Rank) }) { Text("Go To Rankings") }
    }, dismissButton = {
        FilledTonalButton(onClick = {
            game.resetGame()
            nav.reloadKey++
        }) { Text("Restart") }
    })
}

@Preview(showBackground = true)
@Composable
fun PreviewGameOverDialog() {
    GameOverDialog { }
}

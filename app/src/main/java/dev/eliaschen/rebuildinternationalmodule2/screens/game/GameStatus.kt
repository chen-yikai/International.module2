package dev.eliaschen.rebuildinternationalmodule2.screens.game

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.eliaschen.rebuildinternationalmodule2.LocalGameData
import dev.eliaschen.rebuildinternationalmodule2.R

@Composable
fun GameStatus(modifier: Modifier = Modifier) {
    val game = LocalGameData.current

    Row(
        modifier = Modifier
            .padding(horizontal = 10.dp, vertical = 5.dp)
            .fillMaxWidth()
            .statusBarsPadding()
            .then(modifier),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        IconButton(onClick = { game.gameSuspend = !game.gameSuspend }) {
            Icon(
                painter = painterResource(if (game.gameSuspend) R.drawable.play else R.drawable.pause),
                contentDescription = null, modifier = Modifier.size(30.dp)
            )
        }
        Column(
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text("Player name", fontWeight = FontWeight.Bold)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Image(
                    painter = painterResource(R.drawable.coin),
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Text("10", color = Color(0xffDECA35), fontWeight = FontWeight.Bold)
            }
            Text("10s")
        }
    }
}
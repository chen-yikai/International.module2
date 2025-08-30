package dev.eliaschen.rebuildinternationalmodule2.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.eliaschen.rebuildinternationalmodule2.LocalGameData
import dev.eliaschen.rebuildinternationalmodule2.LocalNavController
import dev.eliaschen.rebuildinternationalmodule2.R
import dev.eliaschen.rebuildinternationalmodule2.screens.game.GameStatus
import dev.eliaschen.rebuildinternationalmodule2.screens.game.GameTrees

@Composable
fun GameScreen() {
    val nav = LocalNavController.current
    val game = LocalGameData.current

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(R.drawable.bg),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        GameStatus(modifier = Modifier.align(Alignment.TopCenter))
        GameTrees(modifier = Modifier.align(Alignment.BottomCenter))
    }
}

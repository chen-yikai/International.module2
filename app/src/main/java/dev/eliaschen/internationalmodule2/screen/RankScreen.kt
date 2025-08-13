package dev.eliaschen.internationalmodule2.screen

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseInOutBounce
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.compose.ui.unit.sp
import dev.eliaschen.internationalmodule2.LocalGameData
import dev.eliaschen.internationalmodule2.LocalNavController
import dev.eliaschen.internationalmodule2.model.Screen

@Composable
fun RankScreen() {
    val nav = LocalNavController.current
    val game = LocalGameData.current
    val titles = listOf("ranking", "player name", "coin", "duration")
    var recentAdded by remember { mutableLongStateOf(0L) }
    val shine = remember { Animatable(0f) }
    val listState = rememberLazyListState()

    LaunchedEffect(game.rankings) {
        game.rankings.sortByDescending { it.coin }
        if (nav.navStack.takeLast(2).contains(Screen.Game)) {
            recentAdded = game.rankings.maxByOrNull { it.createdAt }!!
                .id
            listState.animateScrollToItem(game.rankings.indexOfFirst { it.id == recentAdded })
            repeat(3) {
                shine.animateTo(
                    if (it == 1) 0f else 1f,
                    animationSpec = tween(durationMillis = 200, easing = EaseInOutBounce)
                )
            }
        }
    }

    Column(
        modifier = Modifier
            .statusBarsPadding()
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
            IconButton(onClick = { nav.navTo(Screen.Home) }) {
                Icon(
                    Icons.Default.ArrowBack,
                    contentDescription = null
                )
            }
        }
        Text("Rankings", fontWeight = FontWeight.Bold, fontSize = 40.sp)
        if (game.rankings.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No Ranking", fontWeight = FontWeight.Bold)
            }
        } else {
            Column(
                modifier = Modifier
                    .padding(horizontal = 10.dp)
                    .padding(top = 20.dp)
            ) {
                Row(modifier = Modifier.padding(vertical = 10.dp)) {
                    titles.forEach { title ->
                        Box(
                            modifier = Modifier
                                .weight(1f, fill = true),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                title,
                                fontWeight = FontWeight.Bold,
                            )
                        }
                    }
                }
                LazyColumn(state = listState) {
                    itemsIndexed(game.rankings) { index, item ->
                        val rank = listOf(index + 1, item.name, item.coin, item.duration)
                        Row(
                            modifier = Modifier
                                .background(
                                    if (recentAdded == item.id) MaterialTheme.colorScheme.secondaryContainer.copy(
                                        alpha = shine.value
                                    ) else Color.Unspecified
                                )
                                .padding(vertical = 10.dp)
                        ) {
                            rank.forEach {
                                Box(
                                    modifier = Modifier
                                        .weight(1f, fill = true),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        it.toString() + if (it == rank.last()) " s" else "",
                                        fontWeight = FontWeight.Bold,
                                    )
                                }
                            }
                        }
                        HorizontalDivider()
                    }
                }
            }
        }
    }
}
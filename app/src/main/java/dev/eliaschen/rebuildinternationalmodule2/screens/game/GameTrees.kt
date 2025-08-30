package dev.eliaschen.rebuildinternationalmodule2.screens.game

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import dev.eliaschen.rebuildinternationalmodule2.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

@Composable
fun GameTrees(modifier: Modifier = Modifier) {
    val device = LocalConfiguration.current
    val screenWidth = device.screenWidthDp.toFloat()
    val velocity = device.screenWidthDp.toFloat() / 3000
    var secondMovement by remember { mutableFloatStateOf(screenWidth) }
    var firstMovement by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(Unit) {
        while (isActive) {
            firstMovement -= 16 * velocity
            secondMovement -= 16 * velocity
            if (firstMovement <= -screenWidth) {
                firstMovement = secondMovement + screenWidth
            }
            if (secondMovement <= -screenWidth) {
                secondMovement = firstMovement + screenWidth
            }
            delay(16)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .then(modifier), contentAlignment = Alignment.BottomCenter
    ) {
        Image(
            painter = painterResource(R.drawable.trees),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .offset(x = firstMovement.dp)
                .height(350.dp)
                .width(device.screenWidthDp.dp)
        )
        Image(
            painter = painterResource(R.drawable.trees),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .offset(x = secondMovement.dp)
                .height(350.dp)
                .width(device.screenWidthDp.dp)
        )
    }
}
package dev.eliaschen.internationalmodule2.screen

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.widget.Toast
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Ease
import androidx.compose.animation.core.EaseIn
import androidx.compose.animation.core.EaseInBack
import androidx.compose.animation.core.EaseInBounce
import androidx.compose.animation.core.EaseInExpo
import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.EaseOutBounce
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.draw
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.scale
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import androidx.compose.ui.zIndex
import dev.eliaschen.internationalmodule2.LocalGameData
import dev.eliaschen.internationalmodule2.LocalNavController
import dev.eliaschen.internationalmodule2.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.lang.Math.toDegrees
import kotlin.math.atan2
import kotlin.math.roundToInt
import kotlin.system.exitProcess

@Composable
fun GameScreen() {
    val nav = LocalNavController.current
    val context = LocalContext.current
    val game = LocalGameData.current
    val device = LocalConfiguration.current
    val scope = rememberCoroutineScope()

    val treesMovement = remember { Animatable(0f) }
    val secondTreeMovement = remember { Animatable(0f) }

    val playerY = remember { Animatable(0f) }

    val firstItem = remember { Animatable(0f) }
    val firstItemObject = remember { mutableStateListOf<Pair<GameObject, Int>>() }
    val objectBucket = remember { mutableStateListOf<Int>() }
    val itemObjects = listOf(firstItemObject)

    var slopeAngle by remember { mutableStateOf(0f) }
    var playerRect by remember { mutableStateOf<Rect>(Rect(Offset.Zero, Size.Zero)) }

    fun getItem(index: Int) {
        itemObjects[index].clear()
        val itemCount = (0..5).random()
        repeat(itemCount) {
            val random = (0..2).random()
            val coin = random == 0 || random == 1
            var pos = 0
            while (true) {
                pos = (4..15).random()
                if (!objectBucket.contains(pos)) {
                    objectBucket.add(pos)
                    break
                }
            }
            itemObjects[index].add(
                Pair(
                    if (coin) GameObject.Coin else GameObject.Obstacle, pos
                )
            )
        }
    }

    LaunchedEffect(Unit) {
        while (isActive) {
            objectBucket.clear()
            getItem(0)
            firstItem.snapTo(0f)
            firstItem.animateTo(
                1f, tween(durationMillis = 3000, easing = LinearEasing)
            )
        }
    }

    LaunchedEffect(Unit) {
        while (isActive) {
            treesMovement.animateTo(
                -device.screenWidthDp.toFloat(), tween(durationMillis = 2000, easing = LinearEasing)
            )
            treesMovement.snapTo(0f)
        }
    }

    LaunchedEffect(Unit) {
        while (isActive) {
            secondTreeMovement.snapTo(device.screenWidthDp.toFloat())
            secondTreeMovement.animateTo(
                0f, tween(durationMillis = 2000, easing = LinearEasing)
            )
        }
    }

    fun jump() {
        scope.launch {
            playerY.animateTo(1f, tween(durationMillis = 200, easing = EaseOut))
            playerY.animateTo(0f, tween(durationMillis = 700, easing = EaseIn))
        }
    }

    Box(modifier = Modifier
        .fillMaxSize()
        .pointerInput(Unit) {
            detectTapGestures {
                jump()
            }
            detectHorizontalDragGestures { change, dragAmount ->
            }
        }) {
        Image(
            painter = painterResource(R.drawable.bg),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Row(
            modifier = Modifier
                .statusBarsPadding()
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 5.dp)
                .align(Alignment.TopCenter),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = {}) {
                Icon(painter = painterResource(R.drawable.play), contentDescription = null)
            }
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(game.playerName, fontWeight = FontWeight.Bold)
                Row {
                    Image(
                        painter = painterResource(R.drawable.coin),
                        contentDescription = null,
                        modifier = Modifier.size(25.dp)
                    )
                    Spacer(Modifier.width(5.dp))
                    Text("10", fontWeight = FontWeight.Bold, color = Color(0xffc9c249))
                }
                Text("0s", fontWeight = FontWeight.Bold)
            }
        }
        repeat(2) {
            Image(
                painter = painterResource(R.drawable.trees),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(x = if (it == 0) treesMovement.value.dp else secondTreeMovement.value.dp)
                    .align(Alignment.BottomStart)
                    .height(350.dp)
                    .width(device.screenWidthDp.dp),
                contentScale = ContentScale.Crop
            )
        }

        val playerBitmap = ImageBitmap.imageResource(R.drawable.skiing_person)
        val coinBitmapOriginal = BitmapFactory.decodeResource(context.resources, R.drawable.coin)
        val coinBitmap = Bitmap.createScaledBitmap(
            coinBitmapOriginal,
            (coinBitmapOriginal.width * 0.1f).toInt(),
            (coinBitmapOriginal.height * 0.1f).toInt(),
            true
        )
        val obstacleBitmap = ImageBitmap.imageResource(R.drawable.obstacle)

        Canvas(
            modifier = Modifier
                .zIndex(2f)
                .fillMaxSize()
        ) {
            val triangleStart = Offset(size.width, size.height - 50f)
            val triangleEnd = Offset(0f, size.height - 230f)

            val dx = triangleEnd.x - triangleStart.x
            val dy = triangleEnd.y - triangleStart.y
            val angleRadians = atan2(dy, dx)
            slopeAngle = toDegrees(angleRadians.toDouble()).toFloat()

            val slopeCenter = (triangleStart + triangleEnd) / 2f
            val scaleFactor = 0.25f
            val playerOffset =
                slopeCenter - Offset(playerBitmap.width / 2f, playerBitmap.height.toFloat())
            val slopeLength = (triangleEnd - triangleStart).getDistance()
            val firstItemW = firstItemObject.sumOf { it.second }
            val space = 100f

            rotate(degrees = slopeAngle - 180f, pivot = triangleStart) {
                translate(left = -(firstItem.value * (slopeLength + space * 15f))) {
                    firstItemObject.forEachIndexed { index, item ->
                        val objectBitmap = when (item.first) {
                            GameObject.Coin -> coinBitmap.asImageBitmap()
                            GameObject.Obstacle -> obstacleBitmap
                        }

                        val sideOffset = if (item.first == GameObject.Coin) -10f else 10f

                        val objectOffset = triangleStart - Offset(
                            0f, objectBitmap.height.toFloat() - sideOffset
                        ) + Offset(item.second.toFloat() * space, 0f)

                        drawImage(image = objectBitmap, topLeft = objectOffset)
                    }
                }
            }

            drawPath(Path().apply {
                moveTo(0f, size.height)
                lineTo(size.width, size.height)
                lineTo(triangleStart.x, triangleStart.y)
                lineTo(triangleEnd.x, triangleEnd.y)
                close()
            }, color = Color.White)

            rotate(degrees = slopeAngle - 180f) {
                scale(
                    scale = scaleFactor, pivot = slopeCenter
                ) {
                    translate(top = -(playerY.value * 400f) / scaleFactor) {
                        drawImage(image = playerBitmap, topLeft = playerOffset)
                    }
                }
            }
        }
    }
}

enum class GameObject {
    Coin, Obstacle
}
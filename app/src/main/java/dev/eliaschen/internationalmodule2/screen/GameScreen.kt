package dev.eliaschen.internationalmodule2.screen

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaPlayer
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseIn
import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.debugInspectorInfo
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import dev.eliaschen.internationalmodule2.LocalGameData
import dev.eliaschen.internationalmodule2.LocalNavController
import dev.eliaschen.internationalmodule2.R
import dev.eliaschen.internationalmodule2.model.Screen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.Math.toDegrees
import kotlin.math.atan2

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun GameScreen() {
    val nav = LocalNavController.current
    val context = LocalContext.current
    val game = LocalGameData.current
    val device = LocalConfiguration.current
    val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    fun vibrate() =
        vibrator.vibrate(
            VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE)
        )


    val scope = rememberCoroutineScope()

    // audio
    val gameBackgroundMusic = remember {
        MediaPlayer.create(context, R.raw.bgm).apply {
            isLooping = true
            setVolume(0.5f, 0.5f)
        }
    }
    val coinSound = remember { MediaPlayer.create(context, R.raw.coin) }
    val jumpSound = remember { MediaPlayer.create(context, R.raw.jump) }
    val gameOverSound = remember { MediaPlayer.create(context, R.raw.game_over) }

    // static value
    val deviceWidth = device.screenWidthDp.toFloat()
    val treeMovementDuration = 3000
    val gameObjectDuration = 4500
    val treeMovementVelocity = deviceWidth / treeMovementDuration

    // object movement
    val treesMovement = remember { Animatable(0f) }
    var oldTreesMovement by remember { mutableFloatStateOf(0f) }

    val secondTreeMovement = remember { Animatable(0f) }
    var oldSecondTreeMovement by remember { mutableFloatStateOf(0f) }

    val gameObject = remember { Animatable(0f) }
    var oldGameObject by remember { mutableFloatStateOf(0f) }

    var gameObjectKey by remember { mutableIntStateOf(0) }
    var treeMovementKey by remember { mutableIntStateOf(0) }
    var secondTreeMovementKey by remember { mutableIntStateOf(0) }

    // player movement
    val playerY = remember { Animatable(0f) }

    val gameObjects = remember { mutableStateListOf<Pair<GameObject, Int>>() }
    val objectBucket = remember { mutableStateListOf<Int>() }
    val itemObjects = listOf(gameObjects)

    var slopeAngle by remember { mutableFloatStateOf(0f) }

    // game status
    var showGameOverDialog by remember { mutableStateOf(false) }
    var showQuitDialog by remember { mutableStateOf(false) }
    var isSuspended by remember { mutableStateOf(false) }
    var speedUp by remember { mutableStateOf(false) }
    var invincibilityMode by remember { mutableStateOf(false) }

    val speedUpDuration = 3000
    val speedUpFactor = 4
    val speedUpVelocity = (deviceWidth / treeMovementDuration) * speedUpFactor

    LaunchedEffect(speedUp) {
        if (speedUp) {
            launch {
                var treePos = treesMovement.value
                val targetPos = -deviceWidth
                treesMovement.stop()
                val startTime = System.currentTimeMillis()
                while (true) {
                    val elapsedTime = System.currentTimeMillis() - startTime
                    treePos -= speedUpVelocity * 16
                    if (elapsedTime >= speedUpDuration) break
                    if (targetPos >= treePos) treePos = 0f
                    treesMovement.snapTo(treePos)
                    delay(16)
                }

                oldTreesMovement = treesMovement.value
                val remainingDistance = oldTreesMovement - (-deviceWidth)
                treesMovement.animateTo(
                    targetValue = -deviceWidth,
                    animationSpec = tween(
                        durationMillis = (remainingDistance / (deviceWidth / treeMovementDuration)).toInt(),
                        easing = LinearEasing
                    )
                )
                treeMovementKey++
                speedUp = false
            }
            launch {
                var treePos = secondTreeMovement.value
                val targetPos = 0f
                secondTreeMovement.stop()
                val startTime = System.currentTimeMillis()
                while (true) {
                    val elapsedTime = System.currentTimeMillis() - startTime
                    treePos -= speedUpVelocity * 16
                    if (elapsedTime >= speedUpDuration) break
                    if (targetPos >= treePos) treePos = deviceWidth
                    secondTreeMovement.snapTo(treePos)
                    delay(16)
                }

                oldSecondTreeMovement = secondTreeMovement.value
                val remainingDistance = oldSecondTreeMovement
                secondTreeMovement.animateTo(
                    targetValue = 0f,
                    animationSpec = tween(
                        durationMillis = (remainingDistance / (deviceWidth / treeMovementDuration)).toInt(),
                        easing = LinearEasing
                    )
                )
                secondTreeMovementKey++
                speedUp = false
            }
        }
    }

    LaunchedEffect(invincibilityMode) {
        while (invincibilityMode && game.score > 0) {
            game.score--
            delay(1000L)
        }
    }

    LaunchedEffect(isSuspended) {
        if (!isSuspended) {
            gameBackgroundMusic.start()
        } else {
            gameBackgroundMusic.pause()
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            gameBackgroundMusic.release()
            coinSound.release()
            jumpSound.release()
            gameOverSound.release()
        }
    }

    LaunchedEffect(Unit) {
        game.resetGame()
        vibrate()
    }

    LaunchedEffect(isSuspended) {
        if (isSuspended) {
            oldTreesMovement = treesMovement.value
            treesMovement.stop()
        } else {
            val remainingDistance = oldTreesMovement - (-deviceWidth)
            treesMovement.animateTo(
                targetValue = -deviceWidth,
                animationSpec = tween(
                    durationMillis = (remainingDistance / (deviceWidth / treeMovementDuration)).toInt(),
                    easing = LinearEasing
                )
            )
            treeMovementKey++
        }
    }

    LaunchedEffect(isSuspended) {
        if (isSuspended) {
            oldSecondTreeMovement = secondTreeMovement.value
            secondTreeMovement.stop()
        } else {
            val remainingDistance = oldSecondTreeMovement
            secondTreeMovement.animateTo(
                targetValue = 0f,
                animationSpec = tween(
                    durationMillis = (remainingDistance / (deviceWidth / treeMovementDuration)).toInt(),
                    easing = LinearEasing
                )
            )
            secondTreeMovementKey++
        }
    }

    LaunchedEffect(isSuspended) {
        if (isSuspended) {
            oldGameObject = gameObject.value
            gameObject.stop()
        } else {
            val remainingDistance = 1f - oldGameObject
            gameObject.animateTo(
                targetValue = 1f,
                animationSpec = tween(
                    durationMillis = (remainingDistance / (1f / gameObjectDuration)).toInt(),
                    easing = LinearEasing
                )
            )
            gameObjectKey++
        }
    }

    fun getItem(index: Int) {
        itemObjects[index].clear()
        val itemCount = (1..5).random()
        val temp = mutableStateListOf<Int>()

        repeat(itemCount) {
            val random = (0..3).random()
            val coin = random == 0 || random == 1 || random == 2
            var pos = 0
            while (true) {
                pos = (5..15).random()
                if (!temp.contains(pos)) {
                    temp.add(pos)
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

    LaunchedEffect(gameObjectKey) {
        while (isActive) {
            objectBucket.clear()
            getItem(0)
            gameObject.snapTo(0f)
            gameObject.animateTo(
                1f, tween(durationMillis = 4500, easing = LinearEasing)
            )
        }
    }

    LaunchedEffect(treeMovementKey) {
        while (isActive) {
            treesMovement.snapTo(0f)
            treesMovement.animateTo(
                -deviceWidth,
                tween(durationMillis = 3000, easing = LinearEasing)
            )
        }
    }

    LaunchedEffect(secondTreeMovementKey) {
        while (isActive) {
            secondTreeMovement.snapTo(deviceWidth)
            secondTreeMovement.animateTo(
                0f, tween(durationMillis = 3000, easing = LinearEasing)
            )
        }
    }

    LaunchedEffect(Unit) {
        while (isActive) {
            if (!game.gameOver && !isSuspended) game.time++
            delay(1000)
        }
    }

    LaunchedEffect(game.gameOver) {
        if (game.gameOver) {
            showGameOverDialog = true
            isSuspended = true
            gameOverSound.start()
            game.addRank()
            vibrate()
        }
    }

    LaunchedEffect(showQuitDialog) {
        isSuspended = showQuitDialog
    }

    fun jump() {
        if (jumpSound.isPlaying) jumpSound.seekTo(0)
        jumpSound.start()
        scope.launch {
            playerY.animateTo(1f, tween(durationMillis = 200, easing = EaseOut))
            playerY.animateTo(0f, tween(durationMillis = 600, easing = EaseIn))
        }
    }

    Box(modifier = Modifier
        .fillMaxSize()
        .pointerInput(Unit) {
            detectHorizontalDragGestures { _, dragAmount ->
                if (dragAmount > 0) showQuitDialog = true
            }
        }
        .pointerInput(Unit) {
            detectVerticalDragGestures { _, dragAmount ->
                if (dragAmount > 0) speedUp = true
            }
        }
        .pointerInput(Unit) {
            detectTapGestures(onTap = {
                if (!isSuspended && !invincibilityMode) {
                    jump()
                }
            }, onPress = {
                delay(500L)
                if (!isSuspended && !speedUp) {
                    invincibilityMode = true
                }
                awaitRelease()
                invincibilityMode = false
            })
        }
    ) {
        if (showGameOverDialog) GameOverDialog() {}
        if (showQuitDialog) {
            AlertDialog(
                onDismissRequest = { },
                title = { Text("Quit Game") },
                text = { Text("The game is in progress. Are you sure to quit?") },
                confirmButton = {
                    Button(onClick = {
                        nav.navTo(Screen.Home)
                    }) { Text("Yes") }
                },
                dismissButton = {
                    FilledTonalButton(onClick = {
                        showQuitDialog = false
                    }) { Text("No") }
                })
        }
        if (isSuspended) Box(
            modifier = Modifier
                .fillMaxSize()
                .zIndex(5f)
                .background(Color.Black.copy(alpha = 0.4f)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                "Game Suspended...",
                color = Color.White,
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold
            )
        }
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
            IconButton(onClick = { isSuspended = !isSuspended }) {
                Icon(
                    painter = painterResource(if (isSuspended) R.drawable.play else R.drawable.pause),
                    contentDescription = null
                )
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
                    Text(
                        game.score.toString(),
                        fontWeight = FontWeight.Bold,
                        color = Color(0xffc9c249)
                    )
                }
                Text("${game.time}s", fontWeight = FontWeight.Bold)
                if (invincibilityMode) Text(
                    "Invincibility Mode",
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .clip(RoundedCornerShape(5.dp))
                        .background(MaterialTheme.colorScheme.secondaryContainer)
                        .padding(5.dp)
                )
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

        val playerBitmapOriginal = remember {
            BitmapFactory.decodeResource(context.resources, R.drawable.skiing_person)
        }
        val playerBitmap = remember(invincibilityMode) {
            game.adjustPlayerColor(
                Bitmap.createScaledBitmap(
                    playerBitmapOriginal,
                    (playerBitmapOriginal.width * 0.12f).toInt(),
                    (playerBitmapOriginal.height * 0.12f).toInt(),
                    true
                ), game.playerColorHue, invincibilityMode
            )
        }
        val coinBitmapOriginal =
            remember { BitmapFactory.decodeResource(context.resources, R.drawable.coin) }
        val coinBitmap = remember {
            Bitmap.createScaledBitmap(
                coinBitmapOriginal,
                (coinBitmapOriginal.width * 0.1f).toInt(),
                (coinBitmapOriginal.height * 0.1f).toInt(),
                true
            )
        }
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
            val playerOffset = slopeCenter - Offset(
                playerBitmap.width / 2f, playerBitmap.height.toFloat()
            ) - Offset(0f, playerY.value * 400f) + Offset(-100f, playerBitmap.height / 2f)

            val slopeLength = (triangleEnd - triangleStart).getDistance()
            val space = 100f
            val playerRect = Rect(
                playerOffset, Size(playerBitmap.width.toFloat(), playerBitmap.height.toFloat())
            )

            rotate(degrees = slopeAngle - 180f, pivot = triangleStart) {
                gameObjects.forEachIndexed { index, item ->
                    val objectBitmap = when (item.first) {
                        GameObject.Coin -> coinBitmap.asImageBitmap()
                        GameObject.Obstacle -> obstacleBitmap
                    }

                    val sideOffset = if (item.first == GameObject.Coin) -10f else 10f

                    val objectOffset = triangleStart - Offset(
                        0f, objectBitmap.height.toFloat() - sideOffset
                    ) + Offset(
                        item.second.toFloat() * space, 0f
                    ) + Offset(-(gameObject.value * (slopeLength + space * 15f)), 0f)

                    val rect = Rect(
                        objectOffset,
                        Size(objectBitmap.width.toFloat(), objectBitmap.height.toFloat())
                    )

                    scope.launch {
                        try {
                            withContext(Dispatchers.IO) {
                                if (playerRect.overlaps(rect)) {
                                    when (item.first) {
                                        GameObject.Coin -> {
                                            if (coinSound.isPlaying) coinSound.seekTo(0)
                                            coinSound.start()
                                            game.score += 10
                                            gameObjects.removeAt(index)
                                        }

                                        GameObject.Obstacle -> {
                                            if (!invincibilityMode) game.gameOver = true
                                        }
                                    }
                                }
                            }
                        } catch (e: Exception) {
                        }
                    }

                    drawRect(
                        color = Color.Transparent, topLeft = rect.topLeft, size = rect.size
                    )

                    drawImage(image = objectBitmap, topLeft = objectOffset)
                }
            }

            drawPath(Path().apply {
                moveTo(0f, size.height)
                lineTo(size.width, size.height)
                lineTo(triangleStart.x, triangleStart.y)
                lineTo(triangleEnd.x, triangleEnd.y)
                close()
            }, color = Color.White)

            rotate(degrees = slopeAngle - 180f, pivot = triangleStart) {
                drawRect(
                    color = Color.Transparent, topLeft = playerRect.topLeft, size = playerRect.size
                )
                drawImage(
                    image = playerBitmap.asImageBitmap(), topLeft = playerOffset
                )
            }
        }
    }
}

enum class GameObject {
    Coin, Obstacle
}
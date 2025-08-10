package dev.eliaschen.internationalmodule2.screen

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import dev.eliaschen.internationalmodule2.LocalGameData
import dev.eliaschen.internationalmodule2.LocalNavController
import dev.eliaschen.internationalmodule2.R
import dev.eliaschen.internationalmodule2.model.Screen

@Composable
fun SettingScreen() {
    val context = LocalContext.current
    val nav = LocalNavController.current
    val game = LocalGameData.current

    val playerBitmapOriginal by remember {
        mutableStateOf(
            BitmapFactory.decodeResource(context.resources, R.drawable.skiing_person)
                .copy(Bitmap.Config.ARGB_8888, true)
        )
    }
    val originalBitmap = Bitmap.createScaledBitmap(
        playerBitmapOriginal,
        (playerBitmapOriginal.width * 0.12f).toInt(),
        (playerBitmapOriginal.height * 0.12f).toInt(),
        true
    )
    var modifiedBitmap by remember { mutableStateOf(originalBitmap) }
    var hueValue by remember { mutableFloatStateOf(0f) }
    var releaseSlider by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        hueValue = game.playerColorHue
    }

    LaunchedEffect(releaseSlider) {
        modifiedBitmap = game.adjustPlayerColor(originalBitmap, hueValue)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 50.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            bitmap = modifiedBitmap.asImageBitmap(),
            contentDescription = "Skiing person",
            modifier = Modifier.size(250.dp)
        )
        Slider(
            value = hueValue,
            onValueChange = {
                releaseSlider = false
                hueValue = it
            },
            valueRange = 0f..360f,
            onValueChangeFinished = {
                releaseSlider = true
            }
        )
        Button(
            onClick = {
                game.playerColorHue = hueValue
                game.saveConfig()
                nav.navTo(Screen.Home)
            },
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier
                .widthIn(max = 200.dp)
                .fillMaxWidth()
                .padding(vertical = 10.dp)
        ) {
            Text("Done", modifier = Modifier.padding(10.dp))
        }
    }
}
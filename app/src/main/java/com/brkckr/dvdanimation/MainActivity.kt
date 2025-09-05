package com.brkckr.dvdanimation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlin.random.Random

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                DVDScreen()
            }
        }
    }
}

@Composable
fun DVDScreen() {
    var dvdVideoSize by remember { mutableStateOf(Size.Zero) } // dvd+video size
    var isSizeReady by remember { mutableStateOf(false) } // is size ready?
    var velocityX by remember { mutableFloatStateOf(5f) }
    var velocityY by remember { mutableFloatStateOf(5f) }
    var textColor by remember { mutableStateOf(Color.Blue) }

    fun randomColor(): Color {
        return Color(
            red = Random.nextFloat(),
            green = Random.nextFloat(),
            blue = Random.nextFloat(),
            alpha = 1f
        )
    }

    val configuration = LocalConfiguration.current
    val screenWidth = with(LocalDensity.current) { configuration.screenWidthDp.dp.toPx() }
    val screenHeight = with(LocalDensity.current) { configuration.screenHeightDp.dp.toPx() }

    var positionX by remember { mutableFloatStateOf(0f) }
    var positionY by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(Unit) {
        while (true) {
            if (isSizeReady) {
                positionX += velocityX // update x position
                positionY += velocityY // update y position

                var colorChanged = false // flag for collision

                // check for collision with left edge
                if (positionX <= 0f) {
                    velocityX = -velocityX // reverse x velocity
                    positionX = 0f // clamp position to left
                    colorChanged = true // trigger color change
                }
                // check for collision with right edge
                else if (dvdVideoSize != Size.Zero && positionX + dvdVideoSize.width >= screenWidth) {
                    velocityX = -velocityX
                    positionX = screenWidth - dvdVideoSize.width
                    colorChanged = true
                }
                // check for collision with top edge
                if (positionY <= 0f) {
                    velocityY = -velocityY // reverse y velocity
                    positionY = 0f
                    colorChanged = true
                }
                // check for collision with bottom edge
                else if (dvdVideoSize != Size.Zero && positionY + dvdVideoSize.height >= screenHeight) {
                    velocityY = -velocityY
                    positionY = screenHeight - dvdVideoSize.height
                    colorChanged = true
                }

                // change color if a collision occurred
                if (colorChanged) {
                    textColor = randomColor()
                }

                delay(16) // wait for 60 fps
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.TopStart
    ) {
        Column(
            modifier = Modifier
                .offset(
                    x = with(LocalDensity.current) { positionX.toDp() },
                    y = with(LocalDensity.current) { positionY.toDp() }
                )
                .onGloballyPositioned { layoutCoordinates ->
                    val size = layoutCoordinates.size
                    if (size.width > 0 && size.height > 0) {
                        dvdVideoSize = Size(size.width.toFloat(), size.height.toFloat())
                        isSizeReady = true
                    }
                },
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            Text(
                text = "DVD",
                style = TextStyle(
                    fontSize = 72.sp,
                    fontStyle = FontStyle.Italic,
                    fontWeight = FontWeight.Bold,
                    color = textColor,
                    letterSpacing = 0.sp
                )
            )
            Text(
                text = "VIDEO",
                color = Color.Black,
                fontSize = 24.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                letterSpacing = 4.sp,
                modifier = Modifier
                    .width(with(LocalDensity.current) { dvdVideoSize.width.toDp() })
                    .drawBehind {
                        drawOval(
                            color = textColor,
                            size = Size(dvdVideoSize.width, size.height)
                        )
                    }
            )
        }
    }
}
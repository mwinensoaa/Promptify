package com.mwinensoaa.promptify.screens

import android.app.Activity
import android.view.WindowManager
import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PromptScreen(
    extractedText: String,
    onBack: () -> Unit
) {

    val scrollState = rememberScrollState()

    val scope = rememberCoroutineScope()

    var fontSize by remember {
        mutableFloatStateOf(30f)
    }

    var scrollSpeed by remember {
        mutableFloatStateOf(3f)
    }

    var isPlaying by remember {
        mutableStateOf(false)
    }

    val context = LocalContext.current

    DisposableEffect(Unit) {

        val activity = context as Activity

        activity.window.addFlags(
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
        )
        onDispose {
            activity.window.clearFlags(
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
            )
        }
    }
    /*
     * AUTO SCROLL
     */

    LaunchedEffect(isPlaying, scrollSpeed) {

        while (isPlaying) {

            scrollState.scrollBy(scrollSpeed)

            // Reset when end reached
            if (
                scrollState.value >=
                scrollState.maxValue
            ) {

                isPlaying = false

                scrollState.animateScrollTo(0)
            }

            delay(16L)
        }
    }

    val playButtonColor by animateColorAsState(
        targetValue =
            if (isPlaying)
                Color(0xFFEF4444)
            else
                Color(0xFF2563EB),

        label = ""
    )

    /*
     * HANDLE BACK BUTTON
     */

    BackHandler {
        onBack()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()

            /*
             * DOUBLE TAP TO PLAY / PAUSE
             */

            .combinedClickable(

                onClick = {

                    // Single tap resumes
                    isPlaying = true
                },

                onDoubleClick = {

                    // Double tap toggles
                    isPlaying = !isPlaying
                }
            )

            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF020617),
                        Color(0xFF0F172A),
                        Color(0xFF111827)
                    )
                )
            )
    ) {

        /*
         * BACKGROUND GLOW
         */

        Box(
            modifier = Modifier
                .size(260.dp)
                .offset(x = (-80).dp, y = (-20).dp)
                .blur(140.dp)
                .background(
                    Color(0xFF2563EB).copy(alpha = 0.25f)
                )
        )

        Box(
            modifier = Modifier
                .size(220.dp)
                .offset(x = 220.dp, y = 650.dp)
                .blur(140.dp)
                .background(
                    Color(0xFF7C3AED).copy(alpha = 0.2f)
                )
        )

        /*
         * PROMPT TEXT
         */



        Text(
            text = extractedText,

            color = Color.White,

            fontSize = fontSize.sp,

            lineHeight = (fontSize + 18).sp,

            letterSpacing = 0.3.sp,

            fontWeight = FontWeight.Medium,

            textAlign = TextAlign.Justify,

            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(
                    horizontal = 28.dp,
                    vertical = 80.dp
                )
                .padding(bottom = 180.dp)
        )

        /*
         * STATUS CHIP
         */

        Surface(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 30.dp),

            shape = RoundedCornerShape(50.dp),

            color = Color.White.copy(alpha = 0.08f)
        ) {

            Text(
                text =
                    if (isPlaying)
                        "SCROLLING"
                    else
                        "PAUSED",

                color = Color.White,

                modifier = Modifier.padding(
                    horizontal = 18.dp,
                    vertical = 10.dp
                ),

                style = MaterialTheme.typography.labelLarge
            )
        }

        /*
         * CONTROLS
         */

        Card(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(
                    horizontal = 16.dp,
                    vertical = 18.dp
                ),

            shape = RoundedCornerShape(30.dp),

            colors = CardDefaults.cardColors(
                containerColor =
                    Color.White.copy(alpha = 0.08f)
            )
        ) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = 18.dp,
                        vertical = 16.dp
                    )
            ) {

                /*
                 * FONT SIZE
                 */

                Row(
                    modifier = Modifier.fillMaxWidth(),

                    horizontalArrangement =
                        Arrangement.SpaceBetween
                ) {

                    Text(
                        text = "Font",

                        color = Color.White.copy(alpha = 0.7f)
                    )

                    Text(
                        text = fontSize.toInt().toString(),

                        color = Color.White
                    )
                }

                Slider(
                    value = fontSize,

                    onValueChange = {
                        fontSize = it
                    },

                    valueRange = 18f..60f
                )

                Spacer(modifier = Modifier.height(8.dp))

                /*
                 * SPEED
                 */

                Row(
                    modifier = Modifier.fillMaxWidth(),

                    horizontalArrangement =
                        Arrangement.SpaceBetween
                ) {

                    Text(
                        text = "Speed",

                        color = Color.White.copy(alpha = 0.7f)
                    )

                    Text(
                        text = scrollSpeed.toInt().toString(),

                        color = Color.White
                    )
                }

                Slider(
                    value = scrollSpeed,

                    onValueChange = {
                        scrollSpeed = it
                    },

                    valueRange = 1f..20f
                )

                Spacer(modifier = Modifier.height(12.dp))

                /*
                 * BUTTONS
                 */

                Row(
                    modifier = Modifier.fillMaxWidth(),

                    horizontalArrangement =
                        Arrangement.Center,

                    verticalAlignment =
                        Alignment.CenterVertically
                ) {

                    FloatingActionButton(
                        onClick = {

                            isPlaying = !isPlaying
                        },

                        containerColor = playButtonColor,

                        shape = CircleShape
                    ) {

                        Icon(
                            imageVector =
                                if (isPlaying)
                                    Icons.Default.Pause
                                else
                                    Icons.Default.PlayArrow,

                            contentDescription = null,

                            tint = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.width(18.dp))

                    FilledTonalIconButton(
                        onClick = {

                            isPlaying = false

                            scope.launch {

                                scrollState.animateScrollTo(0)
                            }
                        },

                        colors =
                            IconButtonDefaults
                                .filledTonalIconButtonColors(
                                    containerColor =
                                        Color.White.copy(alpha = 0.1f)
                                )
                    ) {

                        Icon(
                            imageVector = Icons.Default.Refresh,

                            contentDescription = null,

                            tint = Color.White
                        )
                    }
                }
            }
        }
    }
}




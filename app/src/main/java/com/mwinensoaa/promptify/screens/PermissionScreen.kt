package com.mwinensoaa.promptify.screens

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat

@Composable
fun PermissionScreen(
    onPermissionGranted: () -> Unit
) {

    val context = LocalContext.current

    /*
     * DETERMINE REQUIRED PERMISSION
     */

    val storagePermission = remember {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {

            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }
    }

    /*
     * CHECK IF ALREADY GRANTED
     */

    LaunchedEffect(Unit) {

        val granted =
            ContextCompat.checkSelfPermission(
                context,
                storagePermission
            ) == PackageManager.PERMISSION_GRANTED

        if (granted) {
            onPermissionGranted()
        }
    }

    /*
     * PERMISSION LAUNCHER
     */

    val permissionLauncher =
        rememberLauncherForActivityResult(
            contract =
                ActivityResultContracts.RequestPermission()
        ) { granted ->

            if (granted) {
                onPermissionGranted()
            }
        }

    /*
     * UI
     */

    Box(
        modifier = Modifier
            .fillMaxSize()
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

        Card(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(24.dp),

            shape = RoundedCornerShape(32.dp),

            colors = CardDefaults.cardColors(
                containerColor =
                    Color.White.copy(alpha = 0.08f)
            )
        ) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(28.dp),

                horizontalAlignment =
                    Alignment.CenterHorizontally
            ) {

                /*
                 * ICON
                 */

                Surface(
                    shape = RoundedCornerShape(24.dp),

                    color =
                        Color(0xFF2563EB)
                            .copy(alpha = 0.15f)
                ) {

                    Icon(
                        imageVector =
                            Icons.Default.FolderOpen,

                        contentDescription = null,

                        tint = Color(0xFF60A5FA),

                        modifier = Modifier
                            .padding(22.dp)
                            .size(48.dp)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                /*
                 * TITLE
                 */

                Text(
                    text = "Storage Permission",

                    style =
                        MaterialTheme.typography.headlineSmall,

                    fontWeight = FontWeight.Bold,

                    color = Color.White
                )

                Spacer(modifier = Modifier.height(12.dp))

                /*
                 * DESCRIPTION
                 */

                Text(
                    text =
                        "Allow access to your files so you can import TXT, PDF and Word documents into the Promptify.",

                    style =
                        MaterialTheme.typography.bodyLarge,

                    color = Color.White.copy(alpha = 0.7f),

                    lineHeight = 26.sp
                )

                Spacer(modifier = Modifier.height(32.dp))

                /*
                 * BUTTON
                 */
                Button(
                    onClick = {
                        permissionLauncher.launch(
                            storagePermission
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(58.dp),

                    shape = RoundedCornerShape(18.dp),
                    colors =
                        ButtonDefaults.buttonColors(
                            containerColor =
                                Color(0xFF2563EB)
                        )
                ) {
                    Text(
                        text = "Grant Permission",
                        fontSize = 17.sp
                    )
                }
            }
        }
    }
}
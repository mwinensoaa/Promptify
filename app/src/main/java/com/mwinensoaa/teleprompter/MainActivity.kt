package com.mwinensoaa.teleprompter

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.mwinensoaa.teleprompter.screens.Navigation
import com.mwinensoaa.teleprompter.ui.theme.TeleprompterTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TeleprompterTheme {
                Navigation()
            }
        }
    }
}


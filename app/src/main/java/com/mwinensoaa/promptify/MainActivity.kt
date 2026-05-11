package com.mwinensoaa.promptify

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.mwinensoaa.promptify.screens.Navigation
import com.mwinensoaa.promptify.ui.theme.PromptifyTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            PromptifyTheme {
                Navigation()
            }
        }

    }
}


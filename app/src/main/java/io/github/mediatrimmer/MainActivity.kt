package io.github.mediatrimmer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import io.github.mediatrimmer.demo.VideoTrimmerDemo
import io.github.mediatrimmer.ui.theme.MediaTrimmerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MediaTrimmerTheme {
                VideoTrimmerDemo()
            }
        }
    }
}


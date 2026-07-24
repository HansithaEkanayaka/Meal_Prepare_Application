package com.example.mealappcw

// Demonstration video link :https://drive.google.com/drive/folders/1GDZ8rQYdGlBPMWtpwMGmlOTcwDfypdMe?usp=sharing


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.mealappcw.ui.theme.HomeScreen


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HomeScreen()
        }
    }
}


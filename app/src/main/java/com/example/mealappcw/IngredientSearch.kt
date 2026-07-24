package com.example.mealappcw

import android.os.Bundle
import androidx.activity.ComponentActivity  // ← change this
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.mealappcw.ui.theme.IngredientSearchScreen

class IngredientSearch : ComponentActivity() {  // ← and this
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            IngredientSearchScreen()
        }
    }
}
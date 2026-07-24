package com.example.mealappcw.ui.theme

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.mealappcw.DataBaseSearch
import com.example.mealappcw.IngredientSearch
import com.example.mealappcw.SearchFromWeb
import com.example.mealappcw.data.HardcodedMeals
import com.example.mealappcw.data.MealDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun HomeScreen() {
    val context = LocalContext.current
    // rememberCoroutineScope lets us launch background work from button clicks
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Meal Prepare",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        // Button 1 - Task 2: Save hardcoded meals to the Room database
        Button(
            onClick = {
                // We must do database work on a background thread (Dispatchers.IO)
                // launch starts a coroutine that runs in the background
                scope.launch {
                    val db = MealDatabase.getDatabase(context)
                    val meals = HardcodedMeals.getMeals()
                    db.mealDao().insertMeals(meals)  // save all meals to SQLite

                    // withContext(Dispatchers.Main) switches back to UI thread to show Toast
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "${meals.size} meals added to database!", Toast.LENGTH_SHORT).show()
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            shape = RoundedCornerShape(9.dp)
        ) {
            Text("Add Meals to DB")
        }

        // Button 2 - Task 3 & 4: Search by ingredient from the web, save to DB
        Button(
            onClick = {
                val intent = Intent(context, IngredientSearch::class.java)
                context.startActivity(intent)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            shape = RoundedCornerShape(9.dp)
        ) {
            Text("Search for Meals By Ingredient")
        }

        // Button 3 - Task 5 & 6: Search the local database
        Button(
            onClick = {
                val intent = Intent(context, DataBaseSearch::class.java)
                context.startActivity(intent)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            shape = RoundedCornerShape(9.dp)
        ) {
            Text("Search for Meals from (DB)")
        }

        // Button 4 - Task 7: Search by meal name directly from the web
        Button(
            onClick = {
                val intent = Intent(context, SearchFromWeb::class.java)
                context.startActivity(intent)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            shape = RoundedCornerShape(9.dp)
        ) {
            Text("Search Meals form (WEB)")
        }
    }
}

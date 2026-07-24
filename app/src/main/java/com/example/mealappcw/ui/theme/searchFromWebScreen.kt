package com.example.mealappcw.ui.theme

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.mealappcw.MainActivity
import com.example.mealappcw.data.MealEntity
import com.example.mealappcw.network.MealApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// Task 7: Search for meals by name substring directly from TheMealDB web service
@Composable
fun SearchFromWebScreen() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // rememberSaveable keeps state across rotation (Task 8)
    var userInput by rememberSaveable { mutableStateOf("") }
    var meals by rememberSaveable { mutableStateOf<List<MealEntity>>(emptyList()) }
    var isLoading by rememberSaveable { mutableStateOf(false) }
    var statusMessage by rememberSaveable { mutableStateOf("") }

    LazyColumn(modifier = Modifier.padding(20.dp)) {

        item {
            Button(
                onClick = {
                    val intent = Intent(context, MainActivity::class.java)
                    context.startActivity(intent)
                },
                modifier = Modifier.padding(top = 20.dp)
            ) { Text("Back") }

            Text(
                text = "Search by Name (Web)",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 10.dp, top = 20.dp)
            )

            TextField(
                value = userInput,
                onValueChange = { newValue -> userInput = newValue },
                label = { Text("Enter part of a Meal Name, e.g. Chi") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp, top = 20.dp),
                singleLine = true,
                shape = RoundedCornerShape(10.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surface
                )
            )

            // Task 7: Search button - fetches from web using the meal name substring
            // The API's /search.php?s=CHi will return meals containing "chi" in their name
            Button(
                onClick = {
                    if (userInput.isBlank()) {
                        Toast.makeText(context, "Please enter a meal name", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    meals = emptyList()
                    isLoading = true
                    statusMessage = ""

                    // Network call must be on Dispatchers.IO (not the main thread)
                    scope.launch {
                        val result = withContext(Dispatchers.IO) {
                            MealApiService.searchMealsByName(userInput)
                        }
                        meals = result
                        isLoading = false
                        statusMessage = if (result.isEmpty()) "No meals found for \"$userInput\"" else ""
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Search")
            }

            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.padding(16.dp))
                Text("Searching the web...")
            }

            if (statusMessage.isNotEmpty()) {
                Text(
                    text = statusMessage,
                    modifier = Modifier.padding(top = 8.dp),
                    color = MaterialTheme.colorScheme.error
                )
            }

            if (meals.isNotEmpty()) {
                Text(
                    text = "${meals.size} meal(s) found:",
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 12.dp, bottom = 4.dp)
                )
            }
        }

        // Show all the retrieved meals using the same MealCard composable
        items(meals) { meal ->
            MealCard(meal = meal)
        }
    }
}

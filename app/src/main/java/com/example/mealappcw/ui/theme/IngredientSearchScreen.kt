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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.unit.sp
import com.example.mealappcw.MainActivity
import com.example.mealappcw.data.MealDatabase
import com.example.mealappcw.data.MealEntity
import com.example.mealappcw.network.MealApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun IngredientSearchScreen() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // rememberSaveable keeps these values when the screen rotates (Task 8)
    var userInput by rememberSaveable { mutableStateOf("") }
    var meals by rememberSaveable { mutableStateOf<List<MealEntity>>(emptyList()) }
    var isLoading by rememberSaveable { mutableStateOf(false) }
    var statusMessage by rememberSaveable { mutableStateOf("") }

    // LazyColumn is used so the screen can scroll efficiently even with many meals
    LazyColumn(modifier = Modifier.padding(20.dp)) {

        // All the UI controls go in one item so they scroll together with results
        item {
            Button(
                onClick = {
                    val intent = Intent(context, MainActivity::class.java)
                    context.startActivity(intent)
                },
                modifier = Modifier.padding(top = 20.dp)
            ) { Text("Back") }

            Text(
                text = "Search By Ingredient",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 10.dp, top = 20.dp)
            )

            TextField(
                value = userInput,
                onValueChange = { newValue -> userInput = newValue },
                label = { Text("Enter an ingredient, e.g. chicken") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp, top = 20.dp),
                singleLine = true,
                shape = RoundedCornerShape(10.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surface
                )
            )

            // Task 3: Retrieve Meals button - fetches from the web API
            Button(
                onClick = {
                    if (userInput.isBlank()) {
                        Toast.makeText(context, "Please enter an ingredient", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    // Reset results and start loading
                    meals = emptyList()
                    isLoading = true
                    statusMessage = ""

                    // Network call must be on Dispatchers.IO (background thread)
                    scope.launch {
                        val result = withContext(Dispatchers.IO) {
                            MealApiService.getMealsByIngredient(userInput)
                        }
                        // Back on main thread to update UI state
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
                Text("Retrieve Meals")
            }

            // Task 4: Save Meals to Database button - saves currently displayed meals
            Button(
                onClick = {
                    if (meals.isEmpty()) {
                        Toast.makeText(context, "Retrieve meals first before saving", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    // Database writes must be on a background thread
                    scope.launch {
                        withContext(Dispatchers.IO) {
                            val db = MealDatabase.getDatabase(context)
                            db.mealDao().insertMeals(meals)
                        }
                        // Show confirmation on UI thread
                        Toast.makeText(context, "${meals.size} meals saved to database!", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Save Meals to Database")
            }

            // Show a spinner while loading
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.padding(16.dp))
                Text("Loading meals, please wait...")
            }

            // Show a message if no results found
            if (statusMessage.isNotEmpty()) {
                Text(
                    text = statusMessage,
                    modifier = Modifier.padding(top = 8.dp),
                    color = MaterialTheme.colorScheme.error
                )
            }

            // Show how many meals were found
            if (meals.isNotEmpty()) {
                Text(
                    text = "${meals.size} meal(s) found:",
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 12.dp, bottom = 4.dp)
                )
            }
        }

        // Task 3: Display all the meal details as cards (one per meal)
        // LazyColumn only renders what's on screen - much more efficient than Column
        items(meals) { meal ->
            MealCard(meal = meal)
        }
    }
}

// Displays all details of a single meal in a Card
@Composable
fun MealCard(meal: MealEntity) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        shape = RoundedCornerShape(10.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {

            // Meal name as header
            Text(
                text = "Meal: ${meal.strMeal}",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                modifier = Modifier.padding(bottom = 4.dp)
            )

            // Display all the fields exactly as the spec requires
            InfoLine("DrinkAlternate", meal.strDrinkAlternate?.let { it } ?: "null")
            InfoLine("Category", meal.strCategory ?: "null")
            InfoLine("Area", meal.strArea ?: "null")
            InfoLine("Instructions", meal.strInstructions ?: "null")
            InfoLine("Tags", meal.strTags ?: "null")
            InfoLine("Youtube", meal.strYoutube ?: "null")

            // Print each non-null ingredient and its measure
            val ingredients = listOf(
                meal.strIngredient1, meal.strIngredient2, meal.strIngredient3,
                meal.strIngredient4, meal.strIngredient5, meal.strIngredient6,
                meal.strIngredient7, meal.strIngredient8, meal.strIngredient9,
                meal.strIngredient10, meal.strIngredient11, meal.strIngredient12,
                meal.strIngredient13, meal.strIngredient14, meal.strIngredient15,
                meal.strIngredient16, meal.strIngredient17, meal.strIngredient18,
                meal.strIngredient19, meal.strIngredient20
            )
            val measures = listOf(
                meal.strMeasure1, meal.strMeasure2, meal.strMeasure3,
                meal.strMeasure4, meal.strMeasure5, meal.strMeasure6,
                meal.strMeasure7, meal.strMeasure8, meal.strMeasure9,
                meal.strMeasure10, meal.strMeasure11, meal.strMeasure12,
                meal.strMeasure13, meal.strMeasure14, meal.strMeasure15,
                meal.strMeasure16, meal.strMeasure17, meal.strMeasure18,
                meal.strMeasure19, meal.strMeasure20
            )
            ingredients.forEachIndexed { index, ingredient ->
                if (!ingredient.isNullOrBlank()) {
                    val num = index + 1
                    InfoLine("Ingredient$num", ingredient)
                    InfoLine("Measure$num", measures[index] ?: "")
                }
            }
        }
    }
}

// Small helper to print "Label: value" on one line
@Composable
fun InfoLine(label: String, value: String) {
    Text(
        text = "$label: $value",
        fontSize = 12.sp,
        modifier = Modifier.padding(vertical = 1.dp)
    )
}

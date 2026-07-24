package com.example.mealappcw.ui.theme

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.mealappcw.MainActivity
import com.example.mealappcw.data.MealDatabase
import com.example.mealappcw.data.MealEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

@Composable
fun DatabaseSearchScreen() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // rememberSaveable keeps values across screen rotation (Task 8)
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
                text = "Search for Meals (Database)",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 10.dp, top = 20.dp)
            )

            TextField(
                value = userInput,
                onValueChange = { newValue -> userInput = newValue },
                label = { Text("Search by name or ingredient, e.g. Pasta") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp, top = 20.dp),
                singleLine = true,
                shape = RoundedCornerShape(10.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surface
                )
            )

            // Task 5: Search button - queries the local Room database
            Button(
                onClick = {
                    if (userInput.isBlank()) {
                        Toast.makeText(context, "Please enter a search term", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    meals = emptyList()
                    isLoading = true
                    statusMessage = ""

                    // Database queries must run on a background thread
                    scope.launch {
                        val result = withContext(Dispatchers.IO) {
                            val db = MealDatabase.getDatabase(context)
                            // searchMeals does a case-insensitive partial match (LIKE '%query%')
                            // on both the meal name and all 20 ingredient columns
                            db.mealDao().searchMeals(userInput)
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

        // Task 6: Display each meal WITH its thumbnail image
        items(meals) { meal ->
            MealCardWithImage(meal = meal)
        }
    }
}

// Task 6: Meal card that also shows the thumbnail image from the internet
@Composable
fun MealCardWithImage(meal: MealEntity) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        shape = RoundedCornerShape(10.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {

            // Task 6: Show the meal thumbnail image if the URL exists
            if (!meal.strMealThumb.isNullOrBlank()) {
                ThumbnailImage(imageUrl = meal.strMealThumb)
            }

            Text(
                text = "Meal: ${meal.strMeal}",
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
            )
            InfoLine("DrinkAlternate", meal.strDrinkAlternate ?: "null")
            InfoLine("Category", meal.strCategory ?: "null")
            InfoLine("Area", meal.strArea ?: "null")
            InfoLine("Instructions", meal.strInstructions ?: "null")
            InfoLine("Tags", meal.strTags ?: "null")
            InfoLine("Youtube", meal.strYoutube ?: "null")

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

// Downloads and displays a thumbnail image from a URL
// Uses LaunchedEffect to load the image when the composable first appears
// Uses Dispatchers.IO so the download happens on a background thread
@Composable
fun ThumbnailImage(imageUrl: String) {
    // remember (not rememberSaveable) - bitmap can't be saved across rotation
    // but that's fine, it will just reload from the URL
    var bitmap by remember { mutableStateOf<Bitmap?>(null) }

    // LaunchedEffect runs when the composable enters the screen
    // It re-runs if imageUrl changes
    LaunchedEffect(imageUrl) {
        bitmap = withContext(Dispatchers.IO) {
            downloadBitmap(imageUrl)
        }
    }

    if (bitmap != null) {
        Image(
            bitmap = bitmap!!.asImageBitmap(),
            contentDescription = "Meal thumbnail for display",
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
                .clip(RoundedCornerShape(8.dp)),
            contentScale = ContentScale.Crop
        )
    } else {
        // Gray placeholder while image is loading
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
                .clip(RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "Loading image...", color = Color.Gray)
        }
    }
}

// Downloads a bitmap from a URL using standard HttpURLConnection
// Must be called from a background thread (Dispatchers.IO)
private fun downloadBitmap(urlString: String): Bitmap? {
    return try {
        val url = URL(urlString)
        val connection = url.openConnection() as HttpURLConnection
        connection.connectTimeout = 8000
        connection.readTimeout = 8000
        connection.doInput = true
        connection.connect()
        val input: InputStream = connection.inputStream
        BitmapFactory.decodeStream(input)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

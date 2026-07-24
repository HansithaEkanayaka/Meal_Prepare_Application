package com.example.mealappcw.network

import com.example.mealappcw.data.MealEntity
import org.json.JSONObject
import org.json.JSONArray
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

// All network calls to TheMealDB API live here.
// We use only standard Java libraries (HttpURLConnection, BufferedReader) - no third-party libs.
// Network calls must NOT run on the main thread - always call from a coroutine with Dispatchers.IO
object MealApiService {

    private const val BASE_URL = "https://www.themealdb.com/api/json/v1/1"

    // Task 3: Get all meals that contain a given ingredient
    // We first call /filter.php?i=chicken to get a list of meal IDs,
    // then call /lookup.php?i=ID for each meal to get its full details.
    fun getMealsByIngredient(ingredient: String): List<MealEntity> {
        val encoded = ingredient.trim().replace(" ", "%20")
        val filterResponse = fetchUrl("$BASE_URL/filter.php?i=$encoded") ?: return emptyList()
        val filterJson = JSONObject(filterResponse)
        val summaryArray = filterJson.optJSONArray("meals") ?: return emptyList()

        val meals = mutableListOf<MealEntity>()
        for (i in 0 until summaryArray.length()) {
            val id = summaryArray.getJSONObject(i).optString("idMeal", "")
            if (id.isNotEmpty()) {
                val fullMeal = getMealById(id)
                if (fullMeal != null) meals.add(fullMeal)
            }
        }
        return meals
    }

    // Task 7: Search meals by name substring from the web
    // Uses /search.php?s=chicken
    fun searchMealsByName(name: String): List<MealEntity> {
        val encoded = name.trim().replace(" ", "%20")
        val response = fetchUrl("$BASE_URL/search.php?s=$encoded") ?: return emptyList()
        return parseMealsFromJson(response)
    }

    // Get full meal details by meal ID
    fun getMealById(id: String): MealEntity? {
        val response = fetchUrl("$BASE_URL/lookup.php?i=$id") ?: return null
        return parseMealsFromJson(response).firstOrNull()
    }

    // Simple HTTP GET - returns the response body as a String, or null if error
    private fun fetchUrl(urlString: String): String? {
        var connection: HttpURLConnection? = null
        return try {
            val url = URL(urlString)
            connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.connectTimeout = 10000  // 10 seconds timeout
            connection.readTimeout = 10000

            if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                val reader = BufferedReader(InputStreamReader(connection.inputStream))
                val sb = StringBuilder()
                var line: String?
                while (reader.readLine().also { line = it } != null) {
                    sb.append(line)
                }
                reader.close()
                sb.toString()
            } else null
        } catch (e: Exception) {
            e.printStackTrace()
            null
        } finally {
            connection?.disconnect()
        }
    }

    // Parse a JSON response that has a "meals" array and return a list of MealEntity
    fun parseMealsFromJson(jsonString: String): List<MealEntity> {
        val meals = mutableListOf<MealEntity>()
        return try {
            val root = JSONObject(jsonString)
            val array: JSONArray = root.optJSONArray("meals") ?: return emptyList()
            for (i in 0 until array.length()) {
                meals.add(parseSingleMeal(array.getJSONObject(i)))
            }
            meals
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    // Convert a single JSON object from the API into a MealEntity
    private fun parseSingleMeal(json: JSONObject): MealEntity {
        // Helper: get a string value or null if it's empty/null/"null"
        fun s(key: String): String? {
            val v = json.optString(key, "")
            return if (v.isNullOrBlank() || v == "null") null else v
        }
        return MealEntity(
            idMeal = s("idMeal") ?: System.currentTimeMillis().toString(),
            strMeal = s("strMeal") ?: "Unknown",
            strDrinkAlternate = s("strDrinkAlternate"),
            strCategory = s("strCategory"),
            strArea = s("strArea"),
            strInstructions = s("strInstructions"),
            strMealThumb = s("strMealThumb"),
            strTags = s("strTags"),
            strYoutube = s("strYoutube"),
            strIngredient1 = s("strIngredient1"), strIngredient2 = s("strIngredient2"),
            strIngredient3 = s("strIngredient3"), strIngredient4 = s("strIngredient4"),
            strIngredient5 = s("strIngredient5"), strIngredient6 = s("strIngredient6"),
            strIngredient7 = s("strIngredient7"), strIngredient8 = s("strIngredient8"),
            strIngredient9 = s("strIngredient9"), strIngredient10 = s("strIngredient10"),
            strIngredient11 = s("strIngredient11"), strIngredient12 = s("strIngredient12"),
            strIngredient13 = s("strIngredient13"), strIngredient14 = s("strIngredient14"),
            strIngredient15 = s("strIngredient15"), strIngredient16 = s("strIngredient16"),
            strIngredient17 = s("strIngredient17"), strIngredient18 = s("strIngredient18"),
            strIngredient19 = s("strIngredient19"), strIngredient20 = s("strIngredient20"),
            strMeasure1 = s("strMeasure1"), strMeasure2 = s("strMeasure2"),
            strMeasure3 = s("strMeasure3"), strMeasure4 = s("strMeasure4"),
            strMeasure5 = s("strMeasure5"), strMeasure6 = s("strMeasure6"),
            strMeasure7 = s("strMeasure7"), strMeasure8 = s("strMeasure8"),
            strMeasure9 = s("strMeasure9"), strMeasure10 = s("strMeasure10"),
            strMeasure11 = s("strMeasure11"), strMeasure12 = s("strMeasure12"),
            strMeasure13 = s("strMeasure13"), strMeasure14 = s("strMeasure14"),
            strMeasure15 = s("strMeasure15"), strMeasure16 = s("strMeasure16"),
            strMeasure17 = s("strMeasure17"), strMeasure18 = s("strMeasure18"),
            strMeasure19 = s("strMeasure19"), strMeasure20 = s("strMeasure20")
        )
    }
}

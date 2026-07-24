package com.example.mealappcw.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

// DAO = Data Access Object. This is where we define all our SQL queries.
@Dao
interface MealDao {

    // Insert one meal. If the same idMeal already exists, replace it.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMeal(meal: MealEntity)

    // Insert a list of meals at once
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMeals(meals: List<MealEntity>)

    // Get every meal stored in the database
    @Query("SELECT * FROM meals")
    suspend fun getAllMeals(): List<MealEntity>

    // Case-insensitive partial search in meal name AND all 20 ingredient columns
    // LIKE '%' || :query || '%' means: contains the query anywhere in the text
    // SQLite's LIKE is case-insensitive for ASCII letters by default
    @Query("""
        SELECT * FROM meals WHERE
        strMeal LIKE '%' || :query || '%' OR
        strIngredient1 LIKE '%' || :query || '%' OR
        strIngredient2 LIKE '%' || :query || '%' OR
        strIngredient3 LIKE '%' || :query || '%' OR
        strIngredient4 LIKE '%' || :query || '%' OR
        strIngredient5 LIKE '%' || :query || '%' OR
        strIngredient6 LIKE '%' || :query || '%' OR
        strIngredient7 LIKE '%' || :query || '%' OR
        strIngredient8 LIKE '%' || :query || '%' OR
        strIngredient9 LIKE '%' || :query || '%' OR
        strIngredient10 LIKE '%' || :query || '%' OR
        strIngredient11 LIKE '%' || :query || '%' OR
        strIngredient12 LIKE '%' || :query || '%' OR
        strIngredient13 LIKE '%' || :query || '%' OR
        strIngredient14 LIKE '%' || :query || '%' OR
        strIngredient15 LIKE '%' || :query || '%' OR
        strIngredient16 LIKE '%' || :query || '%' OR
        strIngredient17 LIKE '%' || :query || '%' OR
        strIngredient18 LIKE '%' || :query || '%' OR
        strIngredient19 LIKE '%' || :query || '%' OR
        strIngredient20 LIKE '%' || :query || '%'
    """)
    suspend fun searchMeals(query: String): List<MealEntity>
}

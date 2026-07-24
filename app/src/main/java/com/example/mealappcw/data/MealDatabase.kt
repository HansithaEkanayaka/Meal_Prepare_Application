package com.example.mealappcw.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

// Room database class. We list all tables (entities) here.
// exportSchema = false means we don't need a schema export folder
@Database(entities = [MealEntity::class], version = 1, exportSchema = false)
abstract class MealDatabase : RoomDatabase() {

    // Room will auto-generate the code for this DAO
    abstract fun mealDao(): MealDao

    companion object {
        // Singleton pattern - only one database instance exists at a time
        @Volatile
        private var INSTANCE: MealDatabase? = null

        fun getDatabase(context: Context): MealDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MealDatabase::class.java,
                    "meal_database"  // name of the .db file on disk
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}

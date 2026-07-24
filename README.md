# MealAppCw

An Android meal-prep app built with Jetpack Compose that lets users fetch meals from [TheMealDB](https://www.themealdb.com/api.php), save them to a local database, and search saved meals offline.

## Features

- **Add Meals to DB** — seeds the local Room database with a set of hardcoded starter meals.
- **Search for Meals by Ingredient** — queries TheMealDB's web API for meals containing a given ingredient and saves the results locally.
- **Search for Meals (DB)** — case-insensitive search across meal names and all ingredient fields, run entirely against the local Room database.
- **Search Meals (Web)** — searches TheMealDB directly by meal name.

## Tech Stack

- **Kotlin** + **Jetpack Compose** (Material 3) for the UI
- **Room** for local persistence (SQLite)
- **HttpURLConnection** + **org.json** for networking and JSON parsing — no Retrofit, Glide, or other third-party networking/image libraries
- **Kotlin Coroutines** for background work (network and database calls run off the main thread)

## Project Structure

```
app/src/main/java/com/example/mealappcw/
├── MainActivity.kt              # Entry point, hosts HomeScreen
├── IngredientSearch.kt          # Activity: search by ingredient
├── DataBaseSearch.kt            # Activity: search local DB
├── SearchFromWeb.kt             # Activity: search by name from web
├── data/
│   ├── MealEntity.kt            # Room entity
│   ├── MealDao.kt               # Room DAO / queries
│   ├── MealDatabase.kt          # Room database
│   └── HardcodedMeals.kt        # Seed data
├── network/
│   └── MealApiService.kt        # TheMealDB API calls (HttpURLConnection)
└── ui/theme/
    ├── HomeScreen.kt
    ├── IngredientSearchScreen.kt
    ├── DatabaseSearchScreen.kt
    └── searchFromWebScreen.kt
```

## Requirements

- Android Studio (recent stable release)
- minSdk 24, targetSdk 35, compileSdk 36
- Internet connection (for the web-search features)

## Getting Started

1. Clone the repo.
2. Open the project in Android Studio.
3. Let Gradle sync and download dependencies.
4. Run on an emulator or physical device (API 24+).

## Notes

- Built as coursework for the Mobile Application Development module under a strict constraint set: Jetpack Compose only, Room for persistence, and `HttpURLConnection`/`org.json` for networking — no third-party HTTP or image-loading libraries.

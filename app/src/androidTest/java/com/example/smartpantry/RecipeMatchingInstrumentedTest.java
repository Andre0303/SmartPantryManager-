
package com.example.smartpantry;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class RecipeMatchingInstrumentedTest {

    @Test
    public void recipeIsHiddenWhenAnIngredientIsInsufficient() {
        Context context = InstrumentationRegistry
                .getInstrumentation()
                .getTargetContext();

        SQLiteDatabase testDb = SQLiteDatabase.create(null);

        try {
            testDb.execSQL(
                    "CREATE TABLE pantry_items (" +
                            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                            "name TEXT NOT NULL, " +
                            "quantity REAL NOT NULL, " +
                            "unit TEXT NOT NULL, " +
                            "expiry_date TEXT)"
            );

            testDb.execSQL(
                    "CREATE TABLE recipes (" +
                            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                            "name TEXT NOT NULL, " +
                            "instructions TEXT NOT NULL)"
            );

            testDb.execSQL(
                    "CREATE TABLE recipe_ingredients (" +
                            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                            "recipe_id INTEGER NOT NULL, " +
                            "ingredient_name TEXT NOT NULL, " +
                            "quantity REAL NOT NULL DEFAULT 1, " +
                            "unit TEXT NOT NULL DEFAULT 'pieces')"
            );

            testDb.execSQL(
                    "INSERT INTO recipes (name, instructions) " +
                            "VALUES ('Test Omelette', 'Test instructions')"
            );

            testDb.execSQL(
                    "INSERT INTO recipe_ingredients " +
                            "(recipe_id, ingredient_name, quantity, unit) " +
                            "VALUES (1, 'Egg', 2, 'pieces'), " +
                            "(1, 'Tomato', 1, 'pieces')"
            );

            testDb.execSQL(
                    "INSERT INTO pantry_items (name, quantity, unit) " +
                            "VALUES ('Egg', 1, 'pieces'), " +
                            "('Tomato', 1, 'pieces')"
            );

            Cursor cursor = testDb.rawQuery(
                    "SELECT r.name FROM recipes r " +
                            "WHERE EXISTS (" +
                            "SELECT 1 FROM recipe_ingredients ri " +
                            "WHERE ri.recipe_id = r.id) " +
                            "AND NOT EXISTS (" +
                            "SELECT 1 FROM recipe_ingredients ri " +
                            "WHERE ri.recipe_id = r.id " +
                            "AND NOT EXISTS (" +
                            "SELECT 1 FROM pantry_items p " +
                            "WHERE LOWER(TRIM(p.name)) = " +
                            "LOWER(TRIM(ri.ingredient_name)) " +
                            "AND LOWER(TRIM(p.unit)) = " +
                            "LOWER(TRIM(ri.unit)) " +
                            "AND p.quantity >= ri.quantity))",
                    null
            );

            try {
                assertFalse(
                        "Recipe must be hidden when there are too few eggs",
                        cursor.moveToFirst()
                );
            } finally {
                cursor.close();
            }

            // Increase the egg quantity to the required amount.
            testDb.execSQL(
                    "UPDATE pantry_items SET quantity = 2 " +
                            "WHERE name = 'Egg'"
            );

            cursor = testDb.rawQuery(
                    "SELECT r.name FROM recipes r " +
                            "WHERE EXISTS (" +
                            "SELECT 1 FROM recipe_ingredients ri " +
                            "WHERE ri.recipe_id = r.id) " +
                            "AND NOT EXISTS (" +
                            "SELECT 1 FROM recipe_ingredients ri " +
                            "WHERE ri.recipe_id = r.id " +
                            "AND NOT EXISTS (" +
                            "SELECT 1 FROM pantry_items p " +
                            "WHERE LOWER(TRIM(p.name)) = " +
                            "LOWER(TRIM(ri.ingredient_name)) " +
                            "AND LOWER(TRIM(p.unit)) = " +
                            "LOWER(TRIM(ri.unit)) " +
                            "AND p.quantity >= ri.quantity))",
                    null
            );

            try {
                assertTrue(
                        "Recipe should appear when all ingredients are available",
                        cursor.moveToFirst()
                );
            } finally {
                cursor.close();
            }

        } finally {
            testDb.close();
        }
    }


    @Test
    public void recipeIsHiddenWhenAnIngredientIsMissing() {
        SQLiteDatabase testDb = SQLiteDatabase.create(null);

        try {
            testDb.execSQL(
                    "CREATE TABLE pantry_items (" +
                            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                            "name TEXT NOT NULL, " +
                            "quantity REAL NOT NULL, " +
                            "unit TEXT NOT NULL, " +
                            "expiry_date TEXT)"
            );

            testDb.execSQL(
                    "CREATE TABLE recipes (" +
                            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                            "name TEXT NOT NULL, " +
                            "instructions TEXT NOT NULL)"
            );

            testDb.execSQL(
                    "CREATE TABLE recipe_ingredients (" +
                            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                            "recipe_id INTEGER NOT NULL, " +
                            "ingredient_name TEXT NOT NULL, " +
                            "quantity REAL NOT NULL DEFAULT 1, " +
                            "unit TEXT NOT NULL DEFAULT 'pieces')"
            );

            testDb.execSQL(
                    "INSERT INTO recipes (name, instructions) " +
                            "VALUES ('Test Omelette', 'Test instructions')"
            );

            testDb.execSQL(
                    "INSERT INTO recipe_ingredients " +
                            "(recipe_id, ingredient_name, quantity, unit) " +
                            "VALUES (1, 'Egg', 2, 'pieces'), " +
                            "(1, 'Tomato', 1, 'pieces')"
            );

            // Only eggs are available. The required tomato is missing.
            testDb.execSQL(
                    "INSERT INTO pantry_items (name, quantity, unit) " +
                            "VALUES ('Egg', 2, 'pieces')"
            );

            Cursor cursor = testDb.rawQuery(
                    "SELECT r.name FROM recipes r " +
                            "WHERE EXISTS (" +
                            "SELECT 1 FROM recipe_ingredients ri " +
                            "WHERE ri.recipe_id = r.id) " +
                            "AND NOT EXISTS (" +
                            "SELECT 1 FROM recipe_ingredients ri " +
                            "WHERE ri.recipe_id = r.id " +
                            "AND NOT EXISTS (" +
                            "SELECT 1 FROM pantry_items p " +
                            "WHERE LOWER(TRIM(p.name)) = " +
                            "LOWER(TRIM(ri.ingredient_name)) " +
                            "AND LOWER(TRIM(p.unit)) = " +
                            "LOWER(TRIM(ri.unit)) " +
                            "AND p.quantity >= ri.quantity))",
                    null
            );

            try {
                assertFalse(
                        "Recipe must be hidden when a required tomato is missing",
                        cursor.moveToFirst()
                );
            } finally {
                cursor.close();
            }
        } finally {
            testDb.close();
        }
    }


    @Test
    public void recipeIsHiddenWhenIngredientUnitDoesNotMatch() {
        SQLiteDatabase testDb = SQLiteDatabase.create(null);

        try {
            testDb.execSQL(
                    "CREATE TABLE pantry_items (" +
                            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                            "name TEXT NOT NULL, " +
                            "quantity REAL NOT NULL, " +
                            "unit TEXT NOT NULL, " +
                            "expiry_date TEXT)"
            );

            testDb.execSQL(
                    "CREATE TABLE recipes (" +
                            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                            "name TEXT NOT NULL, " +
                            "instructions TEXT NOT NULL)"
            );

            testDb.execSQL(
                    "CREATE TABLE recipe_ingredients (" +
                            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                            "recipe_id INTEGER NOT NULL, " +
                            "ingredient_name TEXT NOT NULL, " +
                            "quantity REAL NOT NULL DEFAULT 1, " +
                            "unit TEXT NOT NULL DEFAULT 'pieces')"
            );

            testDb.execSQL(
                    "INSERT INTO recipes (name, instructions) " +
                            "VALUES ('Test Omelette', 'Test instructions')"
            );

            testDb.execSQL(
                    "INSERT INTO recipe_ingredients " +
                            "(recipe_id, ingredient_name, quantity, unit) " +
                            "VALUES (1, 'Egg', 2, 'pieces')"
            );

            // The ingredient name matches, but the unit does not.
            testDb.execSQL(
                    "INSERT INTO pantry_items (name, quantity, unit) " +
                            "VALUES ('Egg', 2, 'kg')"
            );

            Cursor cursor = testDb.rawQuery(
                    "SELECT r.name FROM recipes r " +
                            "WHERE EXISTS (" +
                            "SELECT 1 FROM recipe_ingredients ri " +
                            "WHERE ri.recipe_id = r.id) " +
                            "AND NOT EXISTS (" +
                            "SELECT 1 FROM recipe_ingredients ri " +
                            "WHERE ri.recipe_id = r.id " +
                            "AND NOT EXISTS (" +
                            "SELECT 1 FROM pantry_items p " +
                            "WHERE LOWER(TRIM(p.name)) = " +
                            "LOWER(TRIM(ri.ingredient_name)) " +
                            "AND LOWER(TRIM(p.unit)) = " +
                            "LOWER(TRIM(ri.unit)) " +
                            "AND p.quantity >= ri.quantity))",
                    null
            );

            try {
                assertFalse(
                        "Recipe must be hidden when the ingredient unit does not match",
                        cursor.moveToFirst()
                );
            } finally {
                cursor.close();
            }
        } finally {
            testDb.close();
        }
    }


    @Test
    public void recipeMatchesIngredientNamesWithDifferentCaseAndSpaces() {
        SQLiteDatabase testDb = SQLiteDatabase.create(null);

        try {
            testDb.execSQL(
                    "CREATE TABLE pantry_items (" +
                            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                            "name TEXT NOT NULL, " +
                            "quantity REAL NOT NULL, " +
                            "unit TEXT NOT NULL, " +
                            "expiry_date TEXT)"
            );

            testDb.execSQL(
                    "CREATE TABLE recipes (" +
                            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                            "name TEXT NOT NULL, " +
                            "instructions TEXT NOT NULL)"
            );

            testDb.execSQL(
                    "CREATE TABLE recipe_ingredients (" +
                            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                            "recipe_id INTEGER NOT NULL, " +
                            "ingredient_name TEXT NOT NULL, " +
                            "quantity REAL NOT NULL DEFAULT 1, " +
                            "unit TEXT NOT NULL DEFAULT 'pieces')"
            );

            testDb.execSQL(
                    "INSERT INTO recipes (name, instructions) " +
                            "VALUES ('Test Omelette', 'Test instructions')"
            );

            testDb.execSQL(
                    "INSERT INTO recipe_ingredients " +
                            "(recipe_id, ingredient_name, quantity, unit) " +
                            "VALUES (1, ' Egg ', 2, 'pieces')"
            );

            // Pantry names and units have different capitalization and spaces.
            testDb.execSQL(
                    "INSERT INTO pantry_items (name, quantity, unit) " +
                            "VALUES ('egg', 2, ' PIECES ')"
            );

            Cursor cursor = testDb.rawQuery(
                    "SELECT r.name FROM recipes r " +
                            "WHERE EXISTS (" +
                            "SELECT 1 FROM recipe_ingredients ri " +
                            "WHERE ri.recipe_id = r.id) " +
                            "AND NOT EXISTS (" +
                            "SELECT 1 FROM recipe_ingredients ri " +
                            "WHERE ri.recipe_id = r.id " +
                            "AND NOT EXISTS (" +
                            "SELECT 1 FROM pantry_items p " +
                            "WHERE LOWER(TRIM(p.name)) = " +
                            "LOWER(TRIM(ri.ingredient_name)) " +
                            "AND LOWER(TRIM(p.unit)) = " +
                            "LOWER(TRIM(ri.unit)) " +
                            "AND p.quantity >= ri.quantity))",
                    null
            );

            try {
                assertTrue(
                        "Recipe should match despite differences in case and spaces",
                        cursor.moveToFirst()
                );
            } finally {
                cursor.close();
            }
        } finally {
            testDb.close();
        }
    }



}

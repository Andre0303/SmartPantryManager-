
package com.example.smartpantry;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "smart_pantry.db";
    private static final int DATABASE_VERSION = 3;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Store pantry ingredients
        db.execSQL(
                "CREATE TABLE pantry_items (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "name TEXT NOT NULL, " +
                        "quantity REAL NOT NULL, " +
                        "unit TEXT NOT NULL, " +
                        "expiry_date TEXT)"
        );

        createRecipeTables(db);
    }

    private void createRecipeTables(SQLiteDatabase db) {
        // Store recipes
        db.execSQL(
                "CREATE TABLE recipes (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "name TEXT NOT NULL, " +
                        "instructions TEXT NOT NULL)"
        );

        // Store ingredients required by each recipe
        db.execSQL(
                "CREATE TABLE recipe_ingredients (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "recipe_id INTEGER NOT NULL, " +
                        "ingredient_name TEXT NOT NULL, " +
                        "quantity REAL NOT NULL DEFAULT 1, " +
                        "unit TEXT NOT NULL DEFAULT 'pieces', " +
                        "FOREIGN KEY(recipe_id) REFERENCES recipes(id) " +
                        "ON DELETE CASCADE)"
        );
    }

    @Override
    public void onUpgrade(
            SQLiteDatabase db,
            int oldVersion,
            int newVersion) {

        if (oldVersion < 2) {
            createRecipeTables(db);
        }
    }

    // Save an ingredient to the pantry
    public long addIngredient(
            String name,
            double quantity,
            String unit,
            String expiryDate) {

        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("name", name.trim());
        values.put("quantity", quantity);
        values.put("unit", unit);
        values.put("expiry_date", expiryDate);

        return db.insert("pantry_items", null, values);
    }

    // Retrieve all pantry ingredients
    public Cursor getAllIngredients() {
        SQLiteDatabase db = getReadableDatabase();

        return db.rawQuery(
                "SELECT id, name, quantity, unit, expiry_date " +
                        "FROM pantry_items ORDER BY name ASC",
                null
        );
    }

    // Add a recipe
    public long addRecipe(String name, String instructions) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("name", name.trim());
        values.put("instructions", instructions.trim());

        return db.insert("recipes", null, values);
    }


    // Update the instructions of an existing recipe
    public int updateRecipeInstructions(
            String recipeName,
            String newInstructions) {

        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("instructions", newInstructions.trim());

        return db.update(
                "recipes",
                values,
                "name = ?",
                new String[]{recipeName.trim()}
        );
    }


    // Add an ingredient required by a recipe
    public long addRecipeIngredient(
            long recipeId,
            String ingredientName,
            double quantity,
            String unit) {

        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("recipe_id", recipeId);
        values.put("ingredient_name", ingredientName.trim());
        values.put("quantity", quantity);
        values.put("unit", unit);

        return db.insert("recipe_ingredients", null, values);
    }

    // Return only recipes whose required ingredients are available
    public Cursor getAvailableRecipes() {
        SQLiteDatabase db = getReadableDatabase();

        String sql =
                "SELECT r.id, r.name, r.instructions " +
                        "FROM recipes r " +
                        "WHERE EXISTS (" +
                        "  SELECT 1 FROM recipe_ingredients ri " +
                        "  WHERE ri.recipe_id = r.id" +
                        ") " +
                        "AND NOT EXISTS (" +
                        "  SELECT 1 FROM recipe_ingredients ri " +
                        "  WHERE ri.recipe_id = r.id " +
                        "  AND NOT EXISTS (" +
                        "    SELECT 1 FROM pantry_items p " +
                        "    WHERE LOWER(TRIM(p.name)) = " +
                        "          LOWER(TRIM(ri.ingredient_name)) " +
                        "    AND LOWER(TRIM(p.unit)) = " +
                        "          LOWER(TRIM(ri.unit)) " +
                        "    AND p.quantity >= ri.quantity" +
                        "  )" +
                        ") " +
                        "ORDER BY r.name ASC";

        return db.rawQuery(sql, null);
    }

    // Check whether any recipes exist
    public boolean hasRecipes() {
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT COUNT(*) FROM recipes",
                null
        );

        boolean hasRecipes = false;

        try {
            if (cursor.moveToFirst()) {
                hasRecipes = cursor.getInt(0) > 0;
            }
        } finally {
            cursor.close();
        }

        return hasRecipes;
    }

    // Delete a pantry ingredient
    public int deleteIngredient(int id) {
        SQLiteDatabase db = getWritableDatabase();

        return db.delete(
                "pantry_items",
                "id = ?",
                new String[]{String.valueOf(id)}
        );
    }

    // Update a pantry ingredient
    public int updateIngredient(
            int id,
            String name,
            double quantity,
            String unit,
            String expiryDate) {

        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("name", name.trim());
        values.put("quantity", quantity);
        values.put("unit", unit);
        values.put("expiry_date", expiryDate);

        return db.update(
                "pantry_items",
                values,
                "id = ?",
                new String[]{String.valueOf(id)}
        );
    }

    // Retrieve ingredients for a selected recipe
    public Cursor getIngredientsForRecipe(int recipeId) {
        SQLiteDatabase db = getReadableDatabase();

        return db.rawQuery(
                "SELECT ingredient_name, quantity, unit " +
                        "FROM recipe_ingredients " +
                        "WHERE recipe_id = ? " +
                        "ORDER BY ingredient_name ASC",
                new String[]{String.valueOf(recipeId)}
        );
    }


    public String verifyRecipeDatabase() {
        SQLiteDatabase db = getReadableDatabase();

        Cursor recipeCursor = null;
        Cursor ingredientCursor = null;

        try {
            recipeCursor = db.rawQuery(
                    "SELECT COUNT(*) FROM recipes",
                    null
            );

            int recipeCount = 0;

            if (recipeCursor.moveToFirst()) {
                recipeCount = recipeCursor.getInt(0);
            }

            ingredientCursor = db.rawQuery(
                    "SELECT COUNT(*) FROM recipe_ingredients",
                    null
            );

            int ingredientCount = 0;

            if (ingredientCursor.moveToFirst()) {
                ingredientCount = ingredientCursor.getInt(0);
            }

            return "Recipes saved: " + recipeCount +
                    "\nRecipe ingredients saved: " + ingredientCount;

        } finally {
            if (recipeCursor != null) {
                recipeCursor.close();
            }

            if (ingredientCursor != null) {
                ingredientCursor.close();
            }
        }
    }

}

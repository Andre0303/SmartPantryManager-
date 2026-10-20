
package com.example.smartpantry;

public class RecipeSeeder {

    public static void seedRecipes(DatabaseHelper db) {

        // Do not insert recipes again if they already exist.
        android.database.Cursor cursor =
                db.getReadableDatabase().rawQuery(
                        "SELECT COUNT(*) FROM recipes", null);

        int count = 0;
        try {
            if (cursor.moveToFirst()) {
                count = cursor.getInt(0);
            }
        } finally {
            cursor.close();
        }

        if (count > 0) {
            return;
        }

        addRecipe(db, "Egg Fried Rice",
                "Cook the rice. Scramble the eggs in a pan, add the cooked rice, and stir-fry until heated through.",
                new String[]{"rice", "eggs"},
                new double[]{1, 2},
                new String[]{"cups", "pieces"});

        addRecipe(db, "Tomato Omelette",
                "Beat the eggs, chop the tomato, mix together, and cook in a pan.",
                new String[]{"eggs", "tomato"},
                new double[]{2, 1},
                new String[]{"pieces", "pieces"});

        addRecipe(db, "Banana Pancakes",
                "Mash the banana, mix with eggs and flour, and fry small pancakes.",
                new String[]{"banana", "eggs", "flour"},
                new double[]{1, 1, 0.5},
                new String[]{"pieces", "pieces", "cups"});

        addRecipe(db, "Garlic Pasta",
                "Boil pasta and sauté garlic in oil. Mix together.",
                new String[]{"pasta", "garlic", "oil"},
                new double[]{1, 2, 1},
                new String[]{"cups", "pieces", "tablespoons"});

        addRecipe(db, "Tomato Pasta",
                "Cook the pasta. Chop the tomatoes, cook them in a pan until soft, and mix with the pasta.",
                new String[]{"pasta", "tomato"},
                new double[]{1, 2},
                new String[]{"cups", "pieces"});

        addRecipe(db, "Cheese Toast",
                "Place cheese between bread slices and toast until golden.",
                new String[]{"bread", "cheese"},
                new double[]{2, 1},
                new String[]{"slices", "slices"});

        addRecipe(db, "Peanut Butter Sandwich",
                "Spread peanut butter on bread and make a sandwich.",
                new String[]{"bread", "peanut butter"},
                new double[]{2, 1},
                new String[]{"slices", "tablespoons"});

        addRecipe(db, "Potato Mash",
                "Boil the potatoes until soft. Drain, mash thoroughly, and add the measured salt.",
                new String[]{"potato", "salt"},
                new double[]{2, 1},
                new String[]{"pieces", "pinches"});

        addRecipe(db, "Carrot Salad",
                "Wash and grate the carrot. Mix with the measured oil and serve.",
                new String[]{"carrot", "oil"},
                new double[]{1, 1},
                new String[]{"pieces", "tablespoons"});

        addRecipe(db, "Rice and Beans",
                "Cook the rice and beans, then combine and season.",
                new String[]{"rice", "beans"},
                new double[]{1, 1},
                new String[]{"cups", "cups"});

        addRecipe(db, "Apple Oats",
                "Cook oats and top with chopped apple.",
                new String[]{"oats", "apple"},
                new double[]{1, 1},
                new String[]{"cups", "pieces"});

        addRecipe(db, "Simple French Toast",
                "Dip bread in beaten eggs and fry until golden.",
                new String[]{"bread", "eggs"},
                new double[]{2, 1},
                new String[]{"slices", "pieces"});

        addRecipe(db, "Lemon Water",
                "Squeeze lemon into water and stir.",
                new String[]{"lemon", "water"},
                new double[]{1, 1},
                new String[]{"pieces", "cups"});

        addRecipe(db, "Yoghurt Banana Bowl",
                "Slice the banana and mix with yoghurt.",
                new String[]{"banana", "yoghurt"},
                new double[]{1, 1},
                new String[]{"pieces", "cups"});

        addRecipe(db, "Garlic Potatoes",
                "Cut potatoes, mix with garlic and oil, then cook until tender.",
                new String[]{"potato", "garlic", "oil"},
                new double[]{2, 1, 1},
                new String[]{"pieces", "pieces", "tablespoons"});
    }

    private static void addRecipe(
            DatabaseHelper db,
            String name,
            String instructions,
            String[] ingredients,
            double[] quantities,
            String[] units) {

        long recipeId = db.addRecipe(name, instructions);

        if (recipeId == -1) {
            return;
        }

        for (int i = 0; i < ingredients.length; i++) {
            db.addRecipeIngredient(
                    recipeId,
                    ingredients[i],
                    quantities[i],
                    units[i]);
        }
    }
}


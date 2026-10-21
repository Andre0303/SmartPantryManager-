
package com.example.smartpantry;

public class RecipeSeeder {


    public static void seedRecipes(DatabaseHelper db) {

        // Seed the original recipes only if the database is empty.
        if (db.getRecipeCount() == 0) {

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

        // Add new recipes even when the original recipes already exist.
        addRecipeIfMissing(
                db, "Garlic Bread",
                "Spread butter on bread, add chopped garlic, and toast until golden.",
                new String[]{"bread", "garlic", "butter"},
                new double[]{2, 1, 1},
                new String[]{"slices", "pieces", "tablespoons"});

        addRecipeIfMissing(
                db, "Tomato Rice",
                "Cook the rice. Chop the tomato, cook it with oil, and mix with the rice.",
                new String[]{"rice", "tomato", "oil"},
                new double[]{1, 1, 1},
                new String[]{"cups", "pieces", "tablespoons"});

        addRecipeIfMissing(
                db, "Banana Yoghurt Smoothie",
                "Blend the banana, yoghurt, and milk until smooth.",
                new String[]{"banana", "yoghurt", "milk"},
                new double[]{1, 1, 1},
                new String[]{"pieces", "cups", "cups"});

        addRecipeIfMissing(
                db, "Egg Sandwich",
                "Boil or scramble the eggs, then place them between bread slices.",
                new String[]{"bread", "eggs"},
                new double[]{2, 2},
                new String[]{"slices", "pieces"});

        addRecipeIfMissing(
                db, "Peanut Butter Banana Toast",
                "Toast the bread, spread peanut butter on it, and add sliced banana.",
                new String[]{"bread", "peanut butter", "banana"},
                new double[]{2, 1, 1},
                new String[]{"slices", "tablespoons", "pieces"});
    }



    private static void addRecipeIfMissing(
            DatabaseHelper db,
            String name,
            String instructions,
            String[] ingredients,
            double[] quantities,
            String[] units) {

        long recipeId = db.addRecipeIfMissing(name, instructions);

        if (recipeId == -1) {
            return;
        }

        android.database.Cursor cursor =
                db.getIngredientsForRecipe((int) recipeId);

        try {
            // Do not add ingredients again to an existing recipe.
            if (cursor != null && cursor.getCount() > 0) {
                return;
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        for (int i = 0; i < ingredients.length; i++) {
            db.addRecipeIngredient(
                    recipeId,
                    ingredients[i],
                    quantities[i],
                    units[i]);
        }
    }


    public static void addMissingRecipes(DatabaseHelper db) {

        addRecipeIfMissing(
                db,
                "Garlic Bread",
                "Spread butter on bread, add chopped garlic, and toast until golden.",
                new String[]{"bread", "garlic", "butter"},
                new double[]{2, 1, 1},
                new String[]{"slices", "pieces", "tablespoons"});

        addRecipeIfMissing(
                db,
                "Tomato Rice",
                "Cook the rice. Chop the tomato, cook it with oil, and mix with the rice.",
                new String[]{"rice", "tomato", "oil"},
                new double[]{1, 1, 1},
                new String[]{"cups", "pieces", "tablespoons"});

        addRecipeIfMissing(
                db,
                "Banana Yoghurt Smoothie",
                "Blend the banana, yoghurt, and milk until smooth.",
                new String[]{"banana", "yoghurt", "milk"},
                new double[]{1, 1, 1},
                new String[]{"pieces", "cups", "cups"});

        addRecipeIfMissing(
                db,
                "Egg Sandwich",
                "Boil or scramble the eggs, then place them between bread slices.",
                new String[]{"bread", "eggs"},
                new double[]{2, 2},
                new String[]{"slices", "pieces"});

        addRecipeIfMissing(
                db,
                "Peanut Butter Banana Toast",
                "Toast the bread, spread peanut butter on it, and add sliced banana.",
                new String[]{"bread", "peanut butter", "banana"},
                new double[]{2, 1, 1},
                new String[]{"slices", "tablespoons", "pieces"});
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


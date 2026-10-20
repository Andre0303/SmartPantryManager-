
package com.example.smartpantry;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public class RecipeDetailActivity extends Activity {

    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        databaseHelper = new DatabaseHelper(this);

        int recipeId = getIntent().getIntExtra("recipe_id", -1);

        LinearLayout mainLayout = new LinearLayout(this);
        mainLayout.setOrientation(LinearLayout.VERTICAL);
        mainLayout.setPadding(32, 32, 32, 32);

        TextView heading = new TextView(this);
        heading.setText("Recipe Details");
        heading.setTextSize(26);
        heading.setPadding(0, 0, 0, 20);
        mainLayout.addView(heading);

        ScrollView scrollView = new ScrollView(this);

        LinearLayout content = new LinearLayout(this);
        content.setOrientation(LinearLayout.VERTICAL);
        scrollView.addView(content);

        mainLayout.addView(
                scrollView,
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        0,
                        1
                )
        );

        if (recipeId == -1) {
            TextView error = new TextView(this);
            error.setText("Recipe could not be found.");
            error.setTextSize(18);
            content.addView(error);
        } else {
            loadRecipe(recipeId, content);
        }

        Button backButton = new Button(this);
        backButton.setText("Back to Suggested Recipes");
        backButton.setOnClickListener(v -> finish());
        mainLayout.addView(backButton);

        setContentView(mainLayout);
    }

    private void loadRecipe(int recipeId, LinearLayout content) {

        Cursor recipes = databaseHelper.getReadableDatabase().rawQuery(
                "SELECT name, instructions FROM recipes WHERE id = ?",
                new String[]{String.valueOf(recipeId)}
        );

        try {
            if (!recipes.moveToFirst()) {
                TextView error = new TextView(this);
                error.setText("Recipe could not be found.");
                error.setTextSize(18);
                content.addView(error);
                return;
            }

            String recipeName = recipes.getString(
                    recipes.getColumnIndexOrThrow("name")
            );

            String instructions = recipes.getString(
                    recipes.getColumnIndexOrThrow("instructions")
            );

            TextView nameView = new TextView(this);
            nameView.setText(recipeName);
            nameView.setTextSize(24);
            nameView.setPadding(0, 0, 0, 20);
            content.addView(nameView);

            TextView ingredientsHeading = new TextView(this);
            ingredientsHeading.setText("Required Ingredients");
            ingredientsHeading.setTextSize(20);
            content.addView(ingredientsHeading);

            Cursor ingredients =
                    databaseHelper.getIngredientsForRecipe(recipeId);

            try {
                while (ingredients.moveToNext()) {
                    String ingredientName = ingredients.getString(
                            ingredients.getColumnIndexOrThrow(
                                    "ingredient_name"
                            )
                    );

                    double quantity = ingredients.getDouble(
                            ingredients.getColumnIndexOrThrow("quantity")
                    );

                    String unit = ingredients.getString(
                            ingredients.getColumnIndexOrThrow("unit")
                    );

                    TextView ingredientView = new TextView(this);
                    ingredientView.setText(
                            "\u2022 " + ingredientName + ": "
                                    + quantity + " " + unit
                    );
                    ingredientView.setTextSize(17);
                    ingredientView.setPadding(0, 8, 0, 8);
                    content.addView(ingredientView);
                }
            } finally {
                ingredients.close();
            }

            TextView instructionsHeading = new TextView(this);
            instructionsHeading.setText("\nPreparation Instructions");
            instructionsHeading.setTextSize(20);
            content.addView(instructionsHeading);

            TextView instructionsView = new TextView(this);
            instructionsView.setText(instructions);
            instructionsView.setTextSize(17);
            instructionsView.setPadding(0, 8, 0, 20);
            content.addView(instructionsView);

        } finally {
            recipes.close();
        }
    }

    @Override
    protected void onDestroy() {
        databaseHelper.close();
        super.onDestroy();
    }
}

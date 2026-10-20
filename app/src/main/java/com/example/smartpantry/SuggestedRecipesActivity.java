
package com.example.smartpantry;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public class SuggestedRecipesActivity extends Activity {

    private DatabaseHelper databaseHelper;
    private LinearLayout recipeList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        databaseHelper = new DatabaseHelper(this);

        LinearLayout mainLayout = new LinearLayout(this);
        mainLayout.setOrientation(LinearLayout.VERTICAL);
        mainLayout.setPadding(32, 32, 32, 32);

        TextView heading = new TextView(this);
        heading.setText("Suggested Recipes");
        heading.setTextSize(26);
        heading.setPadding(0, 0, 0, 20);
        mainLayout.addView(heading);

        TextView subtitle = new TextView(this);
        subtitle.setText(
                "Recipes you can make using your pantry ingredients."
        );
        subtitle.setTextSize(16);
        mainLayout.addView(subtitle);

        ScrollView scrollView = new ScrollView(this);

        recipeList = new LinearLayout(this);
        recipeList.setOrientation(LinearLayout.VERTICAL);
        scrollView.addView(recipeList);

        mainLayout.addView(
                scrollView,
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        0,
                        1
                )
        );

        Button backButton = new Button(this);
        backButton.setText("Back");
        backButton.setOnClickListener(v -> finish());
        mainLayout.addView(backButton);

        setContentView(mainLayout);
    }

    @Override
    protected void onResume() {
        super.onResume();
        displayRecipes();
    }

    private void displayRecipes() {
        recipeList.removeAllViews();

        Cursor recipes = databaseHelper.getAvailableRecipes();

        try {
            if (recipes.getCount() == 0) {
                TextView emptyMessage = new TextView(this);
                emptyMessage.setText(
                        "No recipes match your pantry yet.\n\n"
                                + "Add more ingredients or check "
                                + "your saved quantities and units."
                );
                emptyMessage.setTextSize(17);
                emptyMessage.setPadding(0, 24, 0, 24);
                recipeList.addView(emptyMessage);
                return;
            }

            while (recipes.moveToNext()) {
                int recipeId = recipes.getInt(
                        recipes.getColumnIndexOrThrow("id")
                );

                String recipeName = recipes.getString(
                        recipes.getColumnIndexOrThrow("name")
                );

                LinearLayout card = new LinearLayout(this);
                card.setOrientation(LinearLayout.VERTICAL);
                card.setPadding(0, 24, 0, 24);

                TextView nameView = new TextView(this);
                nameView.setText(recipeName);
                nameView.setTextSize(22);
                nameView.setPadding(0, 0, 0, 12);
                card.addView(nameView);

                TextView infoView = new TextView(this);
                infoView.setText(
                        "You have all the required ingredients!"
                );
                infoView.setTextSize(16);
                card.addView(infoView);

                Button viewRecipeButton = new Button(this);
                viewRecipeButton.setText("View Recipe");

                // Save this recipe's ID for the button click.
                final int selectedRecipeId = recipeId;

                viewRecipeButton.setOnClickListener(v -> {
                    Intent intent = new Intent(
                            SuggestedRecipesActivity.this,
                            RecipeDetailActivity.class
                    );

                    intent.putExtra("recipe_id", selectedRecipeId);

                    startActivity(intent);
                });

                card.addView(viewRecipeButton);

                TextView divider = new TextView(this);
                divider.setText(
                        "----------------------------------------"
                );
                card.addView(divider);

                recipeList.addView(card);
            }

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

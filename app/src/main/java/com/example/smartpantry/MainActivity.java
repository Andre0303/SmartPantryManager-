
package com.example.smartpantry;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);


        DatabaseHelper db = new DatabaseHelper(this);

        if (!db.hasRecipes()) {
            RecipeSeeder.seedRecipes(db);
        }

// Update instructions for existing recipes
        db.updateRecipeInstructions(
                "Egg Fried Rice",
                "Cook the rice. Scramble the eggs in a pan, add the cooked rice, and stir-fry until heated through."
        );

        db.updateRecipeInstructions(
                "Tomato Pasta",
                "Cook the pasta. Chop the tomatoes, cook them in a pan until soft, and mix with the pasta."
        );

        db.updateRecipeInstructions(
                "Potato Mash",
                "Boil the potatoes until soft. Drain, mash thoroughly, and add the measured salt."
        );

        db.updateRecipeInstructions(
                "Carrot Salad",
                "Wash and grate the carrot. Mix with the measured oil and serve."
        );

        db.close();


        // Connect the four buttons from activity_main.xml
        Button addButton = findViewById(R.id.addIngredientButton);
        Button pantryButton = findViewById(R.id.viewPantryButton);
        Button recipesButton = findViewById(R.id.suggestedRecipesButton);
        Button settingsButton = findViewById(R.id.settingsButton);

        // Open the Add Ingredient screen
        addButton.setOnClickListener(v -> {
            Intent intent = new Intent(
                    MainActivity.this,
                    IngredientFormActivity.class
            );
            startActivity(intent);
        });

        // Open the View Pantry screen
        pantryButton.setOnClickListener(v -> {
            Intent intent = new Intent(
                    MainActivity.this,
                    PantryActivity.class
            );
            startActivity(intent);
        });

        // Open the Suggested Recipes screen
        recipesButton.setOnClickListener(v -> {
            Intent intent = new Intent(
                    MainActivity.this,
                    SuggestedRecipesActivity.class
            );
            startActivity(intent);
        });

        // Open the Settings screen
        settingsButton.setOnClickListener(v -> {
            Intent intent = new Intent(
                    MainActivity.this,
                    SettingsActivity.class
            );
            startActivity(intent);
        });

        // Handle screen insets
        ViewCompat.setOnApplyWindowInsetsListener(
                findViewById(R.id.main), (v, insets) -> {
                    Insets systemBars = insets.getInsets(
                            WindowInsetsCompat.Type.systemBars()
                    );

                    v.setPadding(
                            systemBars.left,
                            systemBars.top,
                            systemBars.right,
                            systemBars.bottom
                    );

                    return insets;
                });
    }
}

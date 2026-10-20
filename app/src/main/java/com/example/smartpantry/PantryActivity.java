
package com.example.smartpantry;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class PantryActivity extends Activity {

    private DatabaseHelper databaseHelper;
    private RecyclerView recyclerView;
    private IngredientAdapter ingredientAdapter;
    private TextView emptyMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        databaseHelper = new DatabaseHelper(this);

        LinearLayout mainLayout = new LinearLayout(this);
        mainLayout.setOrientation(LinearLayout.VERTICAL);
        mainLayout.setPadding(32, 32, 32, 32);

        TextView heading = new TextView(this);
        heading.setText("My Pantry");
        heading.setTextSize(26);
        heading.setPadding(0, 0, 0, 16);
        mainLayout.addView(heading);

        emptyMessage = new TextView(this);
        emptyMessage.setText(
                "Your pantry is empty. Add an ingredient first."
        );
        emptyMessage.setTextSize(17);
        emptyMessage.setPadding(0, 20, 0, 20);
        mainLayout.addView(emptyMessage);

        recyclerView = new RecyclerView(this);
        recyclerView.setLayoutManager(
                new LinearLayoutManager(this)
        );

        ingredientAdapter = new IngredientAdapter(
                null,
                new IngredientAdapter.OnIngredientActionListener() {

                    @Override
                    public void onEdit(int ingredientId) {
                        Intent intent = new Intent(
                                PantryActivity.this,
                                IngredientFormActivity.class
                        );

                        intent.putExtra("ingredient_id", ingredientId);
                        startActivity(intent);
                    }

                    @Override
                    public void onDelete(
                            int ingredientId,
                            String ingredientName) {

                        confirmDelete(ingredientId, ingredientName);
                    }
                }
        );

        recyclerView.setAdapter(ingredientAdapter);

        mainLayout.addView(
                recyclerView,
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
        displayIngredients();
    }

    private void displayIngredients() {
        Cursor cursor = databaseHelper.getAllIngredients();

        if (cursor.getCount() == 0) {
            emptyMessage.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            emptyMessage.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }

        ingredientAdapter.swapCursor(cursor);
    }

    private void confirmDelete(
            int ingredientId,
            String ingredientName) {

        new AlertDialog.Builder(PantryActivity.this)
                .setTitle("Delete ingredient")
                .setMessage(
                        "Are you sure you want to delete "
                                + ingredientName + "?"
                )
                .setPositiveButton("Delete", (dialog, which) -> {

                    int rowsDeleted =
                            databaseHelper.deleteIngredient(ingredientId);

                    if (rowsDeleted > 0) {
                        Toast.makeText(
                                PantryActivity.this,
                                ingredientName + " deleted",
                                Toast.LENGTH_SHORT
                        ).show();

                        displayIngredients();
                    } else {
                        Toast.makeText(
                                PantryActivity.this,
                                "Could not delete ingredient",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    protected void onDestroy() {
        if (ingredientAdapter != null) {
            ingredientAdapter.swapCursor(null);
        }

        if (databaseHelper != null) {
            databaseHelper.close();
        }

        super.onDestroy();
    }
}

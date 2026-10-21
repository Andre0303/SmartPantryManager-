
package com.example.smartpantry;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class IngredientFormActivity extends Activity {

    private EditText nameInput;
    private EditText quantityInput;
    private EditText expiryInput;
    private Spinner unitSpinner;
    private DatabaseHelper databaseHelper;

    private int ingredientId = -1;

    private final String[] units = {
            "pieces", "g", "kg", "ml", "litres",
            "cups", "slices", "teaspoons", "tablespoons", "pinches"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        databaseHelper = new DatabaseHelper(this);

        // Check whether an existing ingredient is being edited.
        ingredientId = getIntent().getIntExtra("ingredient_id", -1);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(32, 32, 32, 32);

        nameInput = new EditText(this);
        nameInput.setHint("Ingredient name");
        layout.addView(nameInput);

        quantityInput = new EditText(this);
        quantityInput.setHint("Quantity");
        quantityInput.setInputType(
                android.text.InputType.TYPE_CLASS_NUMBER
                        | android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL
                        | android.text.InputType.TYPE_NUMBER_FLAG_SIGNED
        );
        layout.addView(quantityInput);

        unitSpinner = new Spinner(this);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                units
        );

        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        unitSpinner.setAdapter(adapter);
        layout.addView(unitSpinner);

        expiryInput = new EditText(this);
        expiryInput.setHint("Expiry date (YYYY-MM-DD)");
        layout.addView(expiryInput);

        Button saveButton = new Button(this);

        if (ingredientId == -1) {
            saveButton.setText("Save Ingredient");
        } else {
            saveButton.setText("Update Ingredient");
            loadIngredient();
        }

        layout.addView(saveButton);

        setContentView(layout);

        saveButton.setOnClickListener(v -> saveIngredient());
    }

    private void loadIngredient() {
        android.database.Cursor cursor =
                databaseHelper.getAllIngredients();

        try {
            while (cursor.moveToNext()) {
                int id = cursor.getInt(
                        cursor.getColumnIndexOrThrow("id")
                );

                if (id == ingredientId) {
                    nameInput.setText(cursor.getString(
                            cursor.getColumnIndexOrThrow("name")
                    ));

                    quantityInput.setText(String.valueOf(
                            cursor.getDouble(
                                    cursor.getColumnIndexOrThrow("quantity")
                            )
                    ));

                    String currentUnit = cursor.getString(
                            cursor.getColumnIndexOrThrow("unit")
                    );

                    for (int i = 0; i < units.length; i++) {
                        if (units[i].equalsIgnoreCase(currentUnit)) {
                            unitSpinner.setSelection(i);
                            break;
                        }
                    }

                    String expiry = cursor.getString(
                            cursor.getColumnIndexOrThrow("expiry_date")
                    );

                    expiryInput.setText(expiry == null ? "" : expiry);
                    break;
                }
            }
        } finally {
            cursor.close();
        }
    }

    private void saveIngredient() {
        String name = nameInput.getText().toString().trim();
        String quantityText =
                quantityInput.getText().toString().trim();
        String expiry = expiryInput.getText().toString().trim();
        String unit = unitSpinner.getSelectedItem().toString();


        if (!expiry.isEmpty()) {
            SimpleDateFormat dateFormat =
                    new SimpleDateFormat("yyyy-MM-dd", Locale.US);

            dateFormat.setLenient(false);

            ParsePosition position = new ParsePosition(0);
            Date parsedDate = dateFormat.parse(expiry, position);

            if (parsedDate == null || position.getIndex() != expiry.length()) {
                Toast.makeText(
                        this,
                        "Enter a valid expiry date (YYYY-MM-DD)",
                        Toast.LENGTH_SHORT
                ).show();
                return;
            }
        }


        if (name.isEmpty() || quantityText.isEmpty()) {
            Toast.makeText(
                    this,
                    "Enter the ingredient name and quantity",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        try {
            double quantity = Double.parseDouble(quantityText);

            if (quantity <= 0
                    || Double.isNaN(quantity)
                    || Double.isInfinite(quantity)) {
                Toast.makeText(
                        this,
                        "Quantity must be greater than zero",
                        Toast.LENGTH_SHORT
                ).show();
                return;
            }

            int result;

            if (ingredientId == -1) {
                long insertedId = databaseHelper.addIngredient(
                        name, quantity, unit, expiry
                );
                result = insertedId == -1 ? -1 : 1;
            } else {
                result = databaseHelper.updateIngredient(
                        ingredientId, name, quantity, unit, expiry
                );
            }

            if (result > 0) {
                Toast.makeText(
                        this,
                        ingredientId == -1
                                ? "Ingredient saved!"
                                : "Ingredient updated!",
                        Toast.LENGTH_SHORT
                ).show();
                finish();
            } else {
                Toast.makeText(
                        this,
                        "Could not save the ingredient",
                        Toast.LENGTH_SHORT
                ).show();
            }

        } catch (NumberFormatException e) {
            Toast.makeText(
                    this,
                    "Enter a valid quantity",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }

    @Override
    protected void onDestroy() {
        databaseHelper.close();
        super.onDestroy();
    }
}

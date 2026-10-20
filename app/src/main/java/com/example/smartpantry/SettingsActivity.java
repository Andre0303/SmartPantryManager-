package com.example.smartpantry;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.graphics.Color;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class SettingsActivity extends Activity {

    private EditText nameInput;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        preferences = getSharedPreferences(
                "SmartPantrySettings",
                MODE_PRIVATE
        );

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(40, 40, 40, 40);
        layout.setBackgroundColor(0xFFF5F7F2);

        TextView title = new TextView(this);
        title.setText("Settings & Profile");
        title.setTextSize(26);
        title.setTextColor(0xFF245C3A);
        layout.addView(title);

        TextView nameLabel = new TextView(this);
        nameLabel.setText("Your name (optional)");
        nameLabel.setTextSize(16);
        layout.addView(nameLabel);

        nameInput = new EditText(this);
        nameInput.setHint("Enter your name");
        nameInput.setSingleLine(true);

        nameInput.setText(
                preferences.getString("profile_name", "")
        );

        layout.addView(nameInput);

        Button saveButton = new Button(this);
        saveButton.setText("Save Profile");
        layout.addView(saveButton);

        TextView matchingInfo = new TextView(this);
        matchingInfo.setText(
                "Recipe Matching: Always Enabled\n\n"
                        + "The app only suggests recipes when you "
                        + "have every required ingredient in a "
                        + "sufficient quantity."
        );
        matchingInfo.setTextSize(16);
        matchingInfo.setPadding(0, 30, 0, 20);
        layout.addView(matchingInfo);

        TextView aboutInfo = new TextView(this);
        aboutInfo.setText(
                "About Smart Pantry Manager\n\n"
                        + "Smart Pantry Manager helps reduce food "
                        + "waste by suggesting recipes using "
                        + "ingredients already in your pantry."
        );
        aboutInfo.setTextSize(16);
        layout.addView(aboutInfo);

        Button backButton = new Button(this);
        backButton.setText("Back");
        layout.addView(backButton);

        setContentView(layout);

        saveButton.setOnClickListener(v -> {
            String profileName = nameInput.getText()
                    .toString().trim();

            preferences.edit()
                    .putString("profile_name", profileName)
                    .apply();

            Toast.makeText(
                    this,
                    "Profile saved!",
                    Toast.LENGTH_SHORT
            ).show();
        });

        backButton.setOnClickListener(v -> finish());
    }
}

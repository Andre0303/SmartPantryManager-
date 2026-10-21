
package com.example.smartpantry;

import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class IngredientAdapter
        extends RecyclerView.Adapter<IngredientAdapter.IngredientViewHolder> {

    public interface OnIngredientActionListener {
        void onEdit(int ingredientId);

        void onDelete(int ingredientId, String ingredientName);
    }

    private Cursor cursor;
    private final OnIngredientActionListener listener;

    public IngredientAdapter(
            Cursor cursor,
            OnIngredientActionListener listener) {
        this.cursor = cursor;
        this.listener = listener;
    }

    @NonNull
    @Override
    public IngredientViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType) {

        LinearLayout card = new LinearLayout(parent.getContext());
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(24, 20, 24, 20);

        TextView nameView = new TextView(parent.getContext());
        nameView.setTextSize(20);

        TextView quantityView = new TextView(parent.getContext());
        quantityView.setTextSize(16);

        TextView expiryView = new TextView(parent.getContext());
        expiryView.setTextSize(15);

        LinearLayout buttons = new LinearLayout(parent.getContext());
        buttons.setOrientation(LinearLayout.HORIZONTAL);

        Button editButton = new Button(parent.getContext());
        editButton.setText("Edit");

        Button deleteButton = new Button(parent.getContext());
        deleteButton.setText("Delete");

        buttons.addView(
                editButton,
                new LinearLayout.LayoutParams(
                        0,
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        1
                )
        );

        buttons.addView(
                deleteButton,
                new LinearLayout.LayoutParams(
                        0,
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        1
                )
        );

        card.addView(nameView);
        card.addView(quantityView);
        card.addView(expiryView);
        card.addView(buttons);

        return new IngredientViewHolder(
                card, nameView, quantityView, expiryView,
                editButton, deleteButton
        );
    }

    @Override
    public void onBindViewHolder(
            @NonNull IngredientViewHolder holder,
            int position) {

        if (cursor == null || !cursor.moveToPosition(position)) {
            return;
        }

        int id = cursor.getInt(
                cursor.getColumnIndexOrThrow("id")
        );

        String name = cursor.getString(
                cursor.getColumnIndexOrThrow("name")
        );

        double quantity = cursor.getDouble(
                cursor.getColumnIndexOrThrow("quantity")
        );

        String unit = cursor.getString(
                cursor.getColumnIndexOrThrow("unit")
        );

        int expiryIndex = cursor.getColumnIndex("expiry_date");
        String expiry = "";

        if (expiryIndex >= 0 && !cursor.isNull(expiryIndex)) {
            expiry = cursor.getString(expiryIndex);
        }

        holder.nameView.setText(name);
        String quantityText;

        if (quantity == Math.floor(quantity)) {
            quantityText = String.valueOf((long) quantity);
        } else {
            quantityText = String.valueOf(quantity);
        }

        holder.quantityView.setText(
                "Quantity: " + quantityText + " " + unit
        );

        if (expiry == null || expiry.trim().isEmpty()) {
            holder.expiryView.setText("Expiry date: Not provided");
        } else {
            holder.expiryView.setText("Expiry date: " + expiry);
        }

        holder.editButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onEdit(id);
            }
        });

        holder.deleteButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDelete(id, name);
            }
        });
    }

    @Override
    public int getItemCount() {
        return cursor == null ? 0 : cursor.getCount();
    }

    public void swapCursor(Cursor newCursor) {
        Cursor oldCursor = cursor;
        cursor = newCursor;
        notifyDataSetChanged();

        if (oldCursor != null && oldCursor != newCursor) {
            oldCursor.close();
        }
    }

    static class IngredientViewHolder extends RecyclerView.ViewHolder {

        TextView nameView;
        TextView quantityView;
        TextView expiryView;
        Button editButton;
        Button deleteButton;

        IngredientViewHolder(
                @NonNull View itemView,
                TextView nameView,
                TextView quantityView,
                TextView expiryView,
                Button editButton,
                Button deleteButton) {

            super(itemView);

            this.nameView = nameView;
            this.quantityView = quantityView;
            this.expiryView = expiryView;
            this.editButton = editButton;
            this.deleteButton = deleteButton;
        }
    }
}

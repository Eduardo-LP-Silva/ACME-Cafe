package com.ejn.cmov.acmecafe.mobile.ui.order;

import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.ejn.cmov.acmecafe.mobile.R;
import com.ejn.cmov.acmecafe.mobile.data.model.ItemModel;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

public class OrderItemViewHolder extends RecyclerView.ViewHolder {
    private final TextView itemNameView;
    private final TextView itemPriceView;
    private final EditText itemQuantityEditor;

    public OrderItemViewHolder(@NonNull View itemView) {
        super(itemView);
        itemNameView = (TextView) itemView.findViewById(R.id.order_item_name);
        itemPriceView = (TextView) itemView.findViewById(R.id.order_item_price);
        itemQuantityEditor = (EditText) itemView.findViewById(R.id.order_item_quantity);
    }

    public void bindData(final ItemModel itemModel) {
        itemNameView.setText(itemModel.getName());
        itemPriceView.setText(String.format("%s€", itemModel.getPrice()));
        itemQuantityEditor.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                int itemQuantity = itemQuantityEditor.getText().length() > 0 ? Integer.parseInt(itemQuantityEditor.getText().toString()) : 0;

                if (itemQuantity <= 0)
                    itemQuantityEditor.setError("Item quantity must be greater than 0");
            }
        });
    }
}

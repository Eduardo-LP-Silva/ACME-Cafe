package com.ejn.cmov.acmecafe.mobile.ui.receipts.Receipt;

import android.view.View;
import android.widget.TextView;

import com.ejn.cmov.acmecafe.mobile.R;
import com.ejn.cmov.acmecafe.mobile.data.model.ItemModel;
import com.ejn.cmov.acmecafe.mobile.data.model.ReceiptModel;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ReceiptViewHolder extends RecyclerView.ViewHolder {
    private final TextView nameView;
    private final TextView costView;
    private final TextView quantityView;

    public ReceiptViewHolder(@NonNull View itemView) {
        super(itemView);
        this.nameView = itemView.findViewById(R.id.receipt_line_text);
        this.costView = itemView.findViewById(R.id.receipt_line_cost);;
        this.quantityView = itemView.findViewById(R.id.receipt_line_quantity);
    }

    public void bindData(final ItemModel item) {
        nameView.setText(item.getName());
        costView.setText(String.format("%s€", item.getPrice()));
        quantityView.setText(String.format("x %s", item.getQuantity()));
    }
}

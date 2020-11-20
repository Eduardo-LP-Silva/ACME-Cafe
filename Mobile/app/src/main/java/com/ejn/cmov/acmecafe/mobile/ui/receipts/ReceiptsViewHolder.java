package com.ejn.cmov.acmecafe.mobile.ui.receipts;

import android.view.View;
import android.widget.TextView;

import com.ejn.cmov.acmecafe.mobile.R;
import com.ejn.cmov.acmecafe.mobile.data.model.ReceiptModel;
import com.ejn.cmov.acmecafe.mobile.ui.OnRecyclerItemClickListener;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ReceiptsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    private final TextView dateView;
    private final TextView totalView;
    private final TextView quantityView;
    private final OnRecyclerItemClickListener onItemClickListener;

    public ReceiptsViewHolder(@NonNull View itemView, OnRecyclerItemClickListener onItemClickListener) {
        super(itemView);
        this.dateView = itemView.findViewById(R.id.receipt_line_text);
        this.totalView = itemView.findViewById(R.id.receipt_line_cost);
        this.quantityView = itemView.findViewById(R.id.receipt_line_quantity);
        this.onItemClickListener = onItemClickListener;

        itemView.setOnClickListener(this);
    }

    public void bindData(final ReceiptModel receipt) {
        dateView.setText(receipt.getDate());
        totalView.setText(String.format("%s€", receipt.getTotal()));
        quantityView.setText("");
    }

    @Override
    public void onClick(View view) {
        onItemClickListener.onItemClick(view, getAdapterPosition());
    }
}

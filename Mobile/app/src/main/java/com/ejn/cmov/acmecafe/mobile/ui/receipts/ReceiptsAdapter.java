package com.ejn.cmov.acmecafe.mobile.ui.receipts;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ejn.cmov.acmecafe.mobile.R;
import com.ejn.cmov.acmecafe.mobile.data.model.ReceiptModel;
import com.ejn.cmov.acmecafe.mobile.ui.OnRecyclerItemClickListener;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ReceiptsAdapter extends RecyclerView.Adapter {
    private final ReceiptModel[] receipts;
    private final OnRecyclerItemClickListener onItemClickListener;

    public ReceiptsAdapter(ReceiptModel[] receipts, OnRecyclerItemClickListener onItemClickListener) {
        this.receipts = receipts;
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);

        return new ReceiptsViewHolder(view, onItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((ReceiptsViewHolder) holder).bindData(receipts[position]);
    }

    @Override
    public int getItemCount() {
        return receipts.length;
    }

    @Override
    public int getItemViewType(final int position) {
        return R.layout.receipt_line_view;
    }
}

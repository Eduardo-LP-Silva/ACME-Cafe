package com.ejn.cmov.acmecafe.mobile.ui.order;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ejn.cmov.acmecafe.mobile.R;
import com.ejn.cmov.acmecafe.mobile.data.model.ItemModel;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class OrderItemsAdapter extends RecyclerView.Adapter {
    private final ArrayList<ItemModel> items;

    public OrderItemsAdapter(ArrayList<ItemModel> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);

        return new OrderItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((OrderItemViewHolder) holder).bindData(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public int getItemViewType(final int position) {
        return R.layout.order_item_view;
    }
}

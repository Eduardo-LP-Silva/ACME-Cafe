package com.ejn.cmov.acmecafe.mobile.ui.items;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ejn.cmov.acmecafe.mobile.R;
import com.ejn.cmov.acmecafe.mobile.data.model.ItemModel;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ItemAdapter extends RecyclerView.Adapter {
    private final ItemModel[] items;

    public ItemAdapter(ItemModel[] items) {
        this.items = items;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);

        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((ItemViewHolder) holder).bindData(items[position]);
    }

    @Override
    public int getItemCount() {
        return items.length;
    }

    @Override
    public int getItemViewType(final int position) {
        return R.layout.item_view;
    }
}

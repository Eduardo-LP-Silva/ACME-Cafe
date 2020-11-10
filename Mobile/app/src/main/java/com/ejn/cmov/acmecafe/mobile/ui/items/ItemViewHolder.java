package com.ejn.cmov.acmecafe.mobile.ui.items;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ejn.cmov.acmecafe.mobile.R;
import com.ejn.cmov.acmecafe.mobile.data.model.ItemModel;
import com.ejn.cmov.acmecafe.mobile.ui.OnRecyclerItemClickListener;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

public class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    private final ImageView itemIconView;
    private final TextView itemNameView;
    private final TextView itemPriceView;
    private final OnRecyclerItemClickListener onItemClickListener;

    public ItemViewHolder(@NonNull View itemView, OnRecyclerItemClickListener onItemClickListener) {
        super(itemView);
        itemIconView = (ImageView) itemView.findViewById(R.id.item_icon);
        itemNameView = (TextView) itemView.findViewById(R.id.item_name);
        itemPriceView = (TextView) itemView.findViewById(R.id.item_price);
        this.onItemClickListener = onItemClickListener;

        itemView.setOnClickListener(this);
    }

    public void bindData(final ItemModel itemModel) {
        String drawableUri = String.format("@drawable/%s", itemModel.getIcon());
        int icoResource = itemView.getResources().getIdentifier(drawableUri, null, itemView.getContext().getPackageName());
        Drawable drawable = ResourcesCompat.getDrawable(itemView.getResources(), icoResource, null);

        itemIconView.setImageURI(null); //To refresh
        itemIconView.setImageDrawable(drawable);
        itemNameView.setText(itemModel.getName());
        itemPriceView.setText(String.format("%s€", itemModel.getPrice()));
    }

    @Override
    public void onClick(View view) {
        onItemClickListener.onItemClick(view, getAdapterPosition());
    }
}

package com.ejn.cmov.acmecafe.mobile.ui.items;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ejn.cmov.acmecafe.mobile.R;
import com.ejn.cmov.acmecafe.mobile.data.model.ItemModel;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

public class ItemViewHolder extends RecyclerView.ViewHolder {
    private final ImageView itemIconView;
    private final TextView itemNameView;
    private final TextView itemPriceView;

    public ItemViewHolder(@NonNull View itemView) {
        super(itemView);
        itemIconView = (ImageView) itemView.findViewById(R.id.item_icon);
        itemNameView = (TextView) itemView.findViewById(R.id.item_name);
        itemPriceView = (TextView) itemView.findViewById(R.id.item_price);
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
}

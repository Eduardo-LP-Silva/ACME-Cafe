package com.ejn.cmov.acmecafe.mobile.ui.items;

import android.net.Uri;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ejn.cmov.acmecafe.mobile.R;
import com.ejn.cmov.acmecafe.mobile.data.model.ItemModel;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ItemViewHolder extends RecyclerView.ViewHolder {
    private ImageView itemIconView;
    private TextView itemNameView;
    private TextView itemPriceView;

    public ItemViewHolder(@NonNull View itemView) {
        super(itemView);
        itemIconView = (ImageView) itemView.findViewById(R.id.item_icon);
        itemNameView = (TextView) itemView.findViewById(R.id.item_name);
        itemPriceView = (TextView) itemView.findViewById(R.id.item_price);
    }

    public void bindData(final ItemModel itemModel) {
        Uri iconUri = Uri.parse(String.format("@drawable/%s", itemModel.getIcon()));

        itemIconView.setImageURI(null); //To refresh
        itemIconView.setImageURI(iconUri);
        itemNameView.setText(itemModel.getName());
        itemPriceView.setText(itemModel.getPrice());
    }
}

package com.ejn.cmov.acmecafe.mobile.data.model;

import java.io.Serializable;

import androidx.annotation.Nullable;

public class ItemModel implements Serializable {
    private String id;
    private String name;
    private String price;
    private String icon;
    private String lastUpdate;
    private String quantity;
    private boolean selected;

    public ItemModel(String id, String name, String price, String icon, String lastUpdate, @Nullable String quantity) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.icon = icon;
        this.lastUpdate = lastUpdate;
        this.selected = false;

        if (quantity != null)
            this.quantity = quantity;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ItemModel))
            return false;

        final ItemModel item = (ItemModel) obj;

        return lastUpdate.equals(item.getLastUpdate());
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPrice() {
        return price;
    }

    public String getIcon() {
        return icon;
    }

    public String getLastUpdate() {
        return lastUpdate;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getQuantity() {
        return quantity;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }
}

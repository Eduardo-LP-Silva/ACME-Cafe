package com.ejn.cmov.acmecafe.mobile.data.model;

import java.io.Serializable;

public class ItemModel implements Serializable {
    private String id;
    private String name;
    private String price;
    private String icon;
    private String lastUpdate;

    public ItemModel(String id, String name, String price, String icon, String lastUpdate) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.icon = icon;
        this.lastUpdate = lastUpdate;
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
}

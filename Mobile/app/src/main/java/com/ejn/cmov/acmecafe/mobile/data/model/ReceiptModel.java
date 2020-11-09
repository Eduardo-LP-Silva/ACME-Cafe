package com.ejn.cmov.acmecafe.mobile.data.model;

import java.io.Serializable;

public class ReceiptModel implements Serializable {
    private ItemModel[] items;
    private String date;
    private String total;
    private boolean discountVoucher;
    private int coffeeVouchers;

    public ReceiptModel(ItemModel[] items, String date, String total, boolean discountVoucher, int coffeeVouchers) {
        this.items = items;
        this.date = date;
        this.total = total;
        this.discountVoucher = discountVoucher;
        this.coffeeVouchers = coffeeVouchers;
    }

    public ItemModel[] getItems() {
        return items;
    }

    public String getDate() {
        return date;
    }

    public String getTotal() {
        return total;
    }

    public boolean isDiscountVoucher() {
        return discountVoucher;
    }

    public int getCoffeeVouchers() {
        return coffeeVouchers;
    }
}

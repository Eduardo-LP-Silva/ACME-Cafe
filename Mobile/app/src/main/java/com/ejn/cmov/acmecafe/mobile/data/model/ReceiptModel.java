package com.ejn.cmov.acmecafe.mobile.data.model;

import java.io.Serializable;
import java.util.ArrayList;

public class ReceiptModel implements Serializable {
    private ItemModel[] items;
    private String date;
    private String total;
    private VoucherModel discountVoucher;
    private ArrayList<VoucherModel> coffeeVouchers;

    public ReceiptModel(ItemModel[] items, String date, String total, VoucherModel discountVoucher, ArrayList<VoucherModel> coffeeVouchers) {
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

    public VoucherModel getDiscountVoucher() {
        return discountVoucher;
    }

    public ArrayList<VoucherModel> getCoffeeVouchers() {
        return coffeeVouchers;
    }
}

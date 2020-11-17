package com.ejn.cmov.acmecafe.terminal;

public class ReceiptItem {
    public String name;
    public int amount; // If amount == -1 then this object represents the "total" row and shows no unit price or amount
    public double unitPrice;
    public double subTotal;

    public ReceiptItem() {
    }

    public ReceiptItem(String name, int amount, double unitPrice) {
        this.name = name;
        this.amount = amount;
        this.unitPrice = unitPrice;
        this.subTotal = (amount != -1) ? amount * unitPrice : unitPrice;
    }

}

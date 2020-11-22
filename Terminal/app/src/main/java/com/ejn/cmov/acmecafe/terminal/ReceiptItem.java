package com.ejn.cmov.acmecafe.terminal;

public class ReceiptItem {
    public String name;
    public int amount; // If amount == -1 then this object represents the "total" row and shows no unit price or amount
    public double unitPrice;
    public double previousPrice;
    public double subTotal;
    public int type = 0; // 0 = normal, 1 = Header, 2 = Total

    public ReceiptItem() {
    }

    public ReceiptItem(String name, int amount, double unitPrice) {
        this.name = name;
        this.amount = amount;
        this.unitPrice = unitPrice;
        this.previousPrice = -1;
        this.subTotal = (amount != -1) ? amount * unitPrice : unitPrice;
    }

    public ReceiptItem(String name, int amount, double unitPrice, double previousPrice) {
        this.name = name;
        this.amount = amount;
        this.unitPrice = unitPrice;
        this.previousPrice = previousPrice;
        this.subTotal = (amount != -1) ? amount * unitPrice : unitPrice;
    }


    public void CalculateSubTotal()
    {
        this.subTotal =  amount * unitPrice;
    }


}

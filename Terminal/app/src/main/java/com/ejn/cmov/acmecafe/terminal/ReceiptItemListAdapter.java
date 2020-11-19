package com.ejn.cmov.acmecafe.terminal;

import android.content.Context;
import android.graphics.Typeface;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ReceiptItemListAdapter extends ArrayAdapter<ReceiptItem> {

    private static final String TAG = "ReceiptItemListAdapter";
    private Context context;
    private int resource;

    public ReceiptItemListAdapter(Context context, int resource, ArrayList<ReceiptItem> objects) {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        String name = getItem(position).name;
        int amount = getItem(position).amount;
        double unitPrice = getItem(position).unitPrice;
        double subTotal = getItem(position).subTotal;

        LayoutInflater inflater = LayoutInflater.from(context);
        convertView = inflater.inflate(resource, parent, false);

        TextView itemNameView = (TextView) convertView.findViewById(R.id.item_name_view);
        TextView amountView = (TextView) convertView.findViewById(R.id.item_amount_view);
        TextView unitPriceView = (TextView) convertView.findViewById(R.id.unit_price_view);
        unitPriceView.setText("");
        TextView subTotalView = (TextView) convertView.findViewById(R.id.sub_total_view);
        subTotalView.setText("");

        itemNameView.setText(name);
        if (amount != -1) {
            amountView.setText(Integer.toString(amount));
            unitPriceView.setText(unitPrice + "€");
        }
        else {
            itemNameView.setTypeface(null, Typeface.BOLD);
            subTotalView.setTypeface(null, Typeface.BOLD);
            amountView.setText("");
            unitPriceView.setText("");
        }
        subTotalView.setText(subTotal + "€");

        return convertView;
    }
}

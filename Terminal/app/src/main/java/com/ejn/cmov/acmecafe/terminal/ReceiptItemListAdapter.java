package com.ejn.cmov.acmecafe.terminal;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.text.DecimalFormat;
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
        double previousPrice = getItem(position).previousPrice;
        double subTotal = getItem(position).subTotal;
        int type = getItem(position).type;

        LayoutInflater inflater = LayoutInflater.from(context);
        convertView = inflater.inflate(resource, parent, false);

        TextView itemNameView = (TextView) convertView.findViewById(R.id.item_name_view);
        TextView amountView = (TextView) convertView.findViewById(R.id.item_amount_view);
        TextView unitPriceView = (TextView) convertView.findViewById(R.id.unit_price_view);
        TextView previousPriceView = (TextView) convertView.findViewById(R.id.previous_price_view);
        TextView subTotalView = (TextView) convertView.findViewById(R.id.sub_total_view);

        itemNameView.setText(name);

        subTotal = Math.round(subTotal * 100.0)/100.0;

        if (type == 0) {
            amountView.setText(String.valueOf( amount));
            unitPriceView.setText(unitPrice + "€");

            if (previousPrice != -1) {
                previousPriceView.setText(String.valueOf(previousPrice) + '€');
                previousPriceView.setPaintFlags(previousPriceView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                previousPriceView.setVisibility(View.VISIBLE);
            }
            else {
                previousPriceView.setVisibility(View.INVISIBLE);
            }

            subTotalView.setText(subTotal + "€");
        }

        if (type == 1) {
            itemNameView.setTypeface(null, Typeface.BOLD);
            subTotalView.setTypeface(null, Typeface.BOLD);
            amountView.setText("");
            unitPriceView.setText("");
            previousPriceView.setText("");
            subTotalView.setText(subTotal + "€");
        }

        if (type == 2) {
            itemNameView.setText("Name");
            itemNameView.setTypeface(null, Typeface.BOLD);
            amountView.setText("Amount");
            amountView.setTypeface(null, Typeface.BOLD);
            unitPriceView.setText("Price per");
            unitPriceView.setTypeface(null, Typeface.BOLD);
            previousPriceView.setText("");
            subTotalView.setText("Total");
            subTotalView.setTypeface(null, Typeface.BOLD);
        }


        return convertView;
    }
}

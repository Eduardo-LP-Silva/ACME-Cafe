package com.ejn.cmov.acmecafe.mobile.ui.receipts.Receipt;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ejn.cmov.acmecafe.mobile.R;
import com.ejn.cmov.acmecafe.mobile.data.model.ItemModel;
import com.ejn.cmov.acmecafe.mobile.data.model.ReceiptModel;
import com.ejn.cmov.acmecafe.mobile.ui.items.ItemAdapter;

public class ReceiptFragment extends Fragment {
    private static final String RECEIPT_PARAM = "receipt";
    private ReceiptModel receiptModel;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (getArguments() != null) {
            receiptModel = (ReceiptModel) getArguments().getSerializable(RECEIPT_PARAM);

            final TextView coffeeVoucherNoView = requireActivity().findViewById(R.id.receipt_coffee_voucher_no);
            final TextView discountVoucherView = requireActivity().findViewById(R.id.receipt_discount_voucher);
            final TextView totalView = requireActivity().findViewById(R.id.receipt_total);
            final TextView dateView = requireActivity().findViewById(R.id.receipt_date);

            String coffeeVouchers = requireActivity().getResources().getString(R.string.receipt_coffee_voucher_no) + " "
                    + receiptModel.getCoffeeVouchers().size();
            String discountVoucher = requireActivity().getResources().getString(R.string.receipt_discount_voucher) + " "
                    + (receiptModel.getDiscountVoucher() != null ? "Yes" : "No");
            String total = requireActivity().getResources().getString(R.string.receipt_total) + " "
                    + receiptModel.getTotal() + "€";

            coffeeVoucherNoView.setText(coffeeVouchers);
            discountVoucherView.setText(discountVoucher);
            totalView.setText(total);
            dateView.setText(receiptModel.getDate());

            ItemModel[] items = receiptModel.getItems();

            ReceiptAdapter receiptAdapter = new ReceiptAdapter(items);
            RecyclerView recyclerView = requireActivity().findViewById(R.id.receipt_item_list);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            recyclerView.setAdapter(receiptAdapter);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_receipt, container, false);
    }
}
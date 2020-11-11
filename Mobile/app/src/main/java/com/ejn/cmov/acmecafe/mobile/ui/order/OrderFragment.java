package com.ejn.cmov.acmecafe.mobile.ui.order;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.ejn.cmov.acmecafe.mobile.R;
import com.ejn.cmov.acmecafe.mobile.data.model.VoucherModel;
import com.ejn.cmov.acmecafe.mobile.ui.MainMenuActivity;
import com.ejn.cmov.acmecafe.mobile.ui.ViewModelFactory;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Hashtable;

public class OrderFragment extends Fragment {
    private static final String ORDER_PARAM = "items";
    private OrderViewModel orderViewModel;
    private FloatingActionButton getVouchersBtn;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        orderViewModel = new ViewModelProvider(this, new ViewModelFactory()).get(OrderViewModel.class);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (getArguments() != null) {
            //TODO Fill recycler view with items data
        }

        getVouchersBtn = requireActivity().findViewById(R.id.order_get_vouchers);
        //getVouchersBtn.setActivated(false);

        orderViewModel.getVouchers().observe(requireActivity(), new Observer<Hashtable<Integer, ArrayList<VoucherModel>>>() {
            @Override
            public void onChanged(Hashtable<Integer, ArrayList<VoucherModel>> voucherTable) {
                // Prevent race condition
                if (!getVouchersBtn.isActivated())
                    getVouchersBtn.setActivated(true);

                final TextView coffeeDiscountVoucherLabel = requireActivity().findViewById(R.id.order_coffee_vouchers_label);
                final TextView priceDiscountVoucherLabel = requireActivity().findViewById(R.id.order_discount_voucher_label);

                coffeeDiscountVoucherLabel.setText(String.format(getString(R.string.coffee_vouchers), voucherTable.get(0).size()));
                priceDiscountVoucherLabel.setText(String.format(getString(R.string.discount_voucher), voucherTable.get(1).size()));
            }
        });

        getVouchersBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                orderViewModel.getNewVouchers(getContext(), ((MainMenuActivity) requireActivity()).getUserID());
            }
        });

    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_orders, container, false);
    }
}
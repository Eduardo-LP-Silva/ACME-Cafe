package com.ejn.cmov.acmecafe.mobile.ui.order;

import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.nfc.tech.NfcA;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ejn.cmov.acmecafe.mobile.R;
import com.ejn.cmov.acmecafe.mobile.data.Authentication;
import com.ejn.cmov.acmecafe.mobile.data.model.ItemModel;
import com.ejn.cmov.acmecafe.mobile.data.model.VoucherModel;
import com.ejn.cmov.acmecafe.mobile.ui.MainMenuActivity;
import com.ejn.cmov.acmecafe.mobile.ui.ViewModelFactory;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Hashtable;

public class OrderFragment extends Fragment {
    private static final String ORDER_PARAM = "items";
    private OrderViewModel orderViewModel;
    private FloatingActionButton getVouchersBtn;
    private TextView coffeeVoucherEditor;
    private CheckBox discountVoucherCheckBox;
    private Button placeOrderBtn;
    private ArrayList<ItemModel> orderItems;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        orderViewModel = new ViewModelProvider(this, new ViewModelFactory()).get(OrderViewModel.class);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (getArguments() != null) {
            orderItems = (ArrayList<ItemModel>) getArguments().getSerializable(ORDER_PARAM);

            for (ItemModel item: orderItems) {
                item.setQuantity("1");
            }

            OrderItemsAdapter adapter = new OrderItemsAdapter(orderItems);
            RecyclerView recyclerView = requireActivity().findViewById(R.id.order_items_list);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            recyclerView.setAdapter(adapter);
        }

        coffeeVoucherEditor = requireActivity().findViewById(R.id.order_coffee_vouchers_no);
        discountVoucherCheckBox = requireActivity().findViewById(R.id.order_discount_voucher);
        getVouchersBtn = requireActivity().findViewById(R.id.order_get_vouchers);
        placeOrderBtn = requireActivity().findViewById(R.id.order_place_order);

        coffeeVoucherEditor.setEnabled(false);
        coffeeVoucherEditor.setInputType(InputType.TYPE_NULL);
        discountVoucherCheckBox.setEnabled(false);
        getVouchersBtn.setActivated(false);
        placeOrderBtn.setActivated(false);

        orderViewModel.getDataLoaded().observe(requireActivity(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isDataLoaded) {
                if (isDataLoaded)
                    onDataLoaded();
            }
        });

        orderViewModel.getVouchers().observe(requireActivity(), new Observer<Hashtable<Integer, ArrayList<VoucherModel>>>() {
            @Override
            public void onChanged(Hashtable<Integer, ArrayList<VoucherModel>> voucherTable) {
                Hashtable<Integer, ArrayList<VoucherModel>> vouchers = orderViewModel.getVouchers().getValue();
                final TextView coffeeDiscountVoucherLabel = requireActivity().findViewById(R.id.order_coffee_vouchers_label);
                final TextView priceDiscountVoucherLabel = requireActivity().findViewById(R.id.order_discount_voucher_label);

                coffeeDiscountVoucherLabel.setText(String.format(getString(R.string.coffee_vouchers), voucherTable.get(0).size()));
                priceDiscountVoucherLabel.setText(String.format(getString(R.string.discount_voucher), voucherTable.get(1).size()));

                if (vouchers.get(0).size() <= 0) {
                    coffeeVoucherEditor.setText("0");
                    coffeeVoucherEditor.setEnabled(false);
                    coffeeVoucherEditor.setInputType(InputType.TYPE_NULL);
                }
                else {
                    coffeeVoucherEditor.setEnabled(true);
                    coffeeVoucherEditor.setInputType(InputType.TYPE_CLASS_NUMBER);
                }

                discountVoucherCheckBox.setEnabled(vouchers.get(1).size() > 0);
            }
        });

        getVouchersBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                orderViewModel.getNewVouchers(getContext(), ((MainMenuActivity) requireActivity()).getUserID());
                Toast.makeText(getContext(), getString(R.string.fetching_vouchers), Toast.LENGTH_SHORT).show();
            }
        });

        placeOrderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                placeOrder();
            }
        });
    }

    private void placeOrder() {
        JSONObject payload = buildJSONPayload();

        if(payload == null)
            return;

        //orderViewModel.sendOrder(Authentication.buildBodySignedMessage(payload));

        payload = Authentication.buildBodySignedMessage(payload);
        Intent intent = new Intent(getContext(), SendOrderNFCActivity.class);
        intent.putExtra(SendOrderNFCActivity.getPayloadArg(), payload.toString());
        startActivity(intent);
    }

    private JSONObject buildJSONPayload() {
        JSONObject payload = new JSONObject();
        Hashtable<Integer, ArrayList<VoucherModel>> voucherTable = orderViewModel.getVouchers().getValue();
        ArrayList<VoucherModel> coffeeVouchers = voucherTable.get(0);
        ArrayList<VoucherModel> discountVouchers = voucherTable.get(1);
        boolean usedVouchers = false;

        try {
            JSONArray jsonItems = new JSONArray();
            JSONArray jsonVouchers = new JSONArray();


            for (ItemModel item : orderItems) {
                if (item.getQuantity() == null || Integer.parseInt(item.getQuantity()) <= 0)
                    return null;
                else {
                    JSONObject jsonItem = new JSONObject();
                    jsonItem.put("itemId", item.getId());
                    jsonItem.put("quantity", item.getQuantity());
                    Log.i("COMPOSE ORDER", String.format("Item Quantity: %s", item.getQuantity()));
                    jsonItems.put(jsonItem);
                }
            }

            payload.put("items", jsonItems);

            int coffeeVouchersUsed = Integer.parseInt(coffeeVoucherEditor.getText().toString());

            if (coffeeVouchersUsed > 0) {
                for (int i = 0; i < coffeeVouchersUsed; i++) {
                    VoucherModel usedCoffeeVoucher = coffeeVouchers.remove(0);
                    jsonVouchers.put(usedCoffeeVoucher.getId());
                }

                usedVouchers = true;
            }

            if (discountVoucherCheckBox.isChecked()) {
                VoucherModel usedDiscountVoucher = discountVouchers.remove(0);
                jsonVouchers.put(usedDiscountVoucher.getId());
                usedVouchers = true;
            }

            payload.put("vouchers", jsonVouchers);
            payload.put("customerId", ((MainMenuActivity) getActivity()).getUserID());
        }
        catch (JSONException e) {
            Log.e("PLACE ORDER", e.toString());
            return null;
        }

        if (usedVouchers) {
            Hashtable<Integer, ArrayList<VoucherModel>> newVoucherTable = new Hashtable<>();
            newVoucherTable.put(0, coffeeVouchers);
            newVoucherTable.put(1, discountVouchers);
            orderViewModel.saveVouchers(getContext(), newVoucherTable);
        }

        return payload;
    }

    private void onDataLoaded() {
        coffeeVoucherEditor.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                Hashtable<Integer, ArrayList<VoucherModel>> vouchers = orderViewModel.getVouchers().getValue();
                int voucherQuantity = coffeeVoucherEditor.getText().length() > 0 ?
                        Integer.parseInt(coffeeVoucherEditor.getText().toString()) : -1;

                if (voucherQuantity == -1) {
                    coffeeVoucherEditor.setError(getString(R.string.coffee_voucher_empty_error));
                    placeOrderBtn.setActivated(false);
                }
                else if (voucherQuantity > vouchers.get(0).size()) {
                    coffeeVoucherEditor.setError(getString(R.string.coffee_voucher_high_error));
                    placeOrderBtn.setActivated(false);
                }
                else
                    placeOrderBtn.setActivated(true);
            }
        });

        getVouchersBtn.setActivated(true);
        placeOrderBtn.setActivated(true);
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        orderViewModel.loadLocalVouchers(getContext());
        return inflater.inflate(R.layout.fragment_orders, container, false);
    }
}
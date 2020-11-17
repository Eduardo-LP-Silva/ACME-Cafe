package com.ejn.cmov.acmecafe.terminal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    private Button button;
    ThreadExecutor executor;
    RemoteDataRepository repository;

    JSONObject orderReceived;
    // { orderId: newOrder._id, totalPrice: newOrder.totalPrice, vouchers: newOrder.vouchers }
    JSONObject orderResponse;

    // Holds the (orderId,orderNumber) relationship
    ArrayList<Pair<String, Integer>> orderNumbers;
    // Tracks the current order number
    int currentOrderNumber;

    ItemModel[] storeItems;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_recents:
                        Toast.makeText(MainActivity.this, "New", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.action_favorites:
                        Toast.makeText(MainActivity.this, "Order", Toast.LENGTH_SHORT).show();
                        break;
                }
                return true;
            }
        });

        ChangeOrderTextViewVisibility(View.INVISIBLE);

        executor = new ThreadExecutor();
        repository = RemoteDataRepository.getInstance(executor, "192.168.1.88", 8080);

        orderNumbers = new ArrayList<>();
        currentOrderNumber = 0;

        repository.getItems(new Callback<ItemModel[]>() {
            @Override
            public void onComplete(Result<ItemModel[]> result) {

                if (result instanceof Result.Success) {
                    Result.Success<ItemModel[]> newResult = (Result.Success<ItemModel[]>) result;
                    storeItems = newResult.getData();
                }

            }

            @Override
            public void onComplete(Result.Error<String> error) {
                Log.e("get_items", error.getError());
            }
        });

        button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Simulate an NFC order
                try {
                    String orderMsg = "{\"data\":{\"items\":[{\"itemId\":\"e43d8eb0-1df3-11eb-8bf4-0346e6a5d6a0\",\"quantity\":\"1\"},{\"itemId\":\"c2fbeb40-1e87-11eb-930b-6bf5be424b1f\",\"quantity\":\"1\"}],\"vouchers\":[],\"customerId\":\"f08aa2d0-24ff-11eb-9e41-995bcb66b787\",\"timestamp\":\"1605378731\"},\"signature\":\"68ffc857ba50b3ad06c5a90be13edd28e711ca04db3b2a1a7fa11b9ea351249352e5ffc3ad1f11eb3130b1654c3df210897eee2e404abe8c46dab9c9650ac4e7\"}";
                    JSONObject orderJson = new JSONObject(orderMsg);
                    ProcessIncomingOrder(orderJson);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void ProcessIncomingOrder(final JSONObject order)
    {
        boolean debug = true;

        if (debug) {

            try {
                String orderResponseMessage = "{\"orderId\":\"b82c0a60-26a7-11eb-8a16-5db0387a37f8\",\"totalPrice\":\"3.5\",\"vouchers\":\"[]\"}";
                JSONObject orderResponseJson = new JSONObject(orderResponseMessage);
                Log.e("test", orderResponseJson.toString());

                if (GenerateOrderNumber(orderResponseJson)) {
                    orderReceived = order;
                    orderResponse = orderResponseJson;
                    showOrder();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            repository.createOrder(order, ProcessServerResponse(new Callback<JSONObject>() {
                @Override
                public void onComplete(Result<JSONObject> result) {
                    JSONObject orderResponseJson = ((Result.Success<JSONObject>) result).getData();
                    Log.e("test", orderResponseJson.toString());

                    if (GenerateOrderNumber(orderResponseJson)) {
                        orderReceived = order;
                        orderResponse = orderResponseJson;
                        showOrder();
                    }
                }

                @Override
                public void onComplete(Result.Error<String> error) {
                    try {
                        JSONObject errorJson = new JSONObject(error.getError());

                        Log.e("NFC Response", "Code: " + errorJson.get("errorCode"));
                        Log.e("NFC Response", "Message: " + errorJson.get("errorMsg"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }));
        }
    }

    public boolean GenerateOrderNumber(JSONObject order)
    {
        try {
            String orderId = (String) (order.get("orderId"));
            currentOrderNumber++;
            orderNumbers.add(new Pair<>(orderId, currentOrderNumber));

            return true;
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Transforms the message into a JSON. Pretty useless
    private Callback<String> ProcessServerResponse(final Callback<JSONObject> callback)
    {
        return new Callback<String>() {
            @Override
            public void onComplete(Result<String> result) {
                try {
                    Log.e("test", result.toString());

                    if (!(result instanceof Result.Success)) {
                        Result.Error<String> errorResult = (Result.Error<String>) result;
                        callback.onComplete(errorResult);
                    } else {
                        Result.Success<String> successResult = (Result.Success<String>) result;
                        String response = successResult.getData();
                        JSONObject json = new JSONObject(response);
                        Result<JSONObject> res = new Result.Success<JSONObject>(json);
                        callback.onComplete(res);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Result<JSONObject> res = new Result.Error<JSONObject>(null);
                    callback.onComplete(res);
                }
            }

            @Override
            public void onComplete(Result.Error<String> error) {
                Log.e("SendRequest", String.valueOf(error));
            }
        };
    }

    // Hide TextViews related to order info (orderID and amount)
    private void ChangeOrderTextViewVisibility(int visibility)
    {
        TextView textView = (TextView) findViewById(R.id.orderIDTextView);
        textView.setVisibility(visibility);
        textView = (TextView) findViewById(R.id.orderIDValueTextView);
        textView.setVisibility(visibility);
        textView = (TextView) findViewById(R.id.thankOrderTextView);
        textView.setVisibility(visibility);
        ListView listView = (ListView) findViewById(R.id.listView);
        listView.setVisibility(visibility);
    }

    public void showOrder()
    {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    ChangeOrderTextViewVisibility(View.VISIBLE);

                    TextView orderIdTextView = (TextView) findViewById(R.id.orderIDValueTextView);
                    orderIdTextView.setText(Integer.toString(currentOrderNumber));

//                    TextView amountIdTextView = (TextView) findViewById(R.id.amountValueTextView);
//                    amountIdTextView.setText(orderResponse.get("totalPrice").toString()+"€");

                    //MAKE SHIT DYNAMIC (Adapters)
                    JSONArray items = orderReceived.getJSONObject("data").getJSONArray("items");
                    ArrayList<ReceiptItem> list = new ArrayList<>();
                    for (int i = 0; i < items.length(); i++) {
                        JSONObject JSONItem = items.getJSONObject(i);
                        String itemId = JSONItem.getString("itemId");
                        int amount = JSONItem.getInt("quantity");
                        ItemModel itemModel = GetItemByID(itemId);

//                        if (itemModel != null) {
                            double unitPrice = Double.parseDouble(itemModel.getPrice());
                            String name = itemModel.getName();

                            ReceiptItem receiptItem = new ReceiptItem(name, amount, unitPrice);
                            list.add(receiptItem);
//                        }
//                        else {
//                            Log.e("", "NULL");
//                        }
                    }
                    list.add(new ReceiptItem("Total", -1, orderResponse.getDouble("totalPrice")));

                    ReceiptItemListAdapter adapter = new ReceiptItemListAdapter(ACME_Cafe_Terminal.getAppContext(), R.layout.adapter_view_layout, list);
                    ListView listView = (ListView) findViewById(R.id.listView);
                    listView.setAdapter(adapter);

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        });

    }

    /* The NFC messages are received in their own activities and sent to the MainActivity */
    @Override
    public void onResume() {
        super.onResume();
        int type = getIntent().getIntExtra("type", 0);        // type of NFC message (key(1) or order(2))

        if (type == 2) {
            String message = new String(getIntent().getByteArrayExtra("order"));// get the NFC message (order)
            try {
                JSONObject orderJson = new JSONObject(message);
                Log.e("NFC", orderJson.toString());
                ProcessIncomingOrder(orderJson);
            } catch (JSONException e) {
                e.printStackTrace();
                Log.e("NFC", "Received an invalid JSON message");
            }
        }
        else {
            Log.e("NFC", "Received an NFC message type other than 2 (order)");
        }

    }

    private ItemModel GetItemByID(String id) {
        for (int i = 0; i < storeItems.length; i++) {
            if (storeItems[i].getId().equals(id)) {
                return storeItems[i];
            }
        }

        return null;
    }

}

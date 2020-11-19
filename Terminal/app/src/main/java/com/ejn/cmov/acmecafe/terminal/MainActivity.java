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
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;


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

        ChangeOrderUIVisibility(false);

        executor = new ThreadExecutor();
        repository = RemoteDataRepository.getInstance(executor, "192.168.1.88", 8080);

        orderNumbers = new ArrayList<>();
        currentOrderNumber = 0;

        button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Simulate an NFC order
                try {
                    String orderMsg = "{ \"data\": { \"items\": [ { \"itemId\": \"e43d8eb0-1df3-11eb-8bf4-0346e6a5d6a0\", \"quantity\": \"1\" }, { \"itemId\": \"c2fbeb40-1e87-11eb-930b-6bf5be424b1f\", \"quantity\": \"1\" } ], \"vouchers\": [], \"customerId\": \"f08aa2d0-24ff-11eb-9e41-995bcb66b787\", \"timestamp\": \"1605378731\" }, \"signature\": \"68ffc857ba50b3ad06c5a90be13edd28e711ca04db3b2a1a7fa11b9ea351249352e5ffc3ad1f11eb3130b1654c3df210897eee2e404abe8c46dab9c9650ac4e7\" }";
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
        boolean debug = false;

        if (debug) {

            try {
                String orderResponseMessage = "{ \"orderId\": \"45dd5ac0-294c-11eb-bcb7-2109dc46080a\", \"totalPrice\": 3.5, \"vouchers\": [], \"items\": [ { \"_id\": \"5fb491c1658f0e33b0264498\", \"itemId\": { \"_id\": \"e43d8eb0-1df3-11eb-8bf4-0346e6a5d6a0\", \"name\": \"Coffee\", \"quantity\": 198, \"price\": 1.5, \"icon\": \"random/icon\", \"updatedAt\": \"2020-11-18T03:15:13.144Z\" }, \"quantity\": 1, \"price\": 1.5 }, { \"_id\": \"5fb491c1658f0e33b0264499\", \"itemId\": { \"_id\": \"c2fbeb40-1e87-11eb-930b-6bf5be424b1f\", \"name\": \"Red Bull\", \"quantity\": 398, \"price\": 2, \"icon\": \"random/icon\", \"updatedAt\": \"2020-11-18T03:15:13.146Z\" }, \"quantity\": 1, \"price\": 2 } ] }";
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

    // Hides or shows TextViews related to order info (orderID and amount)
    private void ChangeOrderUIVisibility(final boolean visibility)
    {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                int vis = visibility ? View.VISIBLE : View.INVISIBLE;
                int opostiveVis = visibility ?  View.INVISIBLE : View.VISIBLE;

                TextView textView = (TextView) findViewById(R.id.orderIDTextView);
                textView.setVisibility(vis);
                textView = (TextView) findViewById(R.id.waitingOrderTextView);
                textView.setVisibility(opostiveVis);
                textView = (TextView) findViewById(R.id.orderIDValueTextView);
                textView.setVisibility(vis);
                textView = (TextView) findViewById(R.id.thankOrderTextView);
                textView.setVisibility(vis);
                ListView listView = (ListView) findViewById(R.id.listView);
                listView.setVisibility(vis);

            }
        });
    }

    public void showOrder()
    {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    TextView orderIdTextView = (TextView) findViewById(R.id.orderIDValueTextView);
                    orderIdTextView.setText(Integer.toString(currentOrderNumber));

                    //MAKE SHIT DYNAMIC (Adapters)
                    JSONArray items = orderResponse.getJSONArray("items");
                    ArrayList<ReceiptItem> list = new ArrayList<>();
                    for (int i = 0; i < items.length(); i++) {
                        JSONObject JSONItem = items.getJSONObject(i);

                        String name = JSONItem.getJSONObject("itemId").getString("name");
                        int amount = JSONItem.getInt("quantity");
                        double unitPrice = JSONItem.getDouble("price");

                        ReceiptItem receiptItem = new ReceiptItem(name, amount, unitPrice);
                        list.add(receiptItem);
                    }
//                    list.add(new ReceiptItem("Total", -1, orderResponse.getDouble("totalPrice")));

                    final ReceiptItemListAdapter adapter = new ReceiptItemListAdapter(ACME_Cafe_Terminal.getAppContext(), R.layout.adapter_view_layout, list);
                    ListView listView = (ListView) findViewById(R.id.listView);
                    listView.setAdapter(adapter);

                    ChangeOrderUIVisibility(true);


                    Calendar calendar = Calendar.getInstance(); // gets a calendar using the default time zone and locale.
                    calendar.add(Calendar.SECOND, 5);
                    Timer timer = new Timer(); // creating timer
                    TimerTask task = new TimerTask() {
                        @Override
                        public void run() {
                            ChangeOrderUIVisibility(false);
                        }
                    }; // creating timer task
                    timer.schedule(task, calendar.getTime()); // scheduling the task

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
}

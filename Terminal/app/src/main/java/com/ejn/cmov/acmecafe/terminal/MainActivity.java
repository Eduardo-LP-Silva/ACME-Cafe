package com.ejn.cmov.acmecafe.terminal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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

    ThreadExecutor executor;
    RemoteDataRepository repository;

    JSONObject orderReceived;
    // { orderId: newOrder._id, totalPrice: newOrder.totalPrice, vouchers: newOrder.vouchers }
    JSONObject orderResponse;
    JSONArray orderVouchers;


    // Holds the (orderId,orderNumber) relationship
    ArrayList<Pair<String, Integer>> orderNumbers;
    // Tracks the current order number
    int currentOrderNumber = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ChangeOrderUIVisibility(false);

        executor = new ThreadExecutor();
        repository = RemoteDataRepository.getInstance(executor, "192.168.1.88", 8080);

        orderNumbers = new ArrayList<>();
//        currentOrderNumber = 0;

        final Button button = (Button) findViewById(R.id.button);
//        button.setVisibility(View.INVISIBLE);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Simulate an NFC order
                try {
//                    String orderMsg = "{ \"data\": { \"items\": [ { \"itemId\": \"e43d8eb0-1df3-11eb-8bf4-0346e6a5d6a0\", \"quantity\": \"1\" }, { \"itemId\": \"c2fbeb40-1e87-11eb-930b-6bf5be424b1f\", \"quantity\": \"1\" } ], \"vouchers\": [], \"customerId\": \"f08aa2d0-24ff-11eb-9e41-995bcb66b787\", \"timestamp\": \"1605378731\" }, \"signature\": \"68ffc857ba50b3ad06c5a90be13edd28e711ca04db3b2a1a7fa11b9ea351249352e5ffc3ad1f11eb3130b1654c3df210897eee2e404abe8c46dab9c9650ac4e7\" }"; // Error
//                    String orderMsg = "{ \"data\": { \"items\": [ { \"itemId\": \"c2fbeb40-1e87-11eb-930b-6bf5be424b1f\", \"quantity\": \"1\" } ], \"vouchers\": [\"e4c1dfc0-268c-11eb-908b-69792046c821\"], \"customerId\": \"f08aa2d0-24ff-11eb-9e41-995bcb66b787\", \"timestamp\": \"1605206410\" }, \"signature\": \"48ce88884fce7bf2e883d153f62a2940319992af877c9bd0b730f978ec952ff9ca2cb23d04fabcfb20c1091a3c7032bee2820d446b047d55094acd9277b38da0\" }";
                    String orderMsg = "{\"data\":{\"items\":[{\"itemId\":\"e43d8eb0-1df3-11eb-8bf4-0346e6a5d6a0\",\"quantity\":\"1\"},{\"itemId\":\"11ead340-1df4-11eb-8bf4-0346e6a5d6a0\",\"quantity\":\"2\"},{\"itemId\":\"c2fbeb40-1e87-11eb-930b-6bf5be424b1f\",\"quantity\":\"1\"}],\"vouchers\":[{\"id\":\"e4c1dfc0-268c-11eb-908b-69792046c821\",\"type\":0},{\"id\":\"e8bed6f0-268c-11eb-908b-69792046c821\",\"type\":1}],\"customerId\":\"f08aa2d0-24ff-11eb-9e41-995bcb66b787\",\"timestamp\":\"1606019479\"},\"signature\":\"d059898765f50f760664411acbc30af5f43b9e6d7aef28631b5dbbda247f70f06be2c3c2862b4229ad7d67306916e234e64e64ed55432aee1e4c2180287c6e0a\"}";
                    JSONObject orderJson = new JSONObject(orderMsg);
//                    button.setVisibility(View.INVISIBLE);
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

        try {
            final JSONArray vouchers = order.getJSONObject("data").getJSONArray("vouchers");
//            final JSONObject cleanOrder = CleanVoucherJSON(order);

            if (debug) {

                try {
                    String orderResponseMessage = "{ \"orderId\": \"45dd5ac0-294c-11eb-bcb7-2109dc46080a\", \"totalPrice\": 3.5, \"vouchers\": [], \"items\": [ { \"_id\": \"5fb491c1658f0e33b0264498\", \"itemId\": { \"_id\": \"e43d8eb0-1df3-11eb-8bf4-0346e6a5d6a0\", \"name\": \"Coffee\", \"quantity\": 198, \"price\": 1.5, \"icon\": \"random/icon\", \"updatedAt\": \"2020-11-18T03:15:13.144Z\" }, \"quantity\": 1, \"price\": 1.5 }, { \"_id\": \"5fb491c1658f0e33b0264499\", \"itemId\": { \"_id\": \"c2fbeb40-1e87-11eb-930b-6bf5be424b1f\", \"name\": \"Red Bull\", \"quantity\": 398, \"price\": 2, \"icon\": \"random/icon\", \"updatedAt\": \"2020-11-18T03:15:13.146Z\" }, \"quantity\": 1, \"price\": 2 } ] }";
                    JSONObject orderResponseJson = new JSONObject(orderResponseMessage);
                    Log.e("test", orderResponseJson.toString());

                    if (GenerateOrderNumber(orderResponseJson)) {
                        orderReceived = order;
                        orderResponse = orderResponseJson;
                        orderVouchers = vouchers;
                        showOrder();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                repository.createOrder(order, ProcessServerResponse(new Callback<JSONObject>() {
                    @Override
                    public void onComplete(Result<JSONObject> result) {
                        try {
                            JSONObject orderResponseJson = ((Result.Success<JSONObject>) result).getData();
                            Log.e("test", orderResponseJson.toString());

                            // Remove vouchers that were not received from the server
                            JSONArray returnedVouchers = orderResponseJson.getJSONArray("vouchers");
//                            for (int i = 0; i < vouchers.length(); i++) {
//                                int foundIndex = -1;
//                                String originalVoucher = vouchers.getJSONObject(i).getString("id");
//
//                                for (int j = 0; j < returnedVouchers.length(); j++) {
//                                    if (returnedVouchers.getString(i).equals(originalVoucher)) {
//                                        foundIndex = j;
//                                        break;
//                                    }
//                                }
//
//                                // Not found
//                                if (foundIndex == -1) {
//                                    vouchers.remove(i);
//                                    i--;
//                                }
//                            }

                            if (GenerateOrderNumber(orderResponseJson)) {
                                orderReceived = order;
                                orderResponse = orderResponseJson;
                                orderVouchers = returnedVouchers;
                                showOrder();
                            }

                         } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onComplete(Result.Error<String> error) {
                        try {
                            JSONObject errorJson = new JSONObject(error.getError());

                            Log.e("NFC Response", "Code: " + errorJson.get("errorCode"));
                            Log.e("NFC Response", "Message: " + errorJson.get("errorMsg"));

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    ChangeOrderUIVisibility(false);

                                    TextView textView = (TextView) findViewById(R.id.waitingOrderTextView);
                                    textView.setTextColor(getColor(R.color.colorAccent));
                                    textView.setText(R.string.reject_text);

                                    ImageView imageView = (ImageView) findViewById(R.id.rejectImageView);
                                    imageView.setVisibility(View.VISIBLE);

                                    ScheduleUIReset(false);
                                }
                            });

                        } catch (JSONException e) {
                            e.printStackTrace();

                        }
                    }
                }));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private JSONObject CleanVoucherJSON(JSONObject order)
    {
        try {
            JSONArray vouchers = order.getJSONObject("data").getJSONArray("vouchers");
            JSONArray newVouchers = new JSONArray();

            for (int i = 0 ; i < vouchers.length(); i++) {
                newVouchers.put(vouchers.getJSONObject(i).getString("id"));
            }

            order.getJSONObject("data").put("vouchers", newVouchers);

            return order;
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
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
                        Result<JSONObject> res = new Result.Success<>(json);
                        callback.onComplete(res);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Result<JSONObject> res = new Result.Error<>(null);
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
                int oppositeVis = visibility ?  View.INVISIBLE : View.VISIBLE;

                TextView textView = (TextView) findViewById(R.id.orderIDTextView);
                textView.setVisibility(vis);
                textView = (TextView) findViewById(R.id.waitingOrderTextView);
                textView.setVisibility(oppositeVis);
                textView.setText(R.string.waiting_order);
                textView.setTextColor(getResources().getColor(R.color.colorPrimaryDark, getTheme()));
                textView = (TextView) findViewById(R.id.orderIDValueTextView);
                textView.setVisibility(vis);
                textView = (TextView) findViewById(R.id.thankOrderTextView);
                textView.setVisibility(vis);
                ListView listView = (ListView) findViewById(R.id.listView);
                listView.setVisibility(vis);
                ImageView imageView = (ImageView) findViewById(R.id.rejectImageView);
                imageView.setVisibility(View.INVISIBLE);

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
                    orderIdTextView.setText(String.valueOf(currentOrderNumber));

                    // Check vouchers
                    int type0VoucherCount = 0;
                    boolean hasType1Voucher = false;

                    for (int i = 0; i < orderVouchers.length(); i++) {
                        if (orderVouchers.getJSONObject(i).getInt("type") == 0)
                            type0VoucherCount++;

                        if (orderVouchers.getJSONObject(i).getInt("type") == 1)
                            hasType1Voucher = true;
                    }


                    //MAKE SHIT DYNAMIC (Adapters)
                    JSONArray items = orderResponse.getJSONArray("items");
                    ArrayList<ReceiptItem> list = new ArrayList<>();
                    ReceiptItem item;

                    item = new ReceiptItem();
                    item.type = 2;
                    list.add(item);

                    for (int i = 0; i < items.length(); i++) {
                        JSONObject JSONItem = items.getJSONObject(i);

                        String name = JSONItem.getJSONObject("itemId").getString("name");
                        int amount = JSONItem.getInt("quantity");
                        double unitPrice = JSONItem.getDouble("price");

                        item = new ReceiptItem(name, amount, unitPrice);

                        if (name.equals("Coffee")) {
                            if (type0VoucherCount > 0) {
                                item.CalculateSubTotal();
                                item.previousPrice = item.subTotal;
                                item.subTotal = (item.amount - type0VoucherCount) * item.unitPrice;
                            }
                        }
                        else if (hasType1Voucher) {
                            item.CalculateSubTotal();
                            item.previousPrice = item.subTotal
                            item.unitPrice = item.unitPrice*0.95; // 5% discount
                            item.CalculateSubTotal();
                        }


                        list.add(item);
                    }
                    item = new ReceiptItem("Total", -1, orderResponse.getDouble("totalPrice"));
                    item.type = 1;
                    list.add(item);


                    final ReceiptItemListAdapter adapter = new ReceiptItemListAdapter(ACME_Cafe_Terminal.getAppContext(), R.layout.adapter_view_layout, list);
                    ListView listView = (ListView) findViewById(R.id.listView);
                    listView.setAdapter(adapter);

                    ChangeOrderUIVisibility(true);

                    ScheduleUIReset(false);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void ScheduleUIReset(final boolean visibility)
    {
        ScheduleUIReset(visibility, 5);
    }

    private void ScheduleUIReset(final boolean visibility, int delay)
    {
        Calendar calendar = Calendar.getInstance(); // gets a calendar using the default time zone and locale.
        calendar.add(Calendar.SECOND, delay);
        Timer timer = new Timer(); // creating timer
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                ChangeOrderUIVisibility(visibility);
            }
        }; // creating timer task
        timer.schedule(task, calendar.getTime()); // scheduling the task
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

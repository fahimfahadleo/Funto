package com.mumba.funto;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.mumba.funto.SimpleClasses.Functions;
import com.mumba.funto.SimpleClasses.Variables;
import com.razorpay.Checkout;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.mumba.funto.Main_Menu.MainMenuActivity.mainMenuActivity;

public class PricingActivity extends AppCompatActivity {

    ListView listView;
    static TextView textVeiw, coinamount;

    String TAG = "PricingActivity";
    MediaType JSON;
    ArrayList<String> titlelistwwww;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pricing);
        listView = findViewById(R.id.list);
        textVeiw = findViewById(R.id.textview);
        coinamount = findViewById(R.id.coinamount);


        JSON = MediaType.parse("application/json; charset=utf-8");

        titlelistwwww = new ArrayList<>();
        titlelistwwww.add("Coin Price List");
        titlelistwwww.add("Coin Withdraw Price List");
        titlelistwwww.add("Sticker Price List");

        showCoinBalance();

        TitleAdapter adapter = new TitleAdapter(this, titlelistwwww);
        listView.setAdapter(adapter);

    }

    public class TitleAdapter extends ArrayAdapter<String> {
        ArrayList<String> titlelist;

        public TitleAdapter(Context context, ArrayList<String> users) {
            super(context, 0, users);
            titlelist = users;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // Get the data item for this position
            String user = getItem(position);
            // Check if an existing view is being reused, otherwise inflate the view
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.singletitlebox, parent, false);
            }

            CardView maincardview = convertView.findViewById(R.id.cardview);
            TextView titleText = convertView.findViewById(R.id.titletext);

            maincardview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    switch (user) {
                        case "Coin Price List":
                            call_priceapi();
                            break;
                        case "Coin Withdraw Price List":
                            call_withdraw_price();
                            break;
                        case "Sticker Price List":
                            call_sticker_price_list();
                            break;
                    }


                }
            });
            titleText.setText(user);

            // Return the completed view to render on screen
            return convertView;
        }
    }


    void call_priceapi() {
        OkHttpClient okHttpClient = new OkHttpClient.Builder().build();
        Request request = new Request.Builder().get().url("https://www.funto.in/API/index.php?p=coinPrice").build();
        Functions.Show_loader(PricingActivity.this, false, false);
        okHttpClient.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String responsestr = response.body().string();
                ((Activity) PricingActivity.this).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Functions.cancel_loader();
                        try {
                            JSONObject object = new JSONObject(responsestr);
                            JSONArray array;
                            JSONObject object1;
                            Log.e(TAG + "10", object.toString());
                            try {
                                array = object.getJSONArray("msg");
                                showPriceListJsonArray(array);
                            } catch (JSONException e) {

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
            }
        });

    }

    void call_withdraw_price() {
        OkHttpClient okHttpClient = new OkHttpClient.Builder().build();
        //RequestBody requestBody = RequestBody.create(JSON,jsonObject.toString());
        Toast.makeText(PricingActivity.this, "Requesting to server.", Toast.LENGTH_SHORT).show();
        Request request = new Request.Builder().get().url("https://www.funto.in/API/index.php?p=coinWithdrawPrice").build();
        Functions.Show_loader(PricingActivity.this, false, false);
        okHttpClient.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String responsestr = response.body().string();
                Log.e("response", responsestr);
                ((Activity) PricingActivity.this).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //Toast.makeText(context, "Response Got from server.\n" + responsestr, Toast.LENGTH_LONG).show();
                        Functions.cancel_loader();
                        try {
                            JSONObject object = new JSONObject(responsestr);
                            JSONArray array;
                            JSONObject object1;
                            // Log.e("object",object.toString());
                            try {
                                array = object.getJSONArray("msg");
                                showWithdrawPriceList(array);
                            } catch (JSONException e) {
                                e.printStackTrace();

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
            }
        });
    }


    ArrayList<HashMap<String, String>> coinwithdrawprice;

    void showWithdrawPriceList(JSONArray jsonArray) {
        coinwithdrawprice = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {

            try {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                HashMap<String, String> tempmap = new HashMap<>();
                tempmap.put("coin", jsonObject.getString("coin"));
                tempmap.put("price", jsonObject.getString("price"));
                tempmap.put("deduct", jsonObject.getString("deduct"));
                coinwithdrawprice.add(tempmap);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        listView.setAdapter(null);
        CoinWithdrawPriceAdapter priceAdapter = new CoinWithdrawPriceAdapter(PricingActivity.this, coinwithdrawprice);
        listView.setAdapter(priceAdapter);

    }

    public class CoinWithdrawPriceAdapter extends ArrayAdapter<HashMap<String, String>> {
        ArrayList<HashMap<String, String>> coinprice;

        public CoinWithdrawPriceAdapter(Context context, ArrayList<HashMap<String, String>> users) {
            super(context, 0, users);
            coinprice = users;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // Get the data item for this position
            HashMap<String, String> user = getItem(position);
            // Check if an existing view is being reused, otherwise inflate the view
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.singletitlebox, parent, false);
            }
            CardView maincardview = convertView.findViewById(R.id.cardview);
            TextView titleText = convertView.findViewById(R.id.titletext);
            titleText.setText("Withdraw " + user.get("coin") + " Coins At " + user.get("price") + " Deduct Rate " + user.get("deduct") + ".");
            maincardview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    call_coin_withdraw(Variables.u_id, user.get("coin"), user.get("price"), user.get("deduct"));
                }
            });
            return convertView;
        }
    }


    void call_sticker_price_list() {
        OkHttpClient okHttpClient = new OkHttpClient.Builder().build();
        //RequestBody requestBody = RequestBody.create(JSON,jsonObject.toString());
        Toast.makeText(PricingActivity.this, "Requesting to server.", Toast.LENGTH_SHORT).show();
        Request request = new Request.Builder().get().url("https://www.funto.in/API/index.php?p=giftCoinPrice").build();
        Functions.Show_loader(PricingActivity.this, false, false);
        okHttpClient.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String responsestr = response.body().string();
                //Log.e("response", responsestr);
                ((Activity) PricingActivity.this).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // Toast.makeText(context, "Response Got from server.\n" + responsestr, Toast.LENGTH_LONG).show();
                        Functions.cancel_loader();
                        try {
                            JSONObject object = new JSONObject(responsestr);
                            JSONArray array;
                            JSONObject object1;
                            // Log.e("object",object.toString());
                            try {
                                array = object.getJSONArray("msg");


                            } catch (JSONException e) {
                                object1 = object.getJSONObject("msg");


                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
            }
        });
    }


    ArrayList<HashMap<String, String>> coinprice;

    void showPriceListJsonArray(JSONArray jsonArray) {
        coinprice = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {

            try {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                HashMap<String, String> tempmap = new HashMap<>();
                tempmap.put("coin", jsonObject.getString("coin"));
                tempmap.put("price", jsonObject.getString("price"));
                coinprice.add(tempmap);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        listView.setAdapter(null);
        CoinPriceAdapter priceAdapter = new CoinPriceAdapter(PricingActivity.this, coinprice);
        listView.setAdapter(priceAdapter);

    }

    public class CoinPriceAdapter extends ArrayAdapter<HashMap<String, String>> {
        ArrayList<HashMap<String, String>> coinprice;

        public CoinPriceAdapter(Context context, ArrayList<HashMap<String, String>> users) {
            super(context, 0, users);
            coinprice = users;
        }

        @Override
        public View getView(int position, View convertView, @NotNull ViewGroup parent) {
            // Get the data item for this position
            HashMap<String, String> user = getItem(position);
            // Check if an existing view is being reused, otherwise inflate the view
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.singletitlebox, parent, false);
            }
            CardView maincardview = convertView.findViewById(R.id.cardview);
            TextView titleText = convertView.findViewById(R.id.titletext);
            titleText.setText("Get " + user.get("coin") + " Coins At " + user.get("price") + ".");
            maincardview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startPayment(user.get("price"), "Get " + user.get("coin") + "Coins At " + user.get("price") + ".");
                }
            });
            return convertView;
        }
    }

    public static void call_buy_coin_list(String fb_id, String coin, String price) {


        Variables.coins = null;
        Variables.price = null;


        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        OkHttpClient okHttpClient = new OkHttpClient.Builder().build();

        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("uid", fb_id);
            jsonObject.put("coins", coin);
            jsonObject.put("price", price);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody requestBody = RequestBody.create(JSON, jsonObject.toString());
        Toast.makeText(mainMenuActivity, "Requesting to server.", Toast.LENGTH_SHORT).show();
        Request request = new Request.Builder().post(requestBody).url("https://funto.in/API/index.php?p=buyCoinAdd").build();
        Functions.Show_loader(mainMenuActivity, false, false);
        okHttpClient.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String responsestr = response.body().string();
                // Log.e("response", responsestr);
                ((Activity) mainMenuActivity).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //Toast.makeText(mainMenuActivity, "Response Got from server.\n" + responsestr, Toast.LENGTH_LONG).show();
                        Functions.cancel_loader();
//get response from server then add coin to profile
                        //Log.e("coin add response",responsestr);
                        showCoinBalance();

                    }
                });
            }

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
            }
        });
    }

    void call_coin_withdraw(String userid, String coin, String price, String deduct) {
        OkHttpClient okHttpClient = new OkHttpClient.Builder().build();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("uid", userid);
            jsonObject.put("coins", coin);
            jsonObject.put("deduct", deduct);
            jsonObject.put("price", price);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody requestBody = RequestBody.create(JSON, jsonObject.toString());
        // Log.e("request body",jsonObject.toString());
        //Toast.makeText(context, "Requesting to server.", Toast.LENGTH_SHORT).show();
        Request request = new Request.Builder().post(requestBody).url("https://funto.in/API/index.php?p=withdrawCoinAdd").build();
        Functions.Show_loader(PricingActivity.this, false, false);
        okHttpClient.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String responsestr = response.body().string();
                Log.e(TAG + "11", responsestr);
                ((Activity) PricingActivity.this).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(PricingActivity.this, "Response Got from server.\n" + responsestr, Toast.LENGTH_LONG).show();
                        Functions.cancel_loader();


                    }
                });
            }

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
            }
        });
    }


    public void startPayment(String price, String description) {

        Checkout checkout = new Checkout();
        checkout.setKeyID(Variables.paymentkeyid);
        final Activity activity = ((Activity) PricingActivity.this);
        try {
            JSONObject options = new JSONObject();

            options.put("name", "Funto");
            options.put("description", description);
            options.put("image", "https://www.funto.in/public/assets/images/funto-logo.png");
            options.put("currency", "INR");

            Double priceint = Double.parseDouble(price) * 100;
            options.put("amount", String.valueOf(priceint));
            options.put("prefill.name", Variables.user_name);
            options.put("prefill.email", Variables.user_email);

            checkout.open(activity, options);
        } catch (Exception e) {
            Log.e(TAG + "13", "Error in starting Razorpay Checkout", e);
            Toast.makeText(mainMenuActivity, "Error In Payment!", Toast.LENGTH_SHORT).show();
        }
    }

    public static void addCoinFromVideoWatch(String video_id) {
        if (!Variables.videoid.contains(video_id)) {
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            OkHttpClient okHttpClient = new OkHttpClient.Builder().build();
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("uid", Variables.user_id);
                jsonObject.put("coins", "1");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            RequestBody requestBody = RequestBody.create(JSON, jsonObject.toString());
            Request request = new Request.Builder().post(requestBody).url("https://funto.in/API/index.php?p=videoCoinAdd").build();
            okHttpClient.newCall(request).enqueue(new okhttp3.Callback() {
                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    String responsestr = response.body().string();
                }

                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                }
            });
        }
        Variables.videoid.add(video_id);
    }

    public static void showCoinBalance() {
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        OkHttpClient okHttpClient = new OkHttpClient.Builder().build();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("uid", Variables.user_id);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody requestBody = RequestBody.create(JSON, jsonObject.toString());
        //  Log.e("request body",jsonObject.toString());
        // Toast.makeText(mainMenuActivity, "Requesting to server.", Toast.LENGTH_SHORT).show();
        Request request = new Request.Builder().post(requestBody).url("https://funto.in/API/index.php?p=coinBalance").build();
        okHttpClient.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String responsestr = response.body().string();
                // Log.e("response", responsestr);
                ((Activity) mainMenuActivity).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // Toast.makeText(mainMenuActivity, "Response Got from server.\n" + responsestr, Toast.LENGTH_LONG).show();
                        Log.e("Home14", responsestr);
                        JSONObject jsonObject1;
                        JSONArray array;
                        try {
                            jsonObject1 = new JSONObject(responsestr);
                            array = jsonObject1.getJSONArray("msg");
                            String coins = array.getJSONObject(0).getString("totalCoin");
                            Coinamount = coins;
                            UpdateCoin();


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
            }
        });
    }

    static String Coinamount = null;


    static void UpdateCoin() {
        coinamount.setText("You have " + Coinamount + " coins.");
    }

    @Override
    public void onBackPressed() {

        TitleAdapter adapter = new TitleAdapter(this, titlelistwwww);
        if (listView.getAdapter() instanceof CoinPriceAdapter) {
            listView.setAdapter(adapter);
        } else if (listView.getAdapter() instanceof CoinWithdrawPriceAdapter) {
            listView.setAdapter(adapter);
        } else {
            super.onBackPressed();
        }


    }
}
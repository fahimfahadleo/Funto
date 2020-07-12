package com.mumba.funto.Comments;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.mumba.funto.Main_Menu.RelateToFragment_OnBack.RootFragment;
import com.mumba.funto.R;
import com.mumba.funto.SimpleClasses.API_CallBack;
import com.mumba.funto.SimpleClasses.ApiRequest;
import com.mumba.funto.SimpleClasses.Fragment_Data_Send;
import com.mumba.funto.SimpleClasses.Functions;
import com.mumba.funto.SimpleClasses.Variables;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.mumba.funto.Home.Home_F.UpdateCoin;
import static com.mumba.funto.Home.Home_F.showCoinBalance;
import static com.mumba.funto.Main_Menu.MainMenuActivity.mainMenuActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class Comment_F extends RootFragment {

    final String TAG = "Comment_F";
    View view;
    Context context;

    RecyclerView recyclerView;

    Comments_Adapter adapter;

    ArrayList<Comment_Get_Set> data_list;

    String video_id;
    String user_id;

    EditText message_edit;
    public static ImageButton send_btn, sticker_comment;
    ProgressBar send_progress;

    TextView comment_count_txt;

    FrameLayout comment_screen;
    AlertDialog.Builder builder;
    AlertDialog alertDialog;
    View dialogView;

    public static int comment_count = 0;

    public Comment_F() {

    }

    Fragment_Data_Send fragment_data_send;

    @SuppressLint("ValidFragment")
    public Comment_F(int count, Fragment_Data_Send fragment_data_send) {
        comment_count = count;
        this.fragment_data_send = fragment_data_send;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_comment, container, false);
        context = getContext();

        builder = new AlertDialog.Builder(context);
        sticker_comment = view.findViewById(R.id.stickercomment);
        comment_count_txt = view.findViewById(R.id.comment_count);
        send_btn = view.findViewById(R.id.send_btn);

        comment_screen = view.findViewById(R.id.comment_screen);
        comment_screen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getActivity().onBackPressed();

            }
        });

        view.findViewById(R.id.Goback).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getActivity().onBackPressed();
            }
        });

        sticker_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialogView = getLayoutInflater().inflate(R.layout.showstickers, null);


                ImageView like, kiss, line, celibrate, heart, love;


                like = dialogView.findViewById(R.id.likeemoji);
                kiss = dialogView.findViewById(R.id.kissemoji);
                line = dialogView.findViewById(R.id.lineemoji);
                celibrate = dialogView.findViewById(R.id.celibrateemoji);
                heart = dialogView.findViewById(R.id.heartemoji);
                love = dialogView.findViewById(R.id.loveemoji);


                like.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {


                        if (Variables.sharedPreferences.getBoolean(Variables.islogin, false)) {
                            Send_Comments(video_id, "d96edbaa3a8f7533763d64722072780d");
                            message_edit.setText(null);
                            send_progress.setVisibility(View.VISIBLE);
                            send_btn.setVisibility(View.GONE);
                            sticker_comment.setVisibility(View.GONE);
                            alertDialog.dismiss();

                            giftCoins("50");
                        } else {
                            Toast.makeText(context, "Please Login into the app", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                kiss.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (Variables.sharedPreferences.getBoolean(Variables.islogin, false)) {
                            Send_Comments(video_id, "571f986a41ac9038929c209cf8ee9fb7");
                            message_edit.setText(null);
                            send_progress.setVisibility(View.VISIBLE);
                            send_btn.setVisibility(View.GONE);
                            sticker_comment.setVisibility(View.GONE);
                            alertDialog.dismiss();
                            giftCoins("100");
                        } else {
                            Toast.makeText(context, "Please Login into the app", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                line.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (Variables.sharedPreferences.getBoolean(Variables.islogin, false)) {
                            Send_Comments(video_id, "18753f6e0c8e21ea0b76617eb316eac3");
                            message_edit.setText(null);
                            send_progress.setVisibility(View.VISIBLE);
                            send_btn.setVisibility(View.GONE);
                            sticker_comment.setVisibility(View.GONE);
                            alertDialog.dismiss();
                            giftCoins("60");
                        } else {
                            Toast.makeText(context, "Please Login into the app", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                celibrate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (Variables.sharedPreferences.getBoolean(Variables.islogin, false)) {
                            Send_Comments(video_id, "5e711433876950000c41b5f89a5d910f");
                            message_edit.setText(null);
                            send_progress.setVisibility(View.VISIBLE);
                            send_btn.setVisibility(View.GONE);
                            sticker_comment.setVisibility(View.GONE);
                            alertDialog.dismiss();
                            giftCoins("70");
                        } else {
                            Toast.makeText(context, "Please Login into the app", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                heart.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (Variables.sharedPreferences.getBoolean(Variables.islogin, false)) {
                            Send_Comments(video_id, "eb144d0ffe2a27735d886df0ec84243a");
                            message_edit.setText(null);
                            send_progress.setVisibility(View.VISIBLE);
                            send_btn.setVisibility(View.GONE);
                            sticker_comment.setVisibility(View.GONE);
                            alertDialog.dismiss();
                            giftCoins("80");
                        } else {
                            Toast.makeText(context, "Please Login into the app", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                love.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (Variables.sharedPreferences.getBoolean(Variables.islogin, false)) {
                            Send_Comments(video_id, "081e528ed37ac4d406f5b67f9b5a47f9");
                            message_edit.setText(null);
                            send_progress.setVisibility(View.VISIBLE);
                            send_btn.setVisibility(View.GONE);
                            sticker_comment.setVisibility(View.GONE);
                            giftCoins("90");
                            alertDialog.dismiss();
                        } else {
                            Toast.makeText(context, "Please Login into the app", Toast.LENGTH_SHORT).show();
                        }
                    }
                });


                builder.setView(null);
                builder.setView(dialogView);
                alertDialog = builder.create();
                alertDialog.setCanceledOnTouchOutside(true);
                alertDialog.show();

            }
        });


        Bundle bundle = getArguments();
        if (bundle != null) {
            video_id = bundle.getString("video_id");
            user_id = bundle.getString("user_id");


            if(bundle.containsKey("type")){
                Log.e(TAG+"1","exists");
                sticker_comment.callOnClick();
            }
        }




        recyclerView = view.findViewById(R.id.recylerview);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(false);


        data_list = new ArrayList<>();
        adapter = new Comments_Adapter(context, data_list, new Comments_Adapter.OnItemClickListener() {
            @Override
            public void onItemClick(int postion, Comment_Get_Set item, View view) {


            }
        });

        recyclerView.setAdapter(adapter);


        message_edit = view.findViewById(R.id.message_edit);


        send_progress = view.findViewById(R.id.send_progress);

        send_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String message = message_edit.getText().toString();
                if (!TextUtils.isEmpty(message)) {
                    if (Variables.sharedPreferences.getBoolean(Variables.islogin, false)) {
                        Send_Comments(video_id, message);
                        message_edit.setText(null);
                        send_progress.setVisibility(View.VISIBLE);
                        send_btn.setVisibility(View.GONE);
                    } else {
                        Toast.makeText(context, "Please Login into the app", Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });

        Get_All_Comments();


        return view;
    }


    @Override
    public void onDetach() {
        Functions.hideSoftKeyboard(getActivity());

        super.onDetach();
    }

    // this funtion will get all the comments against post
    public void Get_All_Comments() {

        Functions.Call_Api_For_get_Comment(getActivity(), video_id, new API_CallBack() {
            @Override
            public void ArrayData(ArrayList arrayList) {
                ArrayList<Comment_Get_Set> arrayList1 = arrayList;
                for (Comment_Get_Set item : arrayList1) {
                    data_list.add(item);
                }
                comment_count_txt.setText(data_list.size() + " comments");
                adapter.notifyDataSetChanged();
            }

            @Override
            public void OnSuccess(String responce) {

            }

            @Override
            public void OnFail(String responce) {

            }

        });

    }


    // this function will call an api to upload your comment
    public void Send_Comments(String video_id, final String comment) {

        Functions.Call_Api_For_Send_Comment(getActivity(), video_id, comment, new API_CallBack() {
            @Override
            public void ArrayData(ArrayList arrayList) {
                send_progress.setVisibility(View.GONE);
                send_btn.setVisibility(View.VISIBLE);
                sticker_comment.setVisibility(View.VISIBLE);

                ArrayList<Comment_Get_Set> arrayList1 = arrayList;
                for (Comment_Get_Set item : arrayList1) {
                    data_list.add(0, item);
                    comment_count++;

                    SendPushNotification(getActivity(), user_id, comment);

                    comment_count_txt.setText(comment_count + " comments");

                    if (fragment_data_send != null)
                        fragment_data_send.onDataSent("" + comment_count);

                }
                adapter.notifyDataSetChanged();

            }

            @Override
            public void OnSuccess(String responce) {

            }

            @Override
            public void OnFail(String responce) {

            }
        });

    }


    public void SendPushNotification(Activity activity, String user_id, String comment) {

        JSONObject notimap = new JSONObject();
        try {
            notimap.put("title", Variables.sharedPreferences.getString(Variables.u_name, "") + " Comment on your video");
            notimap.put("message", comment);
            notimap.put("icon", Variables.sharedPreferences.getString(Variables.u_pic, ""));
            notimap.put("senderid", Variables.sharedPreferences.getString(Variables.u_id, ""));
            notimap.put("receiverid", user_id);
            notimap.put("action_type", "comment");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ApiRequest.Call_Api(context, Variables.sendPushNotification, notimap, null);

    }

    void giftCoins(String coins){
        String receiverid = user_id;
        String myid = Variables.user_id;

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        OkHttpClient okHttpClient = new OkHttpClient.Builder().build();

        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("uid",receiverid);
            jsonObject.put("coins",coins);
            jsonObject.put("gifted_uid",user_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.e(TAG+"2",jsonObject.toString());
        RequestBody requestBody = RequestBody.create(JSON,jsonObject.toString());
        Toast.makeText(mainMenuActivity, "Requesting to server.", Toast.LENGTH_SHORT).show();
        Request request = new Request.Builder().post(requestBody).url("https://funto.in/API/index.php?p=giftCoinAdd").build();
        okHttpClient.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String responsestr = response.body().string();
                Log.e(TAG+"3", responsestr);
                ((Activity) mainMenuActivity).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(mainMenuActivity, "Response Got from server.\n" + responsestr, Toast.LENGTH_LONG).show();

                        Log.e(TAG+"4",responsestr);

                       showCoinBalance();
                    }
                });
            }
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {}
        });


    }



}

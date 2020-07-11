package com.mumba.funto.Home;


import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.daasuu.gpuv.composer.GPUMp4Composer;
import com.daasuu.gpuv.egl.filter.GlWatermarkFilter;
import com.downloader.Error;
import com.downloader.OnCancelListener;
import com.downloader.OnDownloadListener;
import com.downloader.OnPauseListener;
import com.downloader.OnProgressListener;
import com.downloader.OnStartOrResumeListener;
import com.downloader.PRDownloader;
import com.downloader.Progress;
import com.downloader.request.DownloadRequest;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;

import com.mumba.funto.Accounts.Login_A;
import com.mumba.funto.Comments.Comment_F;
import com.mumba.funto.Main_Menu.MainMenuActivity;
import com.mumba.funto.Main_Menu.MainMenuFragment;
import com.mumba.funto.Main_Menu.RelateToFragment_OnBack.RootFragment;
import com.mumba.funto.Profile.Profile_F;
import com.mumba.funto.R;
import com.mumba.funto.SimpleClasses.API_CallBack;
import com.mumba.funto.SimpleClasses.ApiRequest;
import com.mumba.funto.SimpleClasses.Callback;
import com.mumba.funto.SimpleClasses.Fragment_Callback;
import com.mumba.funto.SimpleClasses.Fragment_Data_Send;
import com.mumba.funto.SimpleClasses.Functions;
import com.mumba.funto.SimpleClasses.Variables;
import com.mumba.funto.SoundLists.VideoSound_A;
import com.mumba.funto.Taged.Taged_Videos_F;
import com.mumba.funto.VideoAction.VideoAction_F;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;
import com.volokh.danylo.hashtaghelper.HashTagHelper;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.facebook.FacebookSdk.getAdvertiserIDCollectionEnabled;
import static com.facebook.FacebookSdk.getApplicationContext;
import static com.mumba.funto.Main_Menu.MainMenuActivity.mainMenuActivity;


/**
 * A simple {@link Fragment} subclass.
 */

// this is the main view which is show all  the video in list
public class Home_F extends RootFragment implements Player.EventListener, Fragment_Data_Send {

    View view;
    Context context;
    static TextView coins;
    AlertDialog.Builder builder;
    AlertDialog alertDialog;

    MediaType JSON;
    View dialogView;


    RecyclerView recyclerView;
    ArrayList<Home_Get_Set> data_list;
    int currentPage=-1;
    LinearLayoutManager layoutManager;

    ProgressBar p_bar;

    SwipeRefreshLayout swiperefresh;


    double temptime = 0.0;

    boolean is_user_stop_video=false;
    public Home_F() {
        // Required empty public constructor
    }

    int swipe_count=0;



    public static void UpdateCoin(String coin){
        if (Variables.sharedPreferences.getBoolean(Variables.islogin, false)) {
            coins.setVisibility(View.VISIBLE);
            coins.setText("coins: "+coin);
        }else {
            coins.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_home, container, false);
        context=getContext();


        JSON = MediaType.parse("application/json; charset=utf-8");

        p_bar=view.findViewById(R.id.p_bar);

        recyclerView=view.findViewById(R.id.recylerview);
        layoutManager=new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(false);

        SnapHelper snapHelper =  new PagerSnapHelper();
         snapHelper.attachToRecyclerView(recyclerView);


        coins = view.findViewById(R.id.coin);



        if (Variables.sharedPreferences.getBoolean(Variables.islogin, false)) {

            showCoinBalance();

            coins.setVisibility(View.VISIBLE);
        }else {
            coins.setVisibility(View.INVISIBLE);
        }


        coins.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                builder = new AlertDialog.Builder(context);

                dialogView = getLayoutInflater().inflate(R.layout.showcoinbox, null);
                TextView pricelist, coinwithdrawpricelist, stickerpricelist;
                pricelist = dialogView.findViewById(R.id.coinpricelist);
                coinwithdrawpricelist = dialogView.findViewById(R.id.coinwithdrawpricelist);
                stickerpricelist = dialogView.findViewById(R.id.stickerpricelist);
                
                pricelist.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        call_priceapi();
                    }
                });
                coinwithdrawpricelist.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        call_withdraw_price();
                    }
                });

                stickerpricelist.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        call_sticker_price_list();
                    }
                });






                builder.setView(null);
                builder.setView(dialogView);
                alertDialog = builder.create();
                alertDialog.setCanceledOnTouchOutside(true);
                alertDialog.show();
            }
        });



        // this is the scroll listener of recycler view which will tell the current item number
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

            }
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                //here we find the current item number
                final int scrollOffset = recyclerView.computeVerticalScrollOffset();
                final int height = recyclerView.getHeight();
                int page_no=scrollOffset / height;

                if(page_no!=currentPage ){
                    currentPage=page_no;

                    Release_Privious_Player();
                    Set_Player(currentPage);

                }
            }
        });



        swiperefresh=view.findViewById(R.id.swiperefresh);
        swiperefresh.setProgressViewOffset(false, 0, 200);

        swiperefresh.setColorSchemeResources(R.color.black);
        swiperefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                currentPage=-1;
                Call_Api_For_get_Allvideos();
            }
        });

        Call_Api_For_get_Allvideos();

        if(!Variables.is_remove_ads)
        Load_add();

        return view;
    }




    InterstitialAd mInterstitialAd;
    public void Load_add() {

        // this is test app id you will get the actual id when you add app in your
        //add mob account
        MobileAds.initialize(context,
                getResources().getString(R.string.ad_app_id));


        //code for intertial add
        mInterstitialAd = new InterstitialAd(context);

        //here we will get the add id keep in mind above id is app id and below Id is add Id
        mInterstitialAd.setAdUnitId(context.getResources().getString(R.string.my_Interstitial_Add));
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                mInterstitialAd.loadAd(new AdRequest.Builder().build());
            }
        });


    }



    boolean is_add_show=false;
    Home_Adapter adapter;
    public void Set_Adapter(){

         adapter=new Home_Adapter(context, data_list, new Home_Adapter.OnItemClickListener() {
            @Override
            public void onItemClick(int postion, final Home_Get_Set item, View view) {

                switch(view.getId()) {

                    case R.id.user_pic:
                        onPause();
                        OpenProfile(item,false);
                        break;

                    case R.id.username:
                        onPause();
                        OpenProfile(item,false);
                        break;

                    case R.id.like_layout:
                        if(Variables.sharedPreferences.getBoolean(Variables.islogin,false)) {
                        Like_Video(postion, item);
                        }else {
                            Toast.makeText(context, "Please Login.", Toast.LENGTH_SHORT).show();
                        }
                        break;

                    case R.id.comment_layout:
                        OpenComment(item);
                        break;

                    case R.id.shared_layout:
                        if (!is_add_show && (mInterstitialAd!=null && mInterstitialAd.isLoaded())) {
                            mInterstitialAd.show();
                            is_add_show = true;
                        } else {
                             is_add_show = false;
                                final VideoAction_F fragment = new VideoAction_F(item.video_id, new Fragment_Callback() {
                                    @Override
                                    public void Responce(Bundle bundle) {

                                        if (bundle.getString("action").equals("save")) {
                                            Save_Video(item);
                                        } else if (bundle.getString("action").equals("delete")) {
                                            Functions.Show_loader(context, false, false);
                                            Functions.Call_Api_For_Delete_Video(getActivity(), item.video_id, new API_CallBack() {
                                                @Override
                                                public void ArrayData(ArrayList arrayList) {

                                                }

                                                @Override
                                                public void OnSuccess(String responce) {
                                                    data_list.remove(currentPage);
                                                    adapter.notifyDataSetChanged();

                                                }

                                                @Override
                                                public void OnFail(String responce) {

                                                }
                                            });

                                        }
                                    }
                                });

                                Bundle bundle = new Bundle();
                                bundle.putString("video_id", item.video_id);
                                bundle.putString("user_id", item.fb_id);
                                fragment.setArguments(bundle);

                                fragment.show(getChildFragmentManager(), "");
                            }

                        break;


                    case R.id.sound_image_layout:
                        if(Variables.sharedPreferences.getBoolean(Variables.islogin,false)) {
                            if(check_permissions()) {
                                Intent intent = new Intent(getActivity(), VideoSound_A.class);
                                intent.putExtra("data", item);
                                startActivity(intent);
                            }
                        }else {
                            Toast.makeText(context, "Please Login.", Toast.LENGTH_SHORT).show();
                        }

                        break;
                }

            }
        });

        adapter.setHasStableIds(true);
        recyclerView.setAdapter(adapter);

    }



    // Bottom two function will call the api and get all the videos form api and parse the json data
    private void Call_Api_For_get_Allvideos() {


        Log.d(Variables.tag, MainMenuActivity.token);

        JSONObject parameters = new JSONObject();
        try {
            parameters.put("fb_id", Variables.sharedPreferences.getString(Variables.u_id,"0"));
            parameters.put("token",MainMenuActivity.token);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        ApiRequest.Call_Api(context, Variables.showAllVideos, parameters, new Callback() {
            @Override
            public void Responce(String resp) {
                swiperefresh.setRefreshing(false);
                Parse_data(resp);
            }
        });



    }

    public void Parse_data(String responce){

        data_list=new ArrayList<>();

        try {
            JSONObject jsonObject=new JSONObject(responce);
            String code=jsonObject.optString("code");
            if(code.equals("200")){
                JSONArray msgArray=jsonObject.getJSONArray("msg");
                for (int i=0;i<msgArray.length();i++) {
                    JSONObject itemdata = msgArray.optJSONObject(i);
                    Home_Get_Set item=new Home_Get_Set();
                    item.fb_id=itemdata.optString("fb_id");

                    JSONObject user_info=itemdata.optJSONObject("user_info");

                    item.username=user_info.optString("username");
                    item.first_name=user_info.optString("first_name",context.getResources().getString(R.string.app_name));
                    item.last_name=user_info.optString("last_name","User");
                    item.profile_pic=user_info.optString("profile_pic","null");

                    JSONObject sound_data=itemdata.optJSONObject("sound");
                    item.sound_id=sound_data.optString("id");
                    item.sound_name=sound_data.optString("sound_name");
                    item.sound_pic=sound_data.optString("thum");



                    JSONObject count=itemdata.optJSONObject("count");
                    item.like_count=count.optString("like_count");
                    item.video_comment_count=count.optString("video_comment_count");


                    item.video_id=itemdata.optString("id");
                    item.liked=itemdata.optString("liked");
                    item.video_url=itemdata.optString("video");
                    item.video_description=itemdata.optString("description");

                    item.thum=itemdata.optString("thum");
                    item.created_date=itemdata.optString("created");

                    data_list.add(item);
                }

                Set_Adapter();

            }else {
                Toast.makeText(context, ""+jsonObject.optString("msg"), Toast.LENGTH_SHORT).show();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }



    private void Call_Api_For_Singlevideos(final int postion) {


        JSONObject parameters = new JSONObject();
        try {
            parameters.put("fb_id", Variables.sharedPreferences.getString(Variables.u_id,"0"));
            parameters.put("token",Variables.sharedPreferences.getString(Variables.device_token,"Null"));
            parameters.put("video_id",data_list.get(postion).video_id);

        } catch (JSONException e) {
            e.printStackTrace();
        }


        ApiRequest.Call_Api(context, Variables.showAllVideos, parameters, new Callback() {
            @Override
            public void Responce(String resp) {
                swiperefresh.setRefreshing(false);
                Singal_Video_Parse_data(postion,resp);
            }
        });


    }

    public void Singal_Video_Parse_data(int pos,String responce){

        try {
            JSONObject jsonObject=new JSONObject(responce);
            String code=jsonObject.optString("code");
            if(code.equals("200")){
                JSONArray msgArray=jsonObject.getJSONArray("msg");
                for (int i=0;i<msgArray.length();i++) {
                    JSONObject itemdata = msgArray.optJSONObject(i);
                    Home_Get_Set item=new Home_Get_Set();
                    item.fb_id=itemdata.optString("fb_id");

                    JSONObject user_info=itemdata.optJSONObject("user_info");

                    item.username=user_info.optString("username");
                    item.first_name=user_info.optString("first_name",context.getResources().getString(R.string.app_name));
                    item.last_name=user_info.optString("last_name","User");
                    item.profile_pic=user_info.optString("profile_pic","null");

                    JSONObject sound_data=itemdata.optJSONObject("sound");
                    item.sound_id=sound_data.optString("id");
                    item.sound_name=sound_data.optString("sound_name");
                    item.sound_pic=sound_data.optString("thum");



                    JSONObject count=itemdata.optJSONObject("count");
                    item.like_count=count.optString("like_count");
                    item.video_comment_count=count.optString("video_comment_count");


                    item.video_id=itemdata.optString("id");
                    item.liked=itemdata.optString("liked");
                    item.video_url=itemdata.optString("video");
                    item.video_description=itemdata.optString("description");

                    item.thum=itemdata.optString("thum");
                    item.created_date=itemdata.optString("created");

                    data_list.remove(pos);
                    data_list.add(pos,item);
                    adapter.notifyDataSetChanged();
                }



            }else {
                Toast.makeText(context, ""+jsonObject.optString("msg"), Toast.LENGTH_SHORT).show();
            }

        } catch (JSONException e) {

            e.printStackTrace();
        }

    }


    Handler handler = new Handler();
    Runnable runnable;



    void showToast(){
        Toast.makeText(mainMenuActivity,"called",Toast.LENGTH_SHORT).show();
        Log.e("checking","chekcing");
    }

    // this will call when swipe for another video and
    // this function will set the player to the current video
    public void Set_Player(final int currentPage){

            final Home_Get_Set item= data_list.get(currentPage);
            DefaultTrackSelector trackSelector = new DefaultTrackSelector();
             final SimpleExoPlayer player = ExoPlayerFactory.newSimpleInstance(context, trackSelector);

            DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(context,
                    Util.getUserAgent(context, "TikTok"));

            MediaSource videoSource = new ExtractorMediaSource.Factory(dataSourceFactory)
                    .createMediaSource(Uri.parse(item.video_url));

            Log.d("resp",item.video_url);


             player.prepare(videoSource);

             player.setRepeatMode(Player.REPEAT_MODE_ALL);
             player.addListener(this);


         View layout=layoutManager.findViewByPosition(currentPage);
         final PlayerView playerView=layout.findViewById(R.id.playerview);
          playerView.setPlayer(player);


        player.setPlayWhenReady(is_visible_to_user);
        privious_player=player;

        handler = new Handler();

        player.addListener(new Player.DefaultEventListener() {
            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                if (playWhenReady && playbackState == Player.STATE_READY) {
                    // media actually playing
                    Log.e("duration",String.valueOf(player.getDuration()));
                    long interval = (long) (player.getDuration() *0.6);
                    Log.e("after",String.valueOf(interval));
                      runnable = new Runnable(){
                        public void run() {
                            Log.e("after","add coin");
                            //call add coin api

                            if (Variables.sharedPreferences.getBoolean(Variables.islogin, false)) {
                                addCoinFromVideoWatch(item.video_id);
                            }else {
                               Toast.makeText(mainMenuActivity,"You must login to gain coin by watching video.",Toast.LENGTH_SHORT).show();
                            }




                        }
                    };
                    Log.e("after",String.valueOf(System.currentTimeMillis()+interval));
                    Log.e("after",String.valueOf(System.currentTimeMillis()));
                    handler.postAtTime(runnable, System.currentTimeMillis()+interval);
                    handler.postDelayed(runnable, interval);
                } else if (playWhenReady) {
                    // might be idle (plays after prepare()),
                    // buffering (plays when data available)
                    // or ended (plays when seek away from end)
                } else {
                    // player paused in any state
                    handler.removeCallbacks(runnable);
                }
            }
        });



        final RelativeLayout mainlayout = layout.findViewById(R.id.mainlayout);
        playerView.setOnTouchListener(new View.OnTouchListener() {
            private GestureDetector gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {

                @Override
                public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                     super.onFling(e1, e2, velocityX, velocityY);
                    float deltaX = e1.getX() - e2.getX();
                    float deltaXAbs = Math.abs(deltaX);
                    // Only when swipe distance between minimal and maximal distance value then we treat it as effective swipe
                    if((deltaXAbs > 100) && (deltaXAbs < 1000)) {
                        if(deltaX > 0)
                        {
                            OpenProfile(item,true);
                        }
                    }


                    return true;
                }

                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    super.onSingleTapUp(e);
                    if(!player.getPlayWhenReady()){
                        is_user_stop_video=false;
                        privious_player.setPlayWhenReady(true);
                    }else{
                        is_user_stop_video=true;
                        privious_player.setPlayWhenReady(false);
                    }


                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    super.onLongPress(e);
                    Show_video_option(item);

                }

                @Override
                public boolean onDoubleTap(MotionEvent e) {

                    if(!player.getPlayWhenReady()){
                        is_user_stop_video=false;
                        privious_player.setPlayWhenReady(true);
                    }


                    if(Variables.sharedPreferences.getBoolean(Variables.islogin,false)) {
                        Show_heart_on_DoubleTap(item, mainlayout, e);
                        Like_Video(currentPage, item);
                    }else {
                        Toast.makeText(context, "Please Login into app", Toast.LENGTH_SHORT).show();
                    }
                    return super.onDoubleTap(e);

                }
            });

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                gestureDetector.onTouchEvent(event);
                return true;
            }
        });

        TextView desc_txt=layout.findViewById(R.id.desc_txt);
        HashTagHelper.Creator.create(context.getResources().getColor(R.color.maincolor), new HashTagHelper.OnHashTagClickListener() {
            @Override
            public void onHashTagClicked(String hashTag) {

                onPause();
                OpenHashtag(hashTag);

            }
        }).handle(desc_txt);



        LinearLayout soundimage = (LinearLayout)layout.findViewById(R.id.sound_image_layout);
        Animation sound_animation = AnimationUtils.loadAnimation(context,R.anim.d_clockwise_rotation);
        soundimage.startAnimation(sound_animation);

        if(Variables.sharedPreferences.getBoolean(Variables.islogin,false))
        Functions.Call_Api_For_update_view(getActivity(),item.video_id);


        swipe_count++;
        if(swipe_count>4){
            Show_add();
            swipe_count=0;
        }



        Call_Api_For_Singlevideos(currentPage);






    }


    public void Show_heart_on_DoubleTap(Home_Get_Set item,final RelativeLayout mainlayout,MotionEvent e){

        int x = (int) e.getX()-100;
        int y = (int) e.getY()-100;
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        final ImageView iv = new ImageView(getApplicationContext());
        lp.setMargins(x, y, 0, 0);
        iv.setLayoutParams(lp);
        if(item.liked.equals("1"))
        iv.setImageDrawable(getResources().getDrawable(
                R.drawable.ic_like));
        else
            iv.setImageDrawable(getResources().getDrawable(
                    R.drawable.ic_like_fill));

        mainlayout.addView(iv);
        Animation fadeoutani = AnimationUtils.loadAnimation(context,R.anim.fade_out);

        fadeoutani.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mainlayout.removeView(iv);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        iv.startAnimation(fadeoutani);

    }



    public void Show_add(){
        if(mInterstitialAd!=null && mInterstitialAd.isLoaded()){
            mInterstitialAd.show();
        }
    }


    @Override
    public void onDataSent(String yourData) {
        int comment_count =Integer.parseInt(yourData);
        Home_Get_Set item=data_list.get(currentPage);
        item.video_comment_count=""+comment_count;
        data_list.remove(currentPage);
        data_list.add(currentPage,item);
        adapter.notifyDataSetChanged();
    }



    // this will call when go to the home tab From other tab.
    // this is very importent when for video play and pause when the focus is changes
    boolean is_visible_to_user;
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        is_visible_to_user=isVisibleToUser;

        if(privious_player!=null && (isVisibleToUser && !is_user_stop_video)){
            privious_player.setPlayWhenReady(true);
        }else if(privious_player!=null && !isVisibleToUser){
            privious_player.setPlayWhenReady(false);
        }
    }



   // when we swipe for another video this will relaese the privious player
    SimpleExoPlayer privious_player;
    public void Release_Privious_Player(){
        if(privious_player!=null) {
            privious_player.removeListener(this);
            privious_player.release();
        }
    }




    // this function will call for like the video and Call an Api for like the video
    public void Like_Video(final int position, final Home_Get_Set home_get_set){
        String action=home_get_set.liked;

        OpenComment(home_get_set,"balsal");
///////////////////////////////////////////////////////////////////////////////////////////////////////

        if(action.equals("1")){
            action="0";
            home_get_set.like_count=""+(Integer.parseInt(home_get_set.like_count) -1);
        }else {
            action="1";
            home_get_set.like_count=""+(Integer.parseInt(home_get_set.like_count) +1);
        }


        data_list.remove(position);
        home_get_set.liked=action;
        data_list.add(position,home_get_set);
        adapter.notifyDataSetChanged();

        Functions.Call_Api_For_like_video(getActivity(), home_get_set.video_id, action,new API_CallBack() {

            @Override
            public void ArrayData(ArrayList arrayList) {

            }

            @Override
            public void OnSuccess(String responce) {

            }

            @Override
            public void OnFail(String responce) {

            }
        });

    }

    private void OpenComment(Home_Get_Set item,String type) {

        int comment_counnt=Integer.parseInt(item.video_comment_count);

        Fragment_Data_Send fragment_data_send=this;

        Comment_F comment_f = new Comment_F(comment_counnt,fragment_data_send);
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.in_from_bottom, R.anim.out_to_top, R.anim.in_from_top, R.anim.out_from_bottom);
        Bundle args = new Bundle();
        args.putString("video_id",item.video_id);
        args.putString("user_id",item.fb_id);
        args.putString("type","comment");
        comment_f.setArguments(args);
        transaction.addToBackStack(null);
        transaction.replace(R.id.MainMenuFragment, comment_f).commit();


    }


    // this will open the comment screen
    private void OpenComment(Home_Get_Set item) {

        int comment_counnt=Integer.parseInt(item.video_comment_count);

        Fragment_Data_Send fragment_data_send=this;

        Comment_F comment_f = new Comment_F(comment_counnt,fragment_data_send);
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.in_from_bottom, R.anim.out_to_top, R.anim.in_from_top, R.anim.out_from_bottom);
        Bundle args = new Bundle();
        args.putString("video_id",item.video_id);
        args.putString("user_id",item.fb_id);
        comment_f.setArguments(args);
        transaction.addToBackStack(null);
        transaction.replace(R.id.MainMenuFragment, comment_f).commit();


    }



    // this will open the profile of user which have uploaded the currenlty running video
    private void OpenProfile(Home_Get_Set item,boolean from_right_to_left) {
        if(Variables.sharedPreferences.getString(Variables.u_id,"0").equals(item.fb_id)){

            TabLayout.Tab profile= MainMenuFragment.tabLayout.getTabAt(4);
            profile.select();

        }else {
            Profile_F profile_f = new Profile_F(new Fragment_Callback() {
                @Override
                public void Responce(Bundle bundle) {
                    Call_Api_For_Singlevideos(currentPage);
                }
            });
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            if(from_right_to_left)
            transaction.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_left, R.anim.in_from_left, R.anim.out_to_right);
            else
                transaction.setCustomAnimations(R.anim.in_from_bottom, R.anim.out_to_top, R.anim.in_from_top, R.anim.out_from_bottom);

            Bundle args = new Bundle();
            args.putString("user_id", item.fb_id);
            args.putString("user_name",item.first_name+" "+item.last_name);
            args.putString("user_pic",item.profile_pic);
            profile_f.setArguments(args);
            transaction.addToBackStack(null);
            transaction.replace(R.id.MainMenuFragment, profile_f).commit();
        }

    }


    // this will open the profile of user which have uploaded the currenlty running video
    private void OpenHashtag(String tag) {

            Taged_Videos_F taged_videos_f = new Taged_Videos_F();
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            transaction.setCustomAnimations(R.anim.in_from_bottom, R.anim.out_to_top, R.anim.in_from_top, R.anim.out_from_bottom);
            Bundle args = new Bundle();
            args.putString("tag", tag);
            taged_videos_f.setArguments(args);
            transaction.addToBackStack(null);
            transaction.replace(R.id.MainMenuFragment, taged_videos_f).commit();


    }



    private void Show_video_option(final Home_Get_Set home_get_set) {

        final CharSequence[] options = { "Save Video","Cancel" };

        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(context,R.style.AlertDialogCustom);

        builder.setTitle(null);

        builder.setItems(options, new DialogInterface.OnClickListener() {

            @Override

            public void onClick(DialogInterface dialog, int item) {

                if (options[item].equals("Save Video"))

                {
                    if(Functions.Checkstoragepermision(getActivity()))
                    Save_Video(home_get_set);

                }


                else if (options[item].equals("Cancel")) {

                    dialog.dismiss();

                }

            }

        });

        builder.show();

    }

    public void Save_Video(final Home_Get_Set item){

        Functions.Show_determinent_loader(context,false,false);
        PRDownloader.initialize(getActivity().getApplicationContext());
        DownloadRequest prDownloader= PRDownloader.download(item.video_url, Variables.app_folder, item.video_id+"no_watermark"+".mp4")
                .build()
                .setOnStartOrResumeListener(new OnStartOrResumeListener() {
                    @Override
                    public void onStartOrResume() {

                    }
                })
                .setOnPauseListener(new OnPauseListener() {
                    @Override
                    public void onPause() {

                    }
                })
                .setOnCancelListener(new OnCancelListener() {
                    @Override
                    public void onCancel() {

                    }
                })
                .setOnProgressListener(new OnProgressListener() {
                    @Override
                    public void onProgress(Progress progress) {

                        int prog=(int)((progress.currentBytes*100)/progress.totalBytes);
                        Functions.Show_loading_progress(prog/2);

                    }
                });


              prDownloader.start(new OnDownloadListener() {
                @Override
                public void onDownloadComplete() {
                    Applywatermark(item);
                   }

                @Override
                public void onError(Error error) {
                    Delete_file_no_watermark(item);
                    Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show();
                    Functions.cancel_determinent_loader();
                }


            });




    }

    public void Applywatermark(final Home_Get_Set item){

         Bitmap myLogo = ((BitmapDrawable)getResources().getDrawable(R.drawable.ic_watermark_image)).getBitmap();
         Bitmap bitmap_resize=Bitmap.createScaledBitmap(myLogo, 50, 50, false);
         GlWatermarkFilter filter=new GlWatermarkFilter(bitmap_resize, GlWatermarkFilter.Position.LEFT_TOP);
         new GPUMp4Composer(Variables.app_folder+item.video_id+"no_watermark"+".mp4",
                Variables.app_folder+item.video_id+".mp4")
                .filter(filter)

                .listener(new GPUMp4Composer.Listener() {
                    @Override
                    public void onProgress(double progress) {

                        Log.d("resp",""+(int) (progress*100));
                        Functions.Show_loading_progress((int)((progress*100)/2)+50);

                    }

                    @Override
                    public void onCompleted() {

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                Functions.cancel_determinent_loader();
                                Delete_file_no_watermark(item);
                                Scan_file(item);

                            }
                        });


                    }

                    @Override
                    public void onCanceled() {
                        Log.d("resp", "onCanceled");
                    }

                    @Override
                    public void onFailed(Exception exception) {

                        Log.d("resp",exception.toString());

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {

                                    Delete_file_no_watermark(item);
                                    Functions.cancel_determinent_loader();
                                    Toast.makeText(context, "Try Again", Toast.LENGTH_SHORT).show();

                                }catch (Exception e){

                                }
                            }
                        });

                    }
                })
                .start();
    }


    public void Delete_file_no_watermark(Home_Get_Set item){
        File file=new File(Variables.app_folder+item.video_id+"no_watermark"+".mp4");
        if(file.exists()){
            file.delete();
        }
    }

    public void Scan_file(Home_Get_Set item){
        MediaScannerConnection.scanFile(getActivity(),
                new String[] { Variables.app_folder+item.video_id+".mp4" },
                null,
                new MediaScannerConnection.OnScanCompletedListener() {

                    public void onScanCompleted(String path, Uri uri) {

                        Log.i("ExternalStorage", "Scanned " + path + ":");
                        Log.i("ExternalStorage", "-> uri=" + uri);
                    }
                });
    }



    public boolean is_fragment_exits(){
        FragmentManager fm = getActivity().getSupportFragmentManager();
        if(fm.getBackStackEntryCount()==0){
            return false;
        }else {
            return true;
        }

    }

    // this is lifecyle of the Activity which is importent for play,pause video or relaese the player
    @Override
    public void onResume() {
        super.onResume();
        if((privious_player!=null && (is_visible_to_user && !is_user_stop_video)) && !is_fragment_exits() ){
            privious_player.setPlayWhenReady(true);
        }
    }


    @Override
    public void onPause() {
        super.onPause();
        if(privious_player!=null){
            privious_player.setPlayWhenReady(false);
        }
    }


    @Override
    public void onStop() {
        super.onStop();
        if(privious_player!=null){
            privious_player.setPlayWhenReady(false);
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if(privious_player!=null){
            privious_player.release();
        }
    }



    public boolean check_permissions() {

        String[] PERMISSIONS = {
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.CAMERA
        };

        if (!hasPermissions(context, PERMISSIONS)) {
            requestPermissions(PERMISSIONS, 2);
        }else {

            return true;
        }

        return false;
    }


    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }





    // Bottom all the function and the Call back listener of the Expo player
    @Override
    public void onTimelineChanged(Timeline timeline, @Nullable Object manifest, int reason) {

    }


    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

    }


    @Override
    public void onLoadingChanged(boolean isLoading) {

    }


    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {

        if(playbackState== Player.STATE_BUFFERING){
            p_bar.setVisibility(View.VISIBLE);
        }
        else if(playbackState== Player.STATE_READY){
             p_bar.setVisibility(View.GONE);
        }


    }

    @Override
    public void onRepeatModeChanged(int repeatMode) {

    }

    @Override
    public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {

    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {

    }

    @Override
    public void onPositionDiscontinuity(int reason) {

    }

    @Override
    public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

    }


    @Override
    public void onSeekProcessed() {

    }






    void call_priceapi() {
        OkHttpClient okHttpClient = new OkHttpClient.Builder().build();
        //RequestBody requestBody = RequestBody.create(JSON,jsonObject.toString());
       // Toast.makeText(context, "Requesting to server.", Toast.LENGTH_SHORT).show();
        Request request = new Request.Builder().get().url("https://www.funto.in/API/index.php?p=coinPrice").build();
        Functions.Show_loader(context, false, false);
        okHttpClient.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String responsestr = response.body().string();
                //Log.e("response", responsestr);
                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                       // Toast.makeText(context, "Response Got from server.\n" + responsestr, Toast.LENGTH_LONG).show();
                        Functions.cancel_loader();
                        try {
                            JSONObject object = new JSONObject(responsestr);
                            JSONArray array;
                            JSONObject object1;
                            Log.e("object",object.toString());
                            try {
                                array = object.getJSONArray("msg");
                                alertDialog.dismiss();
                                showPriceListJsonArray(array);
                            }catch (JSONException e){
                                object1 = object.getJSONObject("msg");
                                alertDialog.dismiss();
                                showPriceListJsonObject(object1);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {}
        });

    }

    void call_withdraw_price(){
        OkHttpClient okHttpClient = new OkHttpClient.Builder().build();
        //RequestBody requestBody = RequestBody.create(JSON,jsonObject.toString());
        Toast.makeText(context, "Requesting to server.", Toast.LENGTH_SHORT).show();
        Request request = new Request.Builder().get().url("https://www.funto.in/API/index.php?p=coinWithdrawPrice").build();
        Functions.Show_loader(context, false, false);
        okHttpClient.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String responsestr = response.body().string();
               // Log.e("response", responsestr);
                ((Activity) context).runOnUiThread(new Runnable() {
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
                                alertDialog.dismiss();
                                showWithdrawPriceListJsonArray(array);
                            }catch (JSONException e){
                                object1 = object.getJSONObject("msg");
                                alertDialog.dismiss();
                                showWithdrawPriceListJsonObject(object1);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {}
        });
    }

    public static void call_buy_coin_list(String fb_id,String coin,String price){


        Variables.coins = null;
        Variables.price = null;


        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        OkHttpClient okHttpClient = new OkHttpClient.Builder().build();

        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("fb_id",fb_id);
            jsonObject.put("coins",coin);
            jsonObject.put("price",price);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody requestBody = RequestBody.create(JSON,jsonObject.toString());
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
            public void onFailure(@NotNull Call call, @NotNull IOException e) {}
        });
    }



    void call_coin_withdraw(String userid,String coin,String price,String deduct){
        OkHttpClient okHttpClient = new OkHttpClient.Builder().build();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("fb_id",userid);
            jsonObject.put("coins",coin);
            jsonObject.put("deduct",deduct);
            jsonObject.put("price",price);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody requestBody = RequestBody.create(JSON,jsonObject.toString());
       // Log.e("request body",jsonObject.toString());
        //Toast.makeText(context, "Requesting to server.", Toast.LENGTH_SHORT).show();
        Request request = new Request.Builder().post(requestBody).url("https://funto.in/API/index.php?p=withdrawCoinAdd").build();
        Functions.Show_loader(context, false, false);
        okHttpClient.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String responsestr = response.body().string();
                Log.e("response", responsestr);
                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context, "Response Got from server.\n" + responsestr, Toast.LENGTH_LONG).show();
                        Functions.cancel_loader();



                    }
                });
            }
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {}
        });
    }


    void showWithdrawPriceListJsonArray(JSONArray jsonArray){
        builder = new AlertDialog.Builder(context);
        View vi  = getLayoutInflater().inflate(R.layout.showpricelist,null,false);
        TextView titletext = vi.findViewById(R.id.title);
        LinearLayout mainLayout = vi.findViewById(R.id.mainlayout);
        titletext.setText("Coin Withdraw Price List");
        for(int i = 0;i<jsonArray.length();i++){
            View view = getLayoutInflater().inflate(R.layout.showpricelistsingle,null);
            TextView t1 = view.findViewById(R.id.text);
            try {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String text ="Withdraw "+ jsonObject.getString("coin")+ " coins at "+jsonObject.getString("price")+" deduct rate is "+jsonObject.getString("deduct");
                t1.setText(text);
                if(view.getParent()!=null){
                    ((ViewGroup)view.getParent()).removeView(view);
                }


                t1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (Variables.sharedPreferences.getBoolean(Variables.islogin, false)) {
                            String id = Variables.user_id;
                            Log.e("userid",id);

                            try {
                                call_coin_withdraw(id,jsonObject.getString("coin"),jsonObject.getString("price"),jsonObject.getString("deduct"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        } else {

                            Intent intent = new Intent(getActivity(), Login_A.class);
                            startActivity(intent);
                            getActivity().overridePendingTransition(R.anim.in_from_bottom, R.anim.out_to_top);
                        }
                    }
                });
                mainLayout.addView(view);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        builder.setView(null);
        builder.setView(vi);
        alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(true);
        alertDialog.show();
    }
    void showWithdrawPriceListJsonObject(JSONObject jsonObject){
        builder = new AlertDialog.Builder(context);
        View vi  = getLayoutInflater().inflate(R.layout.showpricelist,null,false);
        TextView titletext = vi.findViewById(R.id.title);
        LinearLayout mainLayout = vi.findViewById(R.id.mainlayout);
        titletext.setText("Coin Withdraw Price List");
        View view = getLayoutInflater().inflate(R.layout.showpricelistsingle,null);
        TextView t1 = view.findViewById(R.id.text);
        try {
            String text ="Withdraw "+ jsonObject.getString("coin")+ " coins at "+jsonObject.getString("price")+" deduct rate is "+jsonObject.getString("deduct");
            t1.setText(text);
            if(view.getParent()!=null){
                ((ViewGroup)view.getParent()).removeView(view);
            }
            mainLayout.addView(view);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        builder.setView(null);
        builder.setView(vi);
        alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(true);
        alertDialog.show();
    }

    void showPriceListJsonArray(JSONArray jsonArray){
        builder = new AlertDialog.Builder(context);
        View vi  = getLayoutInflater().inflate(R.layout.showpricelist,null,false);
        TextView titletext = vi.findViewById(R.id.title);
        LinearLayout mainLayout = vi.findViewById(R.id.mainlayout);
        titletext.setText("Price List");
        for(int i = 0;i<jsonArray.length();i++){
            View view = getLayoutInflater().inflate(R.layout.showpricelistsingle,null);
            TextView t1 = view.findViewById(R.id.text);
            try {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String text = jsonObject.getString("coin")+ " coins at "+jsonObject.getString("price");
                t1.setText(text);
                if(view.getParent()!=null){
                    ((ViewGroup)view.getParent()).removeView(view);
                }
                t1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if (Variables.sharedPreferences.getBoolean(Variables.islogin, false)) {
                            String id = Variables.user_id;
                           // Log.e("userid",id);
                            try {
                                Variables.coins = jsonObject.getString("coin");
                                Variables.price = jsonObject.getString("price");
                               startPayment(jsonObject.getString("price"),"Get "+jsonObject.getString("coin")+" coins at "+jsonObject.getString("price")+".");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        } else {

                            Intent intent = new Intent(getActivity(), Login_A.class);
                            startActivity(intent);
                            getActivity().overridePendingTransition(R.anim.in_from_bottom, R.anim.out_to_top);
                        }

                    }
                });
                mainLayout.addView(view);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        builder.setView(null);
        builder.setView(vi);
        alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(true);
        alertDialog.show();

    }
    void showPriceListJsonObject(JSONObject jsonObject){
        builder = new AlertDialog.Builder(context);
        View vi  = getLayoutInflater().inflate(R.layout.showpricelist,null,false);
        TextView titletext = vi.findViewById(R.id.title);
        LinearLayout mainLayout = vi.findViewById(R.id.mainlayout);
        titletext.setText("Price List");
        View view = getLayoutInflater().inflate(R.layout.showpricelistsingle,null);
        TextView t1 = view.findViewById(R.id.text);
        try {
            String text = jsonObject.getString("coin")+ " coins at "+jsonObject.getString("price");
            t1.setText(text);
            if(view.getParent()!=null){
                ((ViewGroup)view.getParent()).removeView(view);
            }
            mainLayout.addView(view);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        builder.setView(null);
        builder.setView(vi);
        alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(true);
        alertDialog.show();
    }

    public void startPayment(String price,String description) {

        Checkout checkout = new Checkout();
        checkout.setKeyID(Variables.paymentkeyid);
        final Activity activity = ((Activity)context);
        try {
            JSONObject options = new JSONObject();

            options.put("name", "Funto");
            options.put("description", description);
            options.put("image", "https://www.funto.in/public/assets/images/funto-logo.png");
            options.put("currency", "INR");

            Double priceint = Double.parseDouble(price)*100;
            options.put("amount", String.valueOf(priceint));
            options.put("prefill.name",Variables.user_name);
            options.put("prefill.email",Variables.user_email);

            checkout.open(activity, options);
        } catch (Exception e) {
            Log.e("error in payment", "Error in starting Razorpay Checkout", e);
            Toast.makeText(mainMenuActivity, "Error In Payment!", Toast.LENGTH_SHORT).show();
        }
    }

    void call_sticker_price_list(){
        OkHttpClient okHttpClient = new OkHttpClient.Builder().build();
        //RequestBody requestBody = RequestBody.create(JSON,jsonObject.toString());
        Toast.makeText(context, "Requesting to server.", Toast.LENGTH_SHORT).show();
        Request request = new Request.Builder().get().url("https://www.funto.in/API/index.php?p=giftCoinPrice").build();
        Functions.Show_loader(context, false, false);
        okHttpClient.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String responsestr = response.body().string();
                //Log.e("response", responsestr);
                ((Activity) context).runOnUiThread(new Runnable() {
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
                                alertDialog.dismiss();
                                showStickerPriceList(array);
                            }catch (JSONException e){
                                object1 = object.getJSONObject("msg");
                                alertDialog.dismiss();
                                showStickerPriceList(object1);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {}
        });
    }
    void showStickerPriceList(JSONArray jsonArray){
        builder = new AlertDialog.Builder(context);
        View vi  = getLayoutInflater().inflate(R.layout.showpricelist,null,false);
        TextView titletext = vi.findViewById(R.id.title);
        LinearLayout mainLayout = vi.findViewById(R.id.mainlayout);
        titletext.setText("Price List");
        for(int i = 0;i<jsonArray.length();i++){
            View view = getLayoutInflater().inflate(R.layout.showpricelistsingle,null);
            TextView t1 = view.findViewById(R.id.text);
            try {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String text = jsonObject.getString("coin")+ " coins at "+jsonObject.getString("price");
                t1.setText(text);
                if(view.getParent()!=null){
                    ((ViewGroup)view.getParent()).removeView(view);
                }
                t1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (Variables.sharedPreferences.getBoolean(Variables.islogin, false)) {
                            String id = Variables.user_id;
                            //Log.e("userid",id);
                            try {
                                Variables.coins = jsonObject.getString("coin");
                                Variables.price = jsonObject.getString("price");
                                startPayment(jsonObject.getString("price"),"Get "+jsonObject.getString("coin")+" coins at "+jsonObject.getString("price")+".");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        } else {

                            Intent intent = new Intent(getActivity(), Login_A.class);
                            startActivity(intent);
                            getActivity().overridePendingTransition(R.anim.in_from_bottom, R.anim.out_to_top);
                        }
                    }
                });
                mainLayout.addView(view);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        builder.setView(null);
        builder.setView(vi);
        alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(true);
        alertDialog.show();
    }

    void showStickerPriceList(JSONObject jsonObject){
        builder = new AlertDialog.Builder(context);
        View vi  = getLayoutInflater().inflate(R.layout.showpricelist,null,false);
        TextView titletext = vi.findViewById(R.id.title);
        LinearLayout mainLayout = vi.findViewById(R.id.mainlayout);
        titletext.setText("Price List");
        View view = getLayoutInflater().inflate(R.layout.showpricelistsingle,null);
        TextView t1 = view.findViewById(R.id.text);
        try {
            String text = jsonObject.getString("coin")+ " coins at "+jsonObject.getString("price");
            t1.setText(text);
            if(view.getParent()!=null){
                ((ViewGroup)view.getParent()).removeView(view);
            }
            mainLayout.addView(view);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        builder.setView(null);
        builder.setView(vi);
        alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(true);
        alertDialog.show();
    }

    void addCoinFromVideoWatch(String video_id){
        if(!Variables.videoid.contains(video_id)){
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            OkHttpClient okHttpClient = new OkHttpClient.Builder().build();
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("fb_id",Variables.user_id);
                jsonObject.put("coins","1");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            RequestBody requestBody = RequestBody.create(JSON,jsonObject.toString());
           // Toast.makeText(mainMenuActivity, "You Have Gained One Coin.", Toast.LENGTH_SHORT).show();
            Request request = new Request.Builder().post(requestBody).url("https://funto.in/API/index.php?p=videoCoinAdd").build();
            okHttpClient.newCall(request).enqueue(new okhttp3.Callback() {
                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    String responsestr = response.body().string();
                    //Log.e("response", responsestr);
                    ((Activity) mainMenuActivity).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //Toast.makeText(mainMenuActivity, "Response Got from server.\n" + responsestr, Toast.LENGTH_LONG).show();
                            //get response from server then add coin to profile
                            //Log.e("coin add response",responsestr);
                            showCoinBalance();
                        }
                    });
                }
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {}
            });
        }
        Variables.videoid.add(video_id);
    }

    public static void showCoinBalance(){
        MediaType JSON =MediaType.parse("application/json; charset=utf-8");
        OkHttpClient okHttpClient = new OkHttpClient.Builder().build();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("fb_id",Variables.user_id);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody requestBody = RequestBody.create(JSON,jsonObject.toString());
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
                        Log.e("coin response",responsestr);
                        JSONObject jsonObject1;
                        JSONArray array;
                        try {
                            jsonObject1 = new JSONObject(responsestr);
                            array = jsonObject1.getJSONArray("msg");
                            String coins = array.getJSONObject(0).getString("totalCoin");
                            UpdateCoin(coins);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {}
        });
    }

}

package com.mumba.funto.Accounts;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.mumba.funto.Main_Menu.MainMenuActivity;
import com.mumba.funto.R;
import com.mumba.funto.SimpleClasses.ApiRequest;
import com.mumba.funto.SimpleClasses.Callback;
import com.mumba.funto.SimpleClasses.Functions;
import com.mumba.funto.SimpleClasses.Variables;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import static com.mumba.funto.Home.Home_F.showCoinBalance;

@SuppressWarnings("ConstantConditions")
public class Login_A extends Activity {


    final String TAG = "Login_A";
    FirebaseAuth mAuth;
    FirebaseUser firebaseUser;


    SharedPreferences sharedPreferences;

    View top_view;

    TextView login_title_txt, signup;


    AlertDialog.Builder builder;
    AlertDialog alertDialog;

    View dialogView;


    @SuppressLint("HardwareIds")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);


        if (Build.VERSION.SDK_INT == 26) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_USER);
        }

        getWindow().setBackgroundDrawable(
                new ColorDrawable(android.graphics.Color.TRANSPARENT));

        this.getWindow()
                .setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                        WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);

        signup = findViewById(R.id.signup);
        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();

        builder = new AlertDialog.Builder(this);
        // if the user is already login trought facebook then we will logout the user automatically
        LoginManager.getInstance().logOut();

        sharedPreferences = getSharedPreferences(Variables.pref_name, MODE_PRIVATE);

        findViewById(R.id.facebook_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Loginwith_FB();
            }
        });


        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showLoginBox();
            }
        });

        findViewById(R.id.google_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Sign_in_with_gmail();
            }
        });


        findViewById(R.id.Goback).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        top_view = findViewById(R.id.top_view);


        login_title_txt = findViewById(R.id.login_title_txt);
        login_title_txt.setText("You need a " + getString(R.string.app_name) + "\naccount to Continue");


        SpannableString ss = new SpannableString("By signing up, you confirm that you agree to our \n Terms of Use and have read and understood \n our Privacy Policy.");
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                Open_Privacy_policy();
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(false);
            }
        };
        ss.setSpan(clickableSpan, 99, ss.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        TextView textView = (TextView) findViewById(R.id.login_terms_condition_txt);
        textView.setText(ss);
        textView.setClickable(true);
        textView.setMovementMethod(LinkMovementMethod.getInstance());


        printKeyHash();


    }


    void showLoginBox() {
        dialogView = getLayoutInflater().inflate(R.layout.signinlayout, null);

        TextInputEditText email, password;
        Button login;
        TextView signup;

        email = dialogView.findViewById(R.id.email);
        password = dialogView.findViewById(R.id.password);


        login = dialogView.findViewById(R.id.loginbutton);
        signup = dialogView.findViewById(R.id.sign_up);


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String emailstr = email.getText().toString();
                String passwordstr = password.getText().toString();

                if(TextUtils.isEmpty(emailstr)){
                    email.setError("Field Can Not Be Empty!");
                    email.requestFocus();
                }else if(TextUtils.isEmpty(passwordstr)){
                    password.setError("Field Can Not Be Empty!");
                    password.requestFocus();
                }else {

                    String key = md5(passwordstr);


                    Call_Api_For_Signin(emailstr, key);
                }
            }
        });


        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
                showRegisterBox();
            }
        });


        builder.setView(null);
        builder.setView(dialogView);


        alertDialog = builder.create();

        alertDialog.setCanceledOnTouchOutside(true);
        alertDialog.show();
        alertDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);


    }
    public String md5(String s) {
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
            digest.update(s.getBytes());
            byte[] messageDigest = digest.digest();
            // Create Hex String
            StringBuilder hexString = new StringBuilder();
            for (byte b : messageDigest) hexString.append(Integer.toHexString(0xFF & b));
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }
    void showRegisterBox() {
        dialogView = getLayoutInflater().inflate(R.layout.signuplayout, null);


        TextInputEditText firstname, lastname, email, password, confirmpassword;
        TextView cancelbutton;
        Spinner gender;
        Button signupbutton;

        signupbutton = dialogView.findViewById(R.id.signupbutton);
        cancelbutton = dialogView.findViewById(R.id.signinbutton);
        cancelbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
                showLoginBox();
            }
        });


        firstname = dialogView.findViewById(R.id.firstname);
        lastname = dialogView.findViewById(R.id.lastname);
        gender = dialogView.findViewById(R.id.gender);
        email = dialogView.findViewById(R.id.email);
        password = dialogView.findViewById(R.id.password);
        confirmpassword = dialogView.findViewById(R.id.confirmpassword);


        String[] arraySpinner = new String[]{"Male", "Female", "Other"};
        ArrayAdapter<String> adapter = new ArrayAdapter(this,
                android.R.layout.simple_spinner_item, arraySpinner);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        gender.setAdapter(adapter);


        signupbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                String firstnamestr = firstname.getText().toString();
                String lastnamestr = lastname.getText().toString();
                String genderstr = gender.getSelectedItem().toString();
                String emailstr = email.getText().toString();
                String signuptypestr = "Regular";
                String passwordstr = password.getText().toString();
                String confirmpasswordstr = confirmpassword.getText().toString();

                if(TextUtils.isEmpty(firstnamestr)){
                    firstname.setError("Field Can Not Be Empty!");
                    firstname.requestFocus();
                }else if(TextUtils.isEmpty(lastnamestr)){
                    lastname.setError("Field Can Not Be Empty!");
                    lastname.requestFocus();
                }else if(TextUtils.isEmpty(emailstr)){
                    email.setError("Field Can Not Be Empty!");
                    email.requestFocus();
                }else if(TextUtils.isEmpty(passwordstr)){
                    password.setError("Field Can Not Be Empty!");
                    password.requestFocus();
                }else if(TextUtils.isEmpty(confirmpasswordstr)){
                    confirmpassword.setError("Field Can Not Be Empty!");
                    confirmpassword.requestFocus();
                }else if (confirmpasswordstr.equals(passwordstr)) {

                    String passwordid = md5(passwordstr);


                    Call_Api_For_Signup(passwordid, firstnamestr, lastnamestr, "null", genderstr, signuptypestr, emailstr);
                    Variables.user_email = emailstr;
                } else {
                    password.setError("password did not Match!");
                    password.requestFocus();
                }

            }
        });

        builder.setView(null);
        builder.setView(dialogView);


        alertDialog = builder.create();

        alertDialog.show();
        alertDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
    }

    public void Open_Privacy_policy() {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(Variables.privacy_policy));
        startActivity(browserIntent);
    }


    @Override
    public void onEnterAnimationComplete() {
        super.onEnterAnimationComplete();
        AlphaAnimation anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(200);
        top_view.startAnimation(anim);
        top_view.setVisibility(View.VISIBLE);

    }

    @Override
    public void onBackPressed() {
        top_view.setVisibility(View.GONE);
        finish();
        overridePendingTransition(R.anim.in_from_top, R.anim.out_from_bottom);


    }


    // Bottom two function are related to Fb implimentation
    private CallbackManager mCallbackManager;

    //facebook implimentation
    public void Loginwith_FB() {

        LoginManager.getInstance()
                .logInWithReadPermissions(Login_A.this,
                        Arrays.asList("public_profile", "email"));

        // initialze the facebook sdk and request to facebook for login
        FacebookSdk.sdkInitialize(this.getApplicationContext());
        mCallbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                handleFacebookAccessToken(loginResult.getAccessToken());
                Log.e(TAG + "1", loginResult.getAccessToken() + "");
            }

            @Override
            public void onCancel() {
                // App code
                Toast.makeText(Login_A.this, "Login Cancel", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException error) {
                Log.e(TAG + "2", "" + error.toString());
                Toast.makeText(Login_A.this, "Login Error" + error.toString(), Toast.LENGTH_SHORT).show();
            }

        });


    }

    private void handleFacebookAccessToken(final AccessToken token) {
        // if user is login then this method will call and
        // facebook will return us a token which will user for get the info of user
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        Log.e(TAG + "3", token.getToken() + "");
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Functions.Show_loader(Login_A.this, false, false);

                            GraphRequest request = GraphRequest.newMeRequest(token, new GraphRequest.GraphJSONObjectCallback() {
                                @Override
                                public void onCompleted(JSONObject user, GraphResponse graphResponse) {

                                    Functions.cancel_loader();
                                    Log.e(TAG+"4", user.toString());
                                    //after get the info of user we will pass to function which will store the info in our server
                                    final String id = Profile.getCurrentProfile().getId();

                                    String fname = "" + user.optString("first_name");
                                    String lname = "" + user.optString("last_name");
                                    String gender = "" + user.optString("gender");
                                    String email = "" + user.optString("email");


                                    if (fname.equals("") || fname.equals("null"))
                                        fname = getResources().getString(R.string.app_name);

                                    if (lname.equals("") || lname.equals("null"))
                                        lname = "";


                                    Log.e("facebook details", fname + " " + lname + " " + gender + " " + email);

//                                    CallApi(id,fname,lname,gender,"https://graph.facebook.com/"+id+"/picture?width=500&width=500",
//                                            "facebook");


                                    Call_Api_For_Signup("" + id, fname
                                            , lname,
                                            "https://graph.facebook.com/" + id + "/picture?width=500&width=500",
                                            gender, "facebook", email);

                                    Variables.user_email = email;

                                }
                            });

                            // here is the request to facebook sdk for which type of info we have required
                            Bundle parameters = new Bundle();
                            parameters.putString("fields", "last_name,first_name,email,gender");
                            request.setParameters(parameters);
                            request.executeAsync();
                        } else {
                            Functions.cancel_loader();
                            Toast.makeText(Login_A.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();

                            Log.e("facebook error", task.getException().getMessage());
                        }

                    }
                });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Pass the activity result back to the Facebook SDK
        if (requestCode == 123) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        } else if (mCallbackManager != null)
            mCallbackManager.onActivityResult(requestCode, resultCode, data);

    }


    //google Implimentation
    GoogleSignInClient mGoogleSignInClient;

    public void Sign_in_with_gmail() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(Login_A.this);


        if (account != null) {
            String fname = "" + account.getGivenName();
            String lname = "" + account.getFamilyName();
            String id = account.getId();
            String email = account.getEmail();

            String gender = "male";
            String pic_url;
            if (account.getPhotoUrl() != null) {
                pic_url = account.getPhotoUrl().toString();
            } else {
                pic_url = "null";
            }
            if (fname.equals("") || fname.equals("null"))
                fname = getResources().getString(R.string.app_name);

            if (lname.equals("") || lname.equals("null"))
                lname = "User";


            // CallApi(id,fname,lname,gender,pic_url,"gmail");
            Call_Api_For_Signup(id, fname, lname, pic_url, gender, "gmail", email);
            Variables.user_email = email;
        } else {
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, 123);
        }

    }


    //Relate to google login
    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            if (account != null) {
                String id = account.getId();
                String fname = "" + account.getGivenName();
                String lname = "" + account.getFamilyName();
                String gender = "male";
                String email = account.getEmail();

                // if we do not get the picture of user then we will use default profile picture

                String pic_url;
                if (account.getPhotoUrl() != null) {
                    pic_url = account.getPhotoUrl().toString();
                } else {
                    pic_url = "null";
                }


                if (fname.equals("") || fname.equals("null"))
                    fname = getResources().getString(R.string.app_name);

                if (lname.equals("") || lname.equals("null"))
                    lname = "User";

                //CallApi(id,fname,lname,gender,pic_url,"gmail");

                Call_Api_For_Signup(id, fname, lname, pic_url, gender, "gmail", email);
                Variables.user_email = email;


            }
        } catch (ApiException e) {
            Log.e(TAG + "5", "signInResult:failed code=" + e.getStatusCode());
        }

    }


    // this function call an Api for Signin
    private void Call_Api_For_Signup(String id,
                                     String f_name,
                                     String l_name,
                                     String picture,
                                     String gender,
                                     String singnup_type,
                                     String email) {


        PackageInfo packageInfo = null;
        try {
            packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        String appversion = packageInfo.versionName;

        JSONObject parameters = new JSONObject();
        try {

            if(singnup_type.equals("gmail")){
                parameters.put("fb_id", id);
            }else {
                parameters.put("password",md5(id));
            }


            parameters.put("first_name", "" + f_name);
            parameters.put("last_name", "" + l_name);
            parameters.put("profile_pic", picture);
            parameters.put("email", email);


            if (gender == null || gender.equals("")) {
                gender = "Male";
            }
            parameters.put("gender", gender);
            parameters.put("version", appversion);
            parameters.put("signup_type", singnup_type);
            parameters.put("device", Variables.device);


        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.e(TAG+"6",parameters.toString());

        Functions.Show_loader(this, false, false);
        ApiRequest.Call_Api(this, Variables.SignUp, parameters, new Callback() {
            @Override
            public void Responce(String resp) {
                Functions.cancel_loader();
                Parse_signup_data(resp);

            }
        });

    }


    // this function call an Api for Signin
    private void Call_Api_For_Signin(String username, String password) {


        JSONObject parameters = new JSONObject();
        try {

            parameters.put("password", password);
            parameters.put("email", username);


        } catch (JSONException e) {
            e.printStackTrace();
        }

        Functions.Show_loader(this, false, false);
        ApiRequest.Call_Api(this, Variables.SignUp, parameters, new Callback() {
            @Override
            public void Responce(String resp) {

                Log.e(TAG+"7", resp);
                Functions.cancel_loader();
                Parse_signup_data(resp);

            }
        });

    }


    // if the signup successfull then this method will call and it store the user info in local
    public void Parse_signup_data(String loginData) {

        Log.e(TAG+"8",loginData);

        try {
            JSONObject jsonObject = new JSONObject(loginData);
            String code = jsonObject.optString("code");
            if (code.equals("200")) {
                JSONArray jsonArray = jsonObject.getJSONArray("msg");
                JSONObject userdata = jsonArray.getJSONObject(0);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(Variables.u_id, userdata.optString("uid"));
                editor.putString(Variables.f_name, userdata.optString("first_name"));
                editor.putString(Variables.l_name, userdata.optString("last_name"));
                editor.putString(Variables.u_name, userdata.optString("username"));
                editor.putString(Variables.gender, userdata.optString("gender"));
                editor.putString(Variables.u_pic, userdata.optString("profile_pic"));
                editor.putString(Variables.api_token, userdata.optString("tokon"));
                editor.putBoolean(Variables.islogin, true);
                editor.commit();
                Variables.sharedPreferences = getSharedPreferences(Variables.pref_name, MODE_PRIVATE);
                Variables.user_id = Variables.sharedPreferences.getString(Variables.u_id, "");

                top_view.setVisibility(View.GONE);
                finish();
                startActivity(new Intent(this, MainMenuActivity.class));
                showCoinBalance();
            } else {
                Toast.makeText(this, "" + jsonObject.optString("msg"), Toast.LENGTH_SHORT).show();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    // this function will print the keyhash of your project
    // which is very helpfull during Fb login implimentation
    public void printKeyHash() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.i("keyhash", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }


}

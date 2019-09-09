package com.nanangaja.soogi.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.nanangaja.soogi.MainActivity;
import com.nanangaja.soogi.R;
import com.nanangaja.soogi.app.AppController;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class LoginActivity extends AppCompatActivity {

    private boolean doubleBackToExitPressedOnce;
    private Handler mHandler = new Handler();


    private static long back_pressed_time;
    private static long PERIOD = 2000;


    private String LOGIN_URL = "http://bara-spot.com/soogi/api/user/login";

    private static final String TAG_SUCCESS = "status";
    private static final String TAG_MESSAGE = "message";
    String tag_json_obj = "json_obj_req";
    String bara_token;
    private static final String TAG = LoginActivity.class.getSimpleName();
    Button loginButton;
    Boolean success;
    EditText txt_email, txt_password;

    String sudahLogin = "no";


    private String KEY_USERNAME = "username";
    private String KEY_PASSWORD = "password";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN); //show the activity in full screen
        setContentView(R.layout.activity_login);

        txt_email = (EditText) findViewById(R.id.input_email);
        txt_password = (EditText) findViewById(R.id.input_password);


        Context context = this.getApplicationContext();

        loginButton = (Button) findViewById(R.id.btn_login);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();


            }
        });

        if(sudahLogin.equals("ok")){
            //Context context = this.getApplicationContext();
            SharedPreferences settings = context.getSharedPreferences("BARA_SP", 0);
            SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean("isLogged", true);
            editor.commit();
        }
    }

    private void login(){
        Toast.makeText(LoginActivity.this, "Prepare Login", Toast.LENGTH_LONG).show();
        //menampilkan progress dialog
        final ProgressDialog loading = ProgressDialog.show(this, "Loading...", "Please wait...", false, false);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, LOGIN_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e(TAG, "Response: " + response.toString());

                        try {
                            JSONObject jObj = new JSONObject(response);
                            success = jObj.getBoolean(TAG_SUCCESS);
                            Log.i("v13nr",response);

                            if (success==true) {
                                Log.e("v Login", jObj.toString());

                                Toast.makeText(LoginActivity.this, jObj.getString(TAG_MESSAGE), Toast.LENGTH_LONG).show();
                                bara_token = jObj.getString("token");

                                    onLoginSuccess();


                            } else {
                                Toast.makeText(LoginActivity.this, "Login Gagal", Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        //menghilangkan progress dialog
                        loading.dismiss();


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //menghilangkan progress dialog
                        loading.dismiss();

                        //menampilkan toast
                        Toast.makeText(LoginActivity.this, "Login Gagal", Toast.LENGTH_LONG).show();
                        Log.i("v13", "Login Gagal");
                    }
                }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("X-Api-Key", "A8CB77BE8099B35AD0EAD724C4C36530");

                return params;
            }


            @Override
            protected Map<String, String> getParams() {
                //membuat parameters
                Map<String, String> params = new HashMap<String, String>();

                //menambah parameter yang di kirim ke web servis
                params.put(KEY_USERNAME, txt_email.getText().toString().trim());
                params.put(KEY_PASSWORD, txt_password.getText().toString().trim());

                //kembali ke parameters
                Log.e(TAG, "" + params);
                return params;
            }
        };

        AppController.getInstance().addToRequestQueue(stringRequest, tag_json_obj);


    }

    public void onLoginSuccess() {
        Context context = this.getApplicationContext();
        SharedPreferences settings = context.getSharedPreferences("BARA_SP", 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean("isLogged", true);
        editor.putString("bara_token", bara_token);
        editor.commit();

        startActivity(new Intent(LoginActivity.this, MainActivity.class));

        finish();
    }


    private final Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            doubleBackToExitPressedOnce = false;
        }
    };

    @Override
    protected void onDestroy()
    {
        super.onDestroy();

        if (mHandler != null) { mHandler.removeCallbacks(mRunnable); }
    }

    @Override
    public void onBackPressed() {
        if (back_pressed_time + PERIOD > System.currentTimeMillis()) super.onBackPressed();
        else Toast.makeText(getBaseContext(), "Press once again to exit!", Toast.LENGTH_SHORT).show();
        back_pressed_time = System.currentTimeMillis();

    }
}

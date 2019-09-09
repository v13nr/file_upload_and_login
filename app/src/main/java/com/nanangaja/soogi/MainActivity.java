package com.nanangaja.soogi;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.nanangaja.soogi.activity.LoginActivity;
import com.nanangaja.soogi.app.AppController;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private boolean doubleBackToExitPressedOnce = false;

    Button buttonChoose;
    ImageButton bt_getCode;
    FloatingActionButton buttonUpload;
    Toolbar toolbar;
    ImageView imageView;
    EditText txt_name, txt_bertugas, txt_deskripsi, txt_nopol, txt_driver, txt_muatan, txt_asal, txt_tujuan;
    Bitmap bitmap, decoded;
    int success;
    int PICK_IMAGE_REQUEST = 1;
    int bitmap_size = 60; // range 1 - 100

    private static final String TAG = MainActivity.class.getSimpleName();

    /* 10.0.2.2 adalah IP Address localhost Emulator Android Studio. Ganti IP Address tersebut dengan
    IP Address Laptop jika di RUN di HP/Genymotion. HP/Genymotion dan Laptop harus 1 jaringan! */
    //private String UPLOAD_URL = "http://bara-spot.com/android/upload_image/upload.php";
    private String UPLOAD_URL = "http://bara-spot.com/soogi/api/bukti_upload/add";
    private String DOWNLOAD_SJ = "http://bara-spot.com/android/upload_image/download_code.php";

    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";
//    private String KEY_IMAGE = "image";
//    private String KEY_NAME = "name";
//    private String KEY_BERTUGAS = "betugas";
//    private String KEY_DESKRIPSI = "deskripsi";
//    private String KEY_NOPOL = "nopol";
//    private String KEY_DRIVER = "driver";
//    private String KEY_MUATAN = "muatan";
//    private String KEY_ASAL = "asal";
//    private String KEY_TUJUAN = "tujuan";


    private String KEY_IMAGE = "photo";
    private String KEY_NAME = "name";
    private String KEY_BERTUGAS = "betugas";
    private String KEY_DESKRIPSI = "deskripsi";
    private String KEY_NOPOL = "nopol";
    private String KEY_DRIVER = "driver";
    private String KEY_MUATAN = "muatan_ton_m3";
    private String KEY_ASAL = "asal";
    private String KEY_TUJUAN = "muatan_tujuan";

    String tag_json_obj = "json_obj_req";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        buttonChoose = (Button) findViewById(R.id.buttonChoose);
        buttonUpload = (FloatingActionButton) findViewById(R.id.buttonUpload);
        ImageButton bt_getCode = (ImageButton) findViewById(R.id.id_search_button);
        txt_name = (EditText) findViewById(R.id.editText);
        txt_bertugas = (EditText) findViewById(R.id.editTextBertugas);
        txt_deskripsi = (EditText) findViewById(R.id.editTextDeskripsi);
        txt_nopol = (EditText) findViewById(R.id.editTextNopol);
        txt_driver = (EditText) findViewById(R.id.editTextDriver);
        txt_muatan = (EditText) findViewById(R.id.editTextMuatan);
        txt_asal = (EditText) findViewById(R.id.editTextAsal);
        txt_tujuan = (EditText) findViewById(R.id.editTextTujuan);

        imageView = (ImageView) findViewById(R.id.imageView);

        buttonChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFileChooser();
            }
        });

        buttonUpload.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                uploadImage();
            }
        });

        bt_getCode.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                getCode();
            }
        });

        Context context=this.getApplicationContext();
        SharedPreferences settings=context.getSharedPreferences("BARA_SP", 0);
        boolean isLogged=settings.getBoolean("isLogged", false);
        if(!isLogged){

            //Intent intent = new Intent(this, LoginActivity.class);
            //startActivity(intent);

            Intent i = new Intent(MainActivity.this, LoginActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(i);
            finish();

        }
    }

    public String getStringImage(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, bitmap_size, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

    private void uploadImage() {
        //menampilkan progress dialog
        final ProgressDialog loading = ProgressDialog.show(this, "Uploading...", "Please wait...", false, false);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, UPLOAD_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e(TAG, "Response: " + response.toString());

                        try {
                            JSONObject jObj = new JSONObject(response);
                            success = jObj.getInt(TAG_SUCCESS);

                            if (success == 1) {
                                Log.e("v Add", jObj.toString());

                                Toast.makeText(MainActivity.this, jObj.getString(TAG_MESSAGE), Toast.LENGTH_LONG).show();

                                kosong();

                            } else {
                                Toast.makeText(MainActivity.this, jObj.getString(TAG_MESSAGE), Toast.LENGTH_LONG).show();
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
                        Toast.makeText(MainActivity.this, "Upload Gagal", Toast.LENGTH_LONG).show();
                        Log.e(TAG, "Upload Gagal");
                    }
                }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {



                Map<String, String>  params = new HashMap<String, String>();
                params.put("X-Api-Key", "A8CB77BE8099B35AD0EAD724C4C36530");
                params.put("X-Token", getToken());

                return params;
            }



            @Override
            protected Map<String, String> getParams() {
                //membuat parameters
                Map<String, String> params = new HashMap<String, String>();

                //menambah parameter yang di kirim ke web servis
                params.put(KEY_IMAGE, getStringImage(decoded));
                params.put(KEY_NAME, txt_name.getText().toString().trim());
                params.put(KEY_DESKRIPSI, txt_deskripsi.getText().toString().trim());
                params.put(KEY_BERTUGAS, txt_bertugas.getText().toString().trim());
                params.put(KEY_NOPOL, txt_nopol.getText().toString().trim());
                params.put(KEY_DRIVER, txt_driver.getText().toString().trim());
                params.put(KEY_MUATAN, txt_muatan.getText().toString().trim());
                params.put(KEY_ASAL, txt_asal.getText().toString().trim());
                params.put(KEY_TUJUAN, txt_tujuan.getText().toString().trim());

                //kembali ke parameters
                Log.e(TAG, "" + params);
                return params;
            }
        };

        AppController.getInstance().addToRequestQueue(stringRequest, tag_json_obj);
    }

    public String getToken(){

        Context context=this.getApplicationContext();
        SharedPreferences settings=context.getSharedPreferences("BARA_SP", 0);
        String bara_token=settings.getString("bara_token", "");
        return bara_token;

    }


    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri filePath = data.getData();
            try {
                //mengambil fambar dari Gallery
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                // 512 adalah resolusi tertinggi setelah image di resize, bisa di ganti.
                setToImageView(getResizedBitmap(bitmap, 512));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void kosong() {
        imageView.setImageResource(0);
        txt_name.setText(null);
    }

    private void setToImageView(Bitmap bmp) {
        //compress image
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, bitmap_size, bytes);
        decoded = BitmapFactory.decodeStream(new ByteArrayInputStream(bytes.toByteArray()));

        //menampilkan gambar yang dipilih dari camera/gallery ke ImageView
        imageView.setImageBitmap(decoded);
    }

    // fungsi resize image
    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float) width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }

    public void getCode(){
        Toast.makeText(MainActivity.this, "Mendownload Kode Surat Jalan", Toast.LENGTH_LONG).show();
        //menampilkan progress dialog
        final ProgressDialog loading = ProgressDialog.show(this, "Downloading...", "Please wait...", false, false);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, DOWNLOAD_SJ,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e(TAG, "Response: " + response.toString());

                        try {
                            JSONObject jObj = new JSONObject(response);
                            success = jObj.getInt(TAG_SUCCESS);

                            if (success == 1) {
                                Log.e("v Get", jObj.toString());

                                Toast.makeText(MainActivity.this, jObj.getString(TAG_MESSAGE), Toast.LENGTH_LONG).show();

                                txt_name.setText(jObj.getString(TAG_MESSAGE));

                            } else {
                                Toast.makeText(MainActivity.this, jObj.getString(TAG_MESSAGE), Toast.LENGTH_LONG).show();
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
                        Toast.makeText(MainActivity.this, error.getMessage().toString(), Toast.LENGTH_LONG).show();
                        Log.e(TAG, error.getMessage().toString());
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                //membuat parameters
                Map<String, String> params = new HashMap<String, String>();

                //menambah parameter yang di kirim ke web servis
                params.put("proteksi", "sederhana");

                //kembali ke parameters
                Log.e(TAG, "" + params);
                return params;
            }
        };

        AppController.getInstance().addToRequestQueue(stringRequest, tag_json_obj);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }


    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_logout:
                // your code here
                Toast.makeText(MainActivity.this, "Logout...", Toast.LENGTH_LONG).show();
                Context context = this.getApplicationContext();
                SharedPreferences settings = context.getSharedPreferences("BARA_SP", 0);
                SharedPreferences.Editor editor = settings.edit();
                editor.putBoolean("isLogged", false);
                editor.putString("bara_token", "");
                editor.commit();

                Intent intent = new Intent(this,LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                finish();
                startActivity(intent);

                return true;
            case R.id.action_settings:
                // copied from auto-generated stub
                return true;
        }
        return super.onOptionsItemSelected(item);
    }



}

package com.example.syder;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.syder.databinding.ActivityMakeQRCodeBinding;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.util.HashMap;
import java.util.Map;

public class ActivityMakeQRCode extends AppCompatActivity {
    private ActivityMakeQRCodeBinding binding;
    private static final String TAG =  "MainActivity";
    private RequestQueue requestQueue;
    private String response_QRcode;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestQueue = Volley.newRequestQueue(this);
        binding = ActivityMakeQRCodeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.buttonQRcode.setOnClickListener(v->{
            QRCodecheck();
        });

    }


    public void QRCodecheck(){
        String url = "http://13.124.189.186/api/orders/1/orderAuth?orderId="+ActivityLogin.orderId+"&userId="+ ActivitySend.receiverID+"&userCategory=sender&guard=user";


        StringRequest request = new StringRequest(Request.Method.GET, url, new com.android.volley.Response.Listener<String>() {

            public void onResponse(String response) {
                response_QRcode = response;
                Log.d(TAG,"응답" + response_QRcode);
                Log.d(TAG, "응답 : " +ActivityLogin.orderId + ActivitySend.receiverID);
                QRCodeButton();
            }
        },new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse response = error.networkResponse;
                String jsonError = new String(response.data);
                response_QRcode = jsonError;
                QRCodeButton();
                Log.d(TAG, "에러" + response_QRcode);
            }
        }
        ){
            public Map getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", "Bearer " + ActivityLogin.loginResponse);
                return params;
            }
        };
        request.setShouldCache(false);
        requestQueue.add(request);
        Log.i(TAG,"요청 보냄.");
    }

    public void QRCodeButton() {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();

        try {
            BitMatrix bitMatrix = qrCodeWriter.encode(response_QRcode, BarcodeFormat.QR_CODE, 200, 200);
            Bitmap bitmap = Bitmap.createBitmap(200, 200, Bitmap.Config.RGB_565);

            for (int x = 0; x < 200; x++) {
                for (int y = 0; y < 200; y++) {
                    bitmap.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                }
            }
            binding.imageView.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }
}

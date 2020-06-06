package com.example.syder;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ActivityScanQR extends AppCompatActivity {
    private IntentIntegrator qrScan;
    private int id;
    static int flag;
    private static String TAG = "activity_scan_qr";
    private RequestQueue requestQueue;
    private String url;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_q_r_activity);

        requestQueue = Volley.newRequestQueue(this);
        Intent i = getIntent();
        id = i.getIntExtra("intent", 1);
        Log.d("intent_data_ ", "" + id);
        qrScan = new IntentIntegrator(this);
        qrScan.setOrientationLocked(false);
        qrScan.setPrompt("QR코드를 인식해주세요.");
        qrScan.initiateScan();
    }

    public void QRCodecheck(){
//        url = "http://13.124.189.186/api/orders/5/orderAuth?orderId="+ActivityLogin.userId +"&userId="+ ActivitySend.receiverID+"&userCategory=sender&guard=user";
        url += "1&user_id=2&user_category=sender&guard=user";

        StringRequest request = new StringRequest(Request.Method.GET, url, new com.android.volley.Response.Listener<String>() {
            public void onResponse(String response) {
                boolean result = false;
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    result = jsonResponse.getBoolean("result");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if(result) {
                    if(id == 1) {
                        Intent intent = new Intent(ActivityScanQR.this, ActivityOrderStart.class);
                        startActivity(intent);
                    }else if(id == 2) {
                        Intent intent = new Intent(ActivityScanQR.this, ActivityOrderEnd.class);
                        startActivity(intent);
                    }
                    finish();
                }else {
                    if(id == 1) {
                        Intent intent = new Intent(ActivityScanQR.this, ActivityOrderStart.class);
                        startActivity(intent);
                    }else if(id == 2) {
                        Intent intent = new Intent(ActivityScanQR.this, ActivityOrderEnd.class);
                        flag = 1;
                        startActivity(intent);
                    }
                    finish();
//                    Intent intent = new Intent(ActivityScanQR.this, ActivityWait.class);
//                    startActivity(intent);
                }
                Log.d(TAG, "응답 : " +ActivityLogin.userId + ActivitySend.receiverID);
            }
        },new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse response = error.networkResponse;
                String jsonError = new String(response.data);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        if(result != null){
            if(result.getContents() == null){
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(this, ActivityWait.class);
                startActivity(intent);
            }else{
                Toast.makeText(this, "QR코드 스캔이 완료되었습니다.", Toast.LENGTH_LONG).show();
                try {
                    url = result.getContents();
                    QRCodecheck();
                    Log.d("응답", "QR코드 응답상태 : " + url);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

//            if(qrcode_Result == true){
//                if(id == 1) {
//                    Intent intent = new Intent(this, ActivityOrderStart.class);
//                    startActivity(intent);
//                }else if(id == 2) {
//                    Intent intent = new Intent(this, ActivityOrderEnd.class);
//                    startActivity(intent);
//                }
//                finish();
//
//            }else{
//                if(id == 1) {
//                    Intent intent = new Intent(this, ActivityOrderStart.class);
//                    startActivity(intent);
//                }else if(id == 2) {
//                    Intent intent = new Intent(this, ActivityOrderEnd.class);
//                    flag = 1;
//                    startActivity(intent);
//                }
//                finish();
//            }
        }else{
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}

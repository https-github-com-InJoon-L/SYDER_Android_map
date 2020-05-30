package com.example.syder;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.syder.databinding.ActivitySendWaitBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ActivityWait extends AppCompatActivity {
    private ActivitySendWaitBinding binding;
    private static final String TAG = "activity_wait";
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySendWaitBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Bundle bundle = getIntent().getExtras();
        if(bundle != null) {
            String fcmTokend = (String) bundle.get("receiver_fcm_token");
            Log.d(TAG, "wait fcm" + fcmTokend);
        }
        requestQueue =  Volley.newRequestQueue(this);
        binding.buttonQRcodeCheck.setOnClickListener(v->{
            authCheck();
        });

        binding.buttonMakeQRCode.setOnClickListener(v->{
            Intent intent = new Intent (this, ActivityMakeQRCode.class);
            startActivity(intent);
        });
    }

    public void authCheck() {
        //ActivityLogin.orderId
        String url = "http://13.124.189.186/api/authCheck?guard=user";
        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            public void onResponse(String response) {
                int authId = 0;
                Log.d(TAG,"auth 체크" + response);
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    authId = jsonResponse.getInt("id");
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e(TAG, "auth 체크 에러 하이고" );
                }
                if(authId == ActivityLogin.userId) {
                    startActivity(new Intent(getApplicationContext(), ActivityScanQR.class));
                    finish();
                }
            }
        },new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG,"auth 체크 에러 -> " + error.getMessage());
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization" , "Bearer " + ActivityLogin.loginResponse);
                return params;
            }
        };

        request.setShouldCache(false);
        requestQueue.add(request);
        Log.d(TAG, "auth 체크 요청보냄");
    }
}
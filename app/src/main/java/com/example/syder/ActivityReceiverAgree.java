package com.example.syder;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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
import com.example.syder.databinding.ActivityReceiverAgreeBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ActivityReceiverAgree extends AppCompatActivity {
    private ActivityReceiverAgreeBinding binding;
    private RequestQueue requestQueue;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestQueue = Volley.newRequestQueue(this);
        binding = ActivityReceiverAgreeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnAgreeTrue.setOnClickListener(v->{
            agreeResult();
        });
    }

    public void agreeResult() {

        String url = "http://13.124.189.186/api/consent/response?order_id=3&consent_or_not=0&guard=user";

        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {

            public void onResponse(String response) {
                Log.d("FCM알림", "응답" + response);
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    String jsonAgree = jsonResponse.getString("message");
                    String jsonReciever_token = jsonResponse.getString("reciever_token");
                    Log.d("FCM알림", "동의요청" + jsonAgree);
                    Log.d("FCM알림", "토큰" + jsonReciever_token);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse response = error.networkResponse;
                String jsonError = new String(response.data);
                Log.d("FCM알림", "에러" + jsonError);
            }
        }) {
            public Map getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", "Bearer " + ActivityLogin.loginResponse);
                return params;
            }
        };
        request.setShouldCache(false);
        requestQueue.add(request);
        Log.i("FCM알림", "요청 보냄.");
    }
}

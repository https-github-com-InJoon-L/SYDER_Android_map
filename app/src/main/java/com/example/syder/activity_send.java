package com.example.syder;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.syder.databinding.ActivitySendBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class activity_send extends AppCompatActivity {
    private static final String TAG =  "activity_send";
    private ActivitySendBinding binding;
    private RequestQueue requestQueue;
    static String receiverName;
    static int receiverID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestQueue = Volley.newRequestQueue(this);
        binding = ActivitySendBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.checkNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkSender();
                Log.d("응답", receiverName + receiverID);
                binding.senderName.setText(receiverName);
            }
        });

    }


    public void checkSender(){
        String phoneNumber = binding.senderPhonenumber.getText().toString();
        String url = "http://13.124.189.186/api/user/request?phone="+phoneNumber+"&guard=user";

        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {

            public void onResponse(String response) {
                Log.d(TAG,"응답" + response);
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    JSONArray jsonReceiver = jsonResponse.getJSONArray("receiver");
                    String receiverArray = jsonReceiver.getString(0);
                    JSONObject jsonName = new JSONObject(receiverArray);
                    int getID = jsonName.getInt("id");
                    String getName = jsonName.getString("name");
                    receiverID = getID;
                    receiverName = getName;
                    Log.d(TAG,"응답 : ID - " + receiverID + " Name - " + receiverName);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        },new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse response = error.networkResponse;
                String jsonError = new String(response.data);
                Log.d(TAG, "에러" + jsonError);
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
}
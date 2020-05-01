package com.example.syder;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.JsonWriter;
import android.util.Log;
import android.view.View;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.syder.databinding.ActivityLoginBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.HashMap;
import java.util.Map;

public class ActivityLogin extends AppCompatActivity {
    private static final String TAG =  "activity_login";
    private ActivityLoginBinding binding;
    static RequestQueue requestQueue;
    static String url = "http://13.124.189.186/api/login";

    static String loginResponse;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ActivityLogin.this, ActivityRegister.class);
                startActivity(intent);
            }
        });

        binding.buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeRequest();

            }
        });
        if(requestQueue == null){
            requestQueue = Volley.newRequestQueue(getApplicationContext());
        }

    }

    public void makeRequest() {

        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {


            public void onResponse(String response) {
                Log.d(TAG,"응답" + response);
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    String getToken = jsonResponse.getString("access_token");

                    JSONObject jsonUser = jsonResponse.getJSONObject("user");
                    String account = jsonUser.getString("account");
                    String name = jsonUser.getString("name");
                    String email = jsonUser.getString("email");
                    String phone = jsonUser.getString("phone");

                    loginResponse = getToken;

                    Log.i(TAG,"account : " + account + "name : " + name + "email : " + email + "phone : " + phone);
                    Log.i(TAG,"token 값 응답 : " + loginResponse);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Intent intent = new Intent(ActivityLogin.this, MainActivity.class);
                startActivity(intent);
            }
        },new Response.ErrorListener(){

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG,"에러 ->" + error.getMessage());
            }
        }
        ){
            protected Map<String, String> getParams() throws AuthFailureError{
                Map<String, String> params = new HashMap<String, String>();
                params.put("account", binding.loginID.getText().toString());
                params.put("password",  binding.loginPassword.getText().toString());
                params.put("provider", "users");
                return params;
            }
        };

        request.setShouldCache(false);
        requestQueue.add(request);
        Log.i(TAG,"요청 보냄.");

    }

    String getLoginResponse() {
        return loginResponse;
    }

}

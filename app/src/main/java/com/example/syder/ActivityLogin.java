package com.example.syder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.syder.databinding.ActivityLoginBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ActivityLogin extends AppCompatActivity {
    private static final String TAG =  "activity_login";
    private ActivityLoginBinding binding;
    static RequestQueue requestQueue;
    static String url = "http://13.124.189.186/api/login";
    static int userId;
    static String loginResponse;
    static String FCMtoken;
    static String name;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.loginRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ActivityLogin.this, ActivityRegister.class);
                startActivity(intent);
            }
        });



        if (getIntent().getExtras() != null) {
            for (String key : getIntent().getExtras().keySet()) {
                Object value = getIntent().getExtras().get(key);
                Log.d("TAG: ", "Key: " + key + " Value: " + value);
            }
        }else {
            Log.d(TAG, "왜 못불러오냐");
        }

        binding.buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeRequest();

            }
        });
        if(requestQueue == null){
            requestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.i("FCM", "getInstanceId failed", task.getException());
                            return;
                        }

                        // Get new Instance ID token
                        FCMtoken = task.getResult().getToken();

                        // Log and toast
                        String msg = getString(R.string.msg_token_fmt, FCMtoken);
                        Log.i("FCM", msg);
                        Toast.makeText(ActivityLogin.this, msg, Toast.LENGTH_SHORT).show();
                    }
                });
        FirebaseMessaging.getInstance().setAutoInitEnabled(true);
    }

    public void makeRequest() {

        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {


            public void onResponse(String response) {
                Log.d(TAG,"응답" + response);
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    String getToken = jsonResponse.getString("access_token");

                    JSONObject jsonUser = jsonResponse.getJSONObject("user");
                    int getId = jsonUser.getInt("id");
                    String account = jsonUser.getString("account");
                    name = jsonUser.getString("name");
                    String email = jsonUser.getString("email");
                    String phone = jsonUser.getString("phone");

                    loginResponse = getToken;
                    userId = getId;

                    Log.i(TAG,"account : " + account + "name : " + name + "email : " + email + "phone : " + phone);
                    Log.i(TAG,"token 값 응답 : " + loginResponse);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Intent intent = new Intent(ActivityLogin.this, ActivityWaypoint.class);
                startActivity(intent);
                //------------------------------------------------------------------------------
//                String activityName = "";
//                String orderName = "";
//                try {
//                    Intent getActivity = getIntent();
//                    activityName = Objects.requireNonNull(getActivity.getExtras()).getString("activity_name");
//                    orderName = getActivity.getExtras().getString("order_name");
//                }catch (Exception e) {
//                    e.printStackTrace();
//                }
//                Intent intent = new Intent(ActivityLogin.this, ActivityWaypoint.class);
//                intent.putExtra("activity_name" , activityName);
//                intent.putExtra("order_name", orderName);
//                startActivity(intent);
//                finish();
                //-------------------------------------------------------------------------------
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
                params.put("fcm_token", FCMtoken);
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

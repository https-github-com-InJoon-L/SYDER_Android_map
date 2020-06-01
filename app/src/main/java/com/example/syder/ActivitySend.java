package com.example.syder;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.syder.databinding.ActivitySendBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ActivitySend extends AppCompatActivity {
    private static final String TAG = "activity_send";
    private ActivitySendBinding binding;
    private RequestQueue requestQueue;
    private String moveNeedSelect;
    private String routeReverse;
    private String routeID;
    static String receiverName;
    static int receiverID;
    static int orderID;
    static String createdAt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestQueue = Volley.newRequestQueue(this);
        binding = ActivitySendBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        moveNeedSelect = MainActivity.moveNeed ? "1" : "0";
        routeReverseChecking();
        binding.buttonCheckNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkSender();
            }
        });

        binding.callButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                orderRequest();
//                authCheck();
            }
        });
    }

    public void routeReverseChecking() {
        for(int i = 0; i < MainActivity.routeList.size(); i++) {
            if (MainActivity.startingId.equals(MainActivity.routeList.get(i).getStartingId()) &&
                    MainActivity.arrivalId.equals(MainActivity.routeList.get(i).getArrivalPoint())) {
                routeReverse = "0";
                routeID = MainActivity.routeList.get(i).getRouteId();
                break;
            }else if(MainActivity.startingId.equals(MainActivity.routeList.get(i).getArrivalPoint()) &&
                    MainActivity.arrivalId.equals(MainActivity.routeList.get(i).getStartingId())) {
                routeReverse = "1";
                routeID = MainActivity.routeList.get(i).getRouteId();
                break;
            }
        }
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
                    Intent intent = new Intent(ActivitySend.this, ActivityOrdering.class);
                    startActivity(intent);
                }
            }
        },new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG,"auth 체크 에러 -> " + error.getMessage());
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError{
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization" , "Bearer " + ActivityLogin.loginResponse);
                return params;
            }
        };

        request.setShouldCache(false);
        requestQueue.add(request);
        Log.d(TAG, "auth 체크 요청보냄");
    }

    public void sendAgree() {

        String url = "http://13.124.189.186/api/consent/request?order_id=" + orderID + "&guard=user";

        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {

            public void onResponse(String response) {
                Log.d(TAG, "순서대로 " + response);
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    String jsonAgree = jsonResponse.getString("message");
                    Log.d(TAG, "동의요청" + jsonAgree);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                new CountDownTimer(1000, 1000) {

                    public void onTick(long millisUntilFinished) {
                    }

                    public void onFinish() {
                        Intent intent = new Intent(ActivitySend.this, ActivitySending.class);
                        startActivity(intent);
                    }
                }.start();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse response = error.networkResponse;
                String jsonError = new String(response.data);
                Log.d(TAG, "에러" + jsonError);
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
        Log.i(TAG, "요청 보냄.");
    }

    public void orderRequest() {
        String url = "http://13.124.189.186/api/orders";

        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "동의요청 보내면서 주문 정보 보내기");
                Log.d("주문등록 ", "" + receiverID);
                Log.d("주문등록 ", "" + MainActivity.cartId);
                Log.d("주문등록 ", "" + routeID);
                Log.d("주문등록 ", "" + routeReverse);
                Log.d("주문등록 ", "" + moveNeedSelect);
                Log.d("주문등록 ", "" + MainActivity.moveNeedRoute);
                Log.d(TAG, "순서대로 " + response);
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    JSONObject jsonOrder = jsonResponse.getJSONObject("order");
                    orderID = jsonOrder.getInt("id");
                    String senderId = jsonOrder.getString("sender");
                    String receiver = jsonOrder.getString("receiver");
                    createdAt = jsonOrder.getString("created_at");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.d(TAG, "order id: " + orderID);
                sendAgree();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        }) {
            protected Map<String, String> getParams() throws AuthFailureError{
                Map<String, String> params = new HashMap<String, String>();
                params.put("receiver", "5");
                params.put("order_availability", "1");
                params.put("order_cart", "1");
                params.put("order_route", "1");
                params.put("reverse_direction", "1");
                params.put("cartMove_needs", "1");
                params.put("cartMove_route", "1");
                params.put("guard", "user");
//                params.put("receiver", "" + receiverID);
//                params.put("order_cart", MainActivity.cartId);
//                params.put("order_route", routeID);
//                params.put("reverse_direction", routeReverse);
//                params.put("cartMove_needs", "" + moveNeedSelect);
//                if(moveNeedSelect.equals("1")) {
//                    params.put("cartMove_route", MainActivity.moveNeedRoute);
//                }else {
//                    params.put("cartMove_route", routeID);
//                }
                return params;
            }

            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", "Bearer " + ActivityLogin.loginResponse);

                return params;
            }
        };
        request.setShouldCache(false);
        requestQueue.add(request);

        Log.i(TAG,"주문요청 보냄.");
    }

    public void checkSender() {
        String phoneNumber = binding.senderPhonenumber.getText().toString();
        String url = "http://13.124.189.186/api/user/request?phone=" + phoneNumber + "&guard=user";

        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {

            public void onResponse(String response) {
                Log.d(TAG, "응답" + response);
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    JSONObject jsonReceiver = jsonResponse.getJSONObject("receiver");
                    int getID = jsonReceiver.getInt("id");
                    String getName = jsonReceiver.getString("name");
                    receiverID = getID;
                    receiverName = getName;
                    Log.d(TAG, "응답 : ID - " + receiverID + " Name - " + receiverName);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.d("응답", receiverName + receiverID);
                binding.senderName.setText(receiverName);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse response = error.networkResponse;
                String jsonError = new String(response.data);
                Log.d(TAG, "에러" + jsonError);
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
        Log.i(TAG, "요청 보냄.");
    }
}
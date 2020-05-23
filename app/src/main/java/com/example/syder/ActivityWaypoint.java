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
import com.example.syder.databinding.ActivityWaypointBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ActivityWaypoint extends AppCompatActivity {

    private static String TAG = "waypoint_activity";
    private ActivityWaypointBinding binding;
    private RequestQueue requestQueue;
    private boolean flag = true;
    static JSONArray jsonWaypointArray;
    static JSONArray jsonRouteArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityWaypointBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        requestQueue =  Volley.newRequestQueue(this);
        waypointRequest();
        routeRequest();
        orderCheck();

        Intent intent = new Intent(ActivityWaypoint.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void waypointRequest() {
        String url = "http://13.124.189.186/api/waypoints?guard=user";
        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            public void onResponse(String response) {
                Log.d(TAG,"맵" + response);
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    jsonWaypointArray = jsonResponse.getJSONArray("waypoints");
                    Log.d(TAG, "d" + jsonWaypointArray.length());
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e(TAG, "에러 하이고" );
                }
            }
        },new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG,"에러 -> " + error.getMessage());
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
        Log.d(TAG, "way요청보냄");
    }

    public void routeRequest() {
        String url = "http://13.124.189.186/api/routes?guard=user";
        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            public void onResponse(String response) {
                Log.d(TAG,"경로" + response);
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    jsonRouteArray = jsonResponse.getJSONArray("routes");

                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e(TAG, "경로에러 하이고" );
                }
            }
        },new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG,"경로에러 -> " + error.getMessage());
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
        Log.d(TAG, "route요청보냄");
    }

    public void orderCheck() {
        String url = "http://13.124.189.186/api/orders/check?guard=user";
        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            public void onResponse(String response) {
                Log.d(TAG,"주문쳌" + response);
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    JSONArray jsonArrays = jsonResponse.getJSONArray("order");
                    flag = jsonResponse.getBoolean("availability");
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e(TAG, "주문쳌에러 하이고" );
                }
                if(!flag) {
                    Intent intent = new Intent(ActivityWaypoint.this, ActivityOrdering.class);
                    startActivity(intent);
                    finish();
                }else {
                    Intent intent = new Intent(ActivityWaypoint.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        },new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG,"에러 -> " + error.getMessage());
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
        Log.d(TAG, "route요청보냄");
    }
}
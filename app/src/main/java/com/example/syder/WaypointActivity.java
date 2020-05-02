package com.example.syder;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

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

public class WaypointActivity extends AppCompatActivity {

    private static String TAG = "waypoint_activity";
    private ActivityWaypointBinding binding;
    private RequestQueue requestQueue;
    static ArrayList<String> wayName = new ArrayList<String>();
    static ArrayList<Double> wayLat = new ArrayList<Double>();
    static ArrayList<Double> wayLng = new ArrayList<Double>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityWaypointBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        requestQueue =  Volley.newRequestQueue(this);
        makeRequest();
        Intent intent = new Intent(WaypointActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void makeRequest() {
        String url = "http://13.124.189.186/api/waypoints";
        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            public void onResponse(String response) {
                Log.d(TAG,"맵" + response);
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    JSONArray jsonWaypointArray = jsonResponse.getJSONArray("waypoints");
                    String getName;
                    for (int i = 0; i < jsonWaypointArray.length(); i++) {
                        JSONObject result = jsonWaypointArray.getJSONObject(i);
                        wayName.add(i, result.getString("name"));
                        wayLat.add(i, result.getDouble("lat"));
                        wayLng.add(i, result.getDouble("lng"));
                        Log.d(TAG, "좌표제목 : " + wayName.get(i));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e(TAG, "에러 하이고" );
                }
            }
        },new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG,"에러 ->" + error.getMessage());
            }
        });
        request.setShouldCache(false);
        requestQueue.add(request);
        Log.d(TAG, "way요청보냄");
    }
}

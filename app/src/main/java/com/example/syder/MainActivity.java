package com.example.syder;

import androidx.annotation.NonNull;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.syder.databinding.ActivityMainBinding;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends FragmentActivity implements OnMapReadyCallback {
    private ActivityMainBinding binding;
    private static final String TAG =  "activity_main";
    private GoogleMap           mMap;
    private SupportMapFragment  mapFragment;
    private RequestQueue mQueue;
    ArrayList<LatLng> mMarkerPoints;
    private DrawerLayout drawerLayout;
    private View drawerView;
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        requestQueue =  Volley.newRequestQueue(this);
        drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        drawerView = (View) findViewById(R.id.drawer);

        ImageView menu_open = (ImageView)findViewById(R.id.menu_open);

        menu_open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(drawerView);
            }
        });


        mQueue = Volley.newRequestQueue(this);

        drawerLayout.setDrawerListener(listener);
        drawerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

        Button menu_close = (Button)findViewById(R.id.menu_close);
        menu_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.closeDrawers();
            }
        });

        Button logout = (Button) findViewById(R.id.button_logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
                Log.i(TAG,"토큰 값" + ActivityLogin.loginResponse);
                ActivityLogin.loginResponse = null;
            }
        });
        mapFragment = (SupportMapFragment)getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mMarkerPoints = new ArrayList<>();

    }

    public void logout(){
        String url = "http://13.124.189.186/api/logout";

        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            public void onResponse(String response) {
                Log.d(TAG,"응답" + response);

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
                params.put("guard", "user");
                return params;
            }
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
    public void onMapReady(GoogleMap googleMap) {
        ArrayList<String> title = new ArrayList<String>();
        ArrayList<Double> lat   = new ArrayList<Double>();
        ArrayList<Double> lng   = new ArrayList<Double>();

        String url = "http://13.124.189.186/api/waypoints";
        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            public void onResponse(String response) {
                Log.d(TAG,"맵" + response);
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    JSONArray jsonWaypointArray = jsonResponse.getJSONArray("waypoints");

                    for (int i = 0; i < jsonWaypointArray.length(); i++) {
                        JSONObject result = jsonWaypointArray.getJSONObject(i);
                        title.add(i, result.getString("name"));
                        lat.add(i, result.getDouble("lat"));
                        lng.add(i, result.getDouble("lng"));
                    }
                    Log.d(TAG, "좌표 " + title.get(1) + lat.get(1) + lng.get(1));

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
        Log.i(TAG,"맵");

        mMap = googleMap;
        LatLng YJU = new LatLng(35.896274, 128.621827);
//        LatLng YJU_library      = new LatLng(lat.get(4),lng.get(4));
//        LatLng YJU_engineering  = new LatLng(35.896353,128.621822);
//        LatLng YJU_Yeonsogwan   = new LatLng(lat.get(2),lng.get(2));
//        LatLng YJU_mainbuilding = new LatLng(lat.get(0),lng.get(0));
//        LatLng YJU_frontgate    = new LatLng(lat.get(1),lng.get(1));
//        LatLng YJU_backgate     = new LatLng(lat.get(3),lng.get(3));

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(YJU, 17));
//        mMap.addMarker(new MarkerOptions().position(YJU_library).title(title.get(4)));
//        mMap.addMarker(new MarkerOptions().position(YJU_engineering).title("공학관"));
//        mMap.addMarker(new MarkerOptions().position(YJU_Yeonsogwan).title(title.get(2)));
//        mMap.addMarker(new MarkerOptions().position(YJU_mainbuilding).title(title.get(0)));
//        mMap.addMarker(new MarkerOptions().position(YJU_frontgate).title(title.get(1)));
//        mMap.addMarker(new MarkerOptions().position(YJU_backgate).title(title.get(3)));

        mMap.moveCamera(CameraUpdateFactory.newLatLng(YJU));


    }

    DrawerLayout.DrawerListener listener = new DrawerLayout.DrawerListener() {
        @Override
        public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {

        }
        @Override
        public void onDrawerOpened(@NonNull View drawerView) {

        }
        @Override
        public void onDrawerClosed(@NonNull View drawerView) {

        }
        @Override
        public void onDrawerStateChanged(int newState) {

        }
    };

}

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
        ImageButton callWay = (ImageButton)findViewById(R.id.imageButton);

        callWay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getWaypoint();
            }
        });
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

    public void getWaypoint() {
        String url = "http://13.124.189.186/api/waypoints";
        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            public void onResponse(String response) {
                Log.d(TAG,"맵" + response);
                try {
                    JSONObject jsonResponse = new JSONObject(response);

                    JSONObject jsonWaypoints = jsonResponse.getJSONObject("waypoints");


                } catch (JSONException e) {
                    e.printStackTrace();
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
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng YJU = new LatLng(35.896274, 128.621827);
        LatLng YJU_library      = new LatLng(35.895283,128.622631);
        LatLng YJU_engineering  = new LatLng(35.896353,128.621822);
        LatLng YJU_Yeonsogwan   = new LatLng(35.896731,128.622876);
        LatLng YJU_mainbuilding = new LatLng(35.896511,128.620931);
        LatLng YJU_frontgate    = new LatLng(35.895252,128.623585);
        LatLng YJU_backgate     = new LatLng(35.896240,128.620180);

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(YJU, 17));
        mMap.addMarker(new MarkerOptions().position(YJU_library).title("도서관"));
        mMap.addMarker(new MarkerOptions().position(YJU_engineering).title("공학관"));
        mMap.addMarker(new MarkerOptions().position(YJU_Yeonsogwan).title("연서관"));
        mMap.addMarker(new MarkerOptions().position(YJU_mainbuilding).title("본관"));
        mMap.addMarker(new MarkerOptions().position(YJU_frontgate).title("정문"));
        mMap.addMarker(new MarkerOptions().position(YJU_backgate).title("후문"));

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

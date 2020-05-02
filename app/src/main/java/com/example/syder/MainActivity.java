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

import java.lang.reflect.Array;
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

        mMap = googleMap;
        LatLng YJU = new LatLng(35.896274, 128.621827);
        LatLng YJU_library      = new LatLng(WaypointActivity.wayLat.get(4),WaypointActivity.wayLng.get(4));
//        LatLng YJU_engineering  = new LatLng(35.896353,128.621822);
        LatLng YJU_Yeonsogwan   = new LatLng(WaypointActivity.wayLat.get(2),WaypointActivity.wayLng.get(2));
        LatLng YJU_mainbuilding = new LatLng(WaypointActivity.wayLat.get(0),WaypointActivity.wayLng.get(0));
        LatLng YJU_frontgate    = new LatLng(WaypointActivity.wayLat.get(1),WaypointActivity.wayLng.get(1));
        LatLng YJU_backgate     = new LatLng(WaypointActivity.wayLat.get(3),WaypointActivity.wayLng.get(3));

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(YJU, 17));
        mMap.addMarker(new MarkerOptions().position(YJU_library).title(WaypointActivity.wayName.get(4)));
//        mMap.addMarker(new MarkerOptions().position(YJU_engineering).title("공학관"));
        mMap.addMarker(new MarkerOptions().position(YJU_Yeonsogwan).title(WaypointActivity.wayName.get(2)));
        mMap.addMarker(new MarkerOptions().position(YJU_mainbuilding).title(WaypointActivity.wayName.get(0)));
        mMap.addMarker(new MarkerOptions().position(YJU_frontgate).title(WaypointActivity.wayName.get(1)));
        mMap.addMarker(new MarkerOptions().position(YJU_backgate).title(WaypointActivity.wayName.get(3)));

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

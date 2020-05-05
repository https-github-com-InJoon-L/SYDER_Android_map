package com.example.syder;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
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
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {
    private ActivityMainBinding binding;
    private static final String TAG =  "activity_main";
    private GoogleMap           mMap;
    private SupportMapFragment  mapFragment;
    private RequestQueue mQueue;
    ArrayList<LatLng> mMarkerPoints;
    private DrawerLayout drawerLayout;
    private View drawerView;
    private RequestQueue requestQueue;
    private TextView startPoint;
    private TextView endPoint;
    private TextView timeAttack;
    private TextView tiemResult;
    private LinearLayout deliveryInfo;
    private Marker selectedMarker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        requestQueue =  Volley.newRequestQueue(this);
        drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        drawerView = (View) findViewById(R.id.drawer);
        startPoint = findViewById(R.id.startPoint);
        endPoint = findViewById(R.id.endPoint);
        timeAttack = findViewById(R.id.timeAttack);
        tiemResult = findViewById(R.id.timeResult);
        deliveryInfo = findViewById(R.id.deliveryInfo);
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

        StringRequest logoutRequest = new StringRequest(Request.Method.POST, url,
                response->{
                    Log.i(TAG, response);
                    Toast.makeText(getApplicationContext(), "로그아웃 받아옴", Toast.LENGTH_SHORT).show();
                },
                error -> Log.i(TAG, error.getMessage())
        ) {
            protected Map<String, String> getParams() throws AuthFailureError{
                Map<String, String> params = new HashMap<String, String>();
                params.put("guard" , "user");
                return params;
            }

            public Map getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", "Bearer " + ActivityLogin.loginResponse);

                return params;
            }

        };

        logoutRequest.setShouldCache(false);
        Log.i(TAG,"request" + logoutRequest);
        requestQueue.add(logoutRequest);
        Log.i(TAG,"요청 보냄.");

        Log.i(TAG,"토큰 값" + ActivityLogin.loginResponse);
        ActivityLogin.loginResponse = null;

        Intent intent = new Intent(MainActivity.this, ActivityLogin.class);
        startActivity(intent);
    }

    //addMarker 재수정
    public Marker addMarker(MarkerModel markerModel, boolean isSelectedMarker) {
        LatLng position = new LatLng(markerModel.getLat(), markerModel.getLng());
        String title = markerModel.getTitle();
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.title(title);
        markerOptions.position(position);
        // 클릭시 아이콘 배치할겨
//        if(isSelectedMarker) {
//            markerOptions.icon(BitmapDescriptorFactory.fromResource());
//        }else {
//            markerOptions.icon(BitmapDescriptorFactory.fromResource());
//        }
        return mMap.addMarker(markerOptions);
    }
    // 서버에서 받아온 데이터로 마커 생성
    private void getMarkerItems() {
        ArrayList<MarkerModel> list = new ArrayList<MarkerModel>();
        try {
            for (int i = 0; i < WaypointActivity.jsonWaypointArray.length(); i++) {
                JSONObject result = WaypointActivity.jsonWaypointArray.getJSONObject(i);
                list.add(new MarkerModel(result.getDouble("lat"), result.getDouble("lng"),
                        result.getString("name")));
            }
        }catch (JSONException e) {
            e.printStackTrace();
            Log.e(TAG, "에러 하이고" );
        }


        for (MarkerModel markerModel : list) {
            addMarker(markerModel, false);
        }
    }
    // 표시 마커 동작
    private void changeSelectedMarker(Marker marker) {

        // 선택했던 마커 되돌리기
        if(selectedMarker != null) {
            addMarker(new MarkerModel(selectedMarker.getPosition().latitude, selectedMarker.getPosition().longitude,
                    selectedMarker.getTitle()), false);
            selectedMarker.remove();
        }
        //선택한 마커 표시
        if(marker != null) {
            selectedMarker = addMarker(new MarkerModel(marker.getPosition().latitude, marker.getPosition().longitude,
                    marker.getTitle()), true);
            marker.remove();


        }
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        getMarkerItems();

        //-----------------------------------------------------------------------
        LatLng YJU = new LatLng(35.896274, 128.621827);

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(YJU, 17));
        //크기를 지정해서 비트맵으로 만들기 자동차
        Bitmap bit = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.driving),
                60, 60, false);
        mMap.addMarker(new MarkerOptions().position(YJU).icon(BitmapDescriptorFactory.fromBitmap(bit)));
        //마커 클릭에 대한 이벤트 처리
        mMap.setOnMarkerClickListener(this);

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

    @Override
    public boolean onMarkerClick(Marker marker) {
        changeSelectedMarker(marker);
        deliveryInfo.setVisibility(View.VISIBLE);
        //marker.getId()는 마커생성 순서
        startPoint.setText(marker.getTitle());
        Log.d(TAG, "선택 -> " + selectedMarker + " 마커 -> " + marker);
        return false;
    }
}

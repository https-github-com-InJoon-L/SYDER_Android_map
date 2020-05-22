package com.example.syder;

import androidx.annotation.NonNull;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.syder.databinding.ActivityMainBinding;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import io.socket.client.Socket;

public class MainActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {
    private ActivityMainBinding binding;
    private static final String TAG =  "activity_main";
    private GoogleMap           mMap;
    private SupportMapFragment  mapFragment;
    private RequestQueue mQueue;
    private DrawerLayout drawerLayout;
    private View drawerView;
    private RequestQueue requestQueue;
    private TextView startPoint;
    private TextView endPoint;
    private Socket mSocket;
    private TextView timeAttack;
    private TextView tiemResult;
    private LinearLayout deliveryInfo;
    private ArrayList<MarkerModel> markersInfo = new ArrayList<MarkerModel>();
    static int selectedCount = 0;
    static String[] selectedTitle = new String[2];


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

        binding.send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ActivitySend.class);
                startActivity(intent);
            }
        });

        mapFragment = (SupportMapFragment)getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


    }

    @Override
    protected  void onStop() {
        super.onStop();
//        mSocket.disconnect();
    }

    @Override
    protected void onStart() {
        super.onStart();
        // 소켓 서버
//        try {
//            mSocket = IO.socket("http://13.124.124.67:80/user");
//            Log.d(TAG, "ip주소 유");
//            mSocket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
//                @Override
//                public void call(Object... args) {
//                    mSocket.emit("message_from_user1", "hi");
//                    Log.d(TAG, "소켓 서버 접속");
//                }
//            }).on("location", new Emitter.Listener() {
//                @Override
//                public void call(Object... args) {
//                    Log.d(TAG, "뭐가 올까 " + args[0] + " 그자체 " + Arrays.toString(args));
//                    JSONArray onLocationArray = (JSONArray) args[0];
//                    Double getLat = 0.0;
//                    Double getLng = 0.0;
//                    String getTitle = "";
//
//                    try {
//                        for(int i = 0; i < onLocationArray.length(); i++) {
//                            JSONObject onLocationData = onLocationArray.getJSONObject(i);
//                            getLat = onLocationData.getDouble("lat");
//                            getLng = onLocationData.getDouble("lng");
//                        }
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                    Double finalGetLat = getLat;
//                    Double finalGetLng = getLng;
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            Bitmap car = Bitmap.createScaledBitmap(BitmapFactory
//                                            .decodeResource(getResources(), R.drawable.driving),
//                                    60, 60, false);
//                            mMap.addMarker(new MarkerOptions().position(
//                                    new LatLng(finalGetLat, finalGetLng)).title(getTitle)
//                                    .icon(BitmapDescriptorFactory.fromBitmap(car)));
//                        }
//                    });
//                }
//            });
//            mSocket.connect();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
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

            public Map<String, String> getHeaders() throws AuthFailureError {
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

    public void onLocationChange() {

    }
    //addMarker 재수정
    public void addMarker(MarkerModel markerModel, boolean isSelectedMarker) {
        LatLng position = new LatLng(markerModel.getLat(), markerModel.getLng());
        String title = markerModel.getTitle();
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.title(title);
        markerOptions.position(position);

        Bitmap markerDraw = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.baseline_place_black_18dp),
                100, 100, false);
        Bitmap markerDrawSelected_1= Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.baseline_pin_drop_white_24dp),
                100, 100, false);
        Bitmap markerDrawSelected_2= Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.baseline_pin_drop_black_24dp),
                100, 100, false);

//         클릭시 아이콘 배치할겨
        if(isSelectedMarker) {
            if(selectedCount != 2) {
                selectedTitle[selectedCount] = title;
                selectedCount++;
                Log.d(TAG, "제목 설정 및 카운트 업");
            }

            if (selectedCount == 1) {
                markerOptions.icon(BitmapDescriptorFactory.fromBitmap(markerDrawSelected_1));
            } else if(selectedCount == 2){
                markerOptions.icon(BitmapDescriptorFactory.fromBitmap(markerDrawSelected_2));
            }
            Log.d(TAG, "마커 표시addMarker " + title);
        }else {
            if(selectedCount != 0) { selectedCount--; }
            Log.d(TAG, "제목 카운터 다운");
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(markerDraw));
        }

        markersInfo.add(new MarkerModel(mMap.addMarker(markerOptions), title));
        Log.d(TAG, "배열 넣어진 크기 " + markersInfo.size());
    }
    // 서버에서 받아온 데이터로 마커 생성
    private void getMarkerItems() {
        ArrayList<MarkerModel> list = new ArrayList<MarkerModel>();
        try {
            for (int i = 0; i < ActivityWaypoint.jsonWaypointArray.length(); i++) {
                JSONObject result = ActivityWaypoint.jsonWaypointArray.getJSONObject(i);
                list.add(new MarkerModel(result.getDouble("lat"), result.getDouble("lng"),
                        result.getString("name")));
            }
        }catch (JSONException e) {
            e.printStackTrace();
            Log.e(TAG, "에러 하이고" );
        }

        Log.d(TAG, "배열 원래 크기 " + list.size());
        for (MarkerModel markerModel : list) {
            addMarker(markerModel, false);
        }
    }
    // 표시 마커 동작
    private void changeSelectedMarker(Marker marker) {
        Log.d(TAG, "마커 정보 : " + marker);
        Log.d(TAG, "배열 넣어진 크기 changeSelectesMarker" + markersInfo.size());
            // 선택했던 마커 다시 선택시 되돌리기 단, 순서대로
        if((selectedTitle[0] != null && selectedTitle[0].equals(marker.getTitle())) && !marker.getTitle().equals("car")
        || (selectedTitle[1] != null && selectedTitle[1].equals(marker.getTitle()) && !marker.getTitle().equals("car"))) {
            if(selectedTitle[1] == null) {
                markersCheck(marker);
                addMarker(new MarkerModel(marker.getPosition().latitude, marker.getPosition().longitude,
                        marker.getTitle()), false);
                marker.remove();
                selectedTitle[0] = null;
                Log.d(TAG, "제목 초기화");
            }else if(selectedTitle[1].equals(marker.getTitle()) && !marker.getTitle().equals("car")){
                markersCheck(marker);
                addMarker(new MarkerModel(marker.getPosition().latitude, marker.getPosition().longitude,
                        marker.getTitle()), false);
                marker.remove();
                selectedTitle[1] = null;
                Log.d(TAG, "제목 초기화");
            }
        }else if (marker != null && selectedCount < 2 && !marker.getTitle().equals("car")) { //선택한 마커 표시
            markersCheck(marker);
            addMarker(new MarkerModel(marker.getPosition().latitude, marker.getPosition().longitude,
                    marker.getTitle()), true);
            marker.remove();
        }
    }
    // 클릭한 마커의 정보가 있는지 체크
    public void markersCheck(Marker marker) {
        Log.d(TAG, "배열 넣어진 크기 markersCheck" + markersInfo.size());
        for(int i = 0; i < markersInfo.size(); i++) {
            Log.d(TAG, "마커배열들 " + markersInfo.get(i).getTitle());
            if(markersInfo.get(i).getTitle().equals(marker.getTitle())) {
                markersInfo.remove(i);
            }
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
        Bitmap car = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.driving),
                60, 60, false);
        mMap.addMarker(new MarkerOptions().position(YJU).title("car").icon(BitmapDescriptorFactory.fromBitmap(car)));
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
        changeSetVisible();
        //marker.getId()는 마커생성 순서
        startPoint.setText(String.format("출발지: %s", selectedTitle[0]));
        endPoint.setText(selectedTitle[1] == null ? "" : "도착지: " + selectedTitle[1]);

        Log.d(TAG, "마커 제발 되라 " + selectedCount + " 제목좀 " +selectedTitle[0] + selectedTitle[1]);
        return false;
    }

    public void changeSetVisible() {
        if(selectedTitle[0] ==  null || selectedTitle[1] == null) {
            deliveryInfo.setVisibility(View.GONE);
        }else {
            deliveryInfo.setVisibility(View.VISIBLE);
        }

        if(selectedTitle[1] != null) {
            Log.d(TAG, "배열 넣어진 크기 visible" + markersInfo.size());
            for(int i = 0; i < markersInfo.size(); i++) {
                int count = 0;
                Log.d(TAG, "배열크기" + markersInfo.size());
                Log.d(TAG, "마커정보들과 선택된 제목" + markersInfo.get(i).getTitle());
                Log.d(TAG, "마커정보들과 선택된 제목" + selectedTitle[0] + selectedTitle[1]);
                for (String s : selectedTitle) {
                    if (!markersInfo.get(i).getTitle().equals(s)) {
                        count++;
                        if(count == 2) markersInfo.get(i).getMarker().setVisible(false);
                    }
                }
            }
        }else {
            for(int i = 0; i < markersInfo.size(); i++) {
                markersInfo.get(i).getMarker().setVisible(true);
            }
        }
    }
}

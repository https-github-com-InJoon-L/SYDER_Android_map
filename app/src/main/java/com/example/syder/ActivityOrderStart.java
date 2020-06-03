package com.example.syder;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.syder.databinding.ActivityOrderStartBinding;
import com.example.syder.databinding.ActivityOrderingBinding;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class ActivityOrderStart extends FragmentActivity implements OnMapReadyCallback {

    private ActivityOrderStartBinding binding;
    private GoogleMap           mMap;
    private RequestQueue requestQueue;
    private SupportMapFragment mapFragment;
    private static int count;
    private static final String TAG = "activity_order_start";
    private int select;
    private Socket socket;
    private String Socket_URL_USER = "http://13.124.124.67:80/user";
    //    private static int
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOrderStartBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        requestQueue =  Volley.newRequestQueue(this);
//Socket Connect
        connectServer();

        long now = System.currentTimeMillis() + MainActivity.travelTime * 60000; //*60000
        Log.d(TAG, "now : " + now);
        Date mDate = new Date(now);
        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDate = new SimpleDateFormat("hh:mm:ss");
        String getTime = simpleDate.format(mDate);
        binding.arriveTime.setText(getTime);

        binding.buttonStart.setOnClickListener(v -> {
            binding.buttonStart.setVisibility(View.GONE);
            Intent intent = new Intent(this, MainActivity.class);
            new CountDownTimer(1000, 1000) {

                public void onTick(long millisUntilFinished) {

                }

                public void onFinish() {
                    startActivity(intent);
                    finish();
                }
            }.start();
        });

//        binding.buttonQRcodeCheckMap.setOnClickListener(v -> {
////            Intent intent = new Intent(this, ActivityScanQR.class);
////            intent.putExtra("intent", 2);
////            startActivity(intent);
////            finish();
////        });

        mapFragment = (SupportMapFragment)getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }
    private void connectServer(){
        try {
            socket = IO.socket(Socket_URL_USER);
            socket.connect();
            socketConnect();
            Log.i(TAG, "지금 연결함!!");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    private void socketConnect(){
        socket.on(Socket.EVENT_CONNECT,onConnect);
        socket.on(Socket.EVENT_CONNECT_ERROR,onConnectError);

        //차량 개방 요청 모듈
//        binding.btnOpen.setOnClickListener(v -> {
////            int car_info = 1;
////            socket.emit("user_openRequest", car_info);
////            Log.d(TAG,"유저로부터 차량 개방 요청 보냄!");
////            Log.d(TAG,"서버로 데이터 전송");
////
////        });
    }

    // 서버와 소켓 연결 성공 시 리스너
    private Emitter.Listener onConnect = args ->runOnUiThread(()->{
        Log.d(TAG,"Connect message : 서버와 연결이 성공하였습니다.");
        Toast.makeText(getApplicationContext(),"서버와 연결이 성공하였습니다.",Toast.LENGTH_SHORT).show();
    });

    // 서버연결이 실패 했을 때 리스너
    private Emitter.Listener onConnectError = args ->runOnUiThread(()->{
        Log.e(TAG,"Error message : 서버와 연결이 실패됬습니다.");
        Toast.makeText(getApplicationContext(),"서버와 연결이 실패됬습니다.",Toast.LENGTH_SHORT).show();
    });

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng YJU = new LatLng(35.896274, 128.621827);

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(YJU, 17));
        getMarkerItems();
    }

    //addMarker 재수정
    public void addMarker(MarkerModel markerModel, boolean isSelectedMarker) {
        LatLng position = new LatLng(markerModel.getLat(), markerModel.getLng());
        String title = markerModel.getTitle();
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.title(title);
        markerOptions.position(position);
        count++;
        Log.d(TAG, "d" + count);
        Bitmap markerDrawSelected_1= Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.baseline_pin_drop_white_24dp),
                100, 100, false);
        Bitmap markerDrawSelected_2= Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.baseline_pin_drop_black_24dp),
                100, 100, false);
        if(count == 1) {
            mMap.addMarker(markerOptions.icon(BitmapDescriptorFactory.fromBitmap(markerDrawSelected_1)));
            Log.d(TAG, "출발지마커 등록");
        }
        else if(count == 2) {
            mMap.addMarker(markerOptions.icon(BitmapDescriptorFactory.fromBitmap(markerDrawSelected_2)));
            Log.d(TAG, "도착지마커 등록");
        }
    }

    // 서버에서 받아온 데이터로 마커 생성
    private void getMarkerItems() {
        ArrayList<MarkerModel> list = new ArrayList<MarkerModel>();
        try {
            for (int i = 0; i < ActivityWaypoint.jsonWaypointArray.length(); i++) {
                JSONObject result = ActivityWaypoint.jsonWaypointArray.getJSONObject(i);
                list.add(new MarkerModel(result.getDouble("lat"), result.getDouble("lng"),
                        result.getString("name"),result.getString("id")));
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
}
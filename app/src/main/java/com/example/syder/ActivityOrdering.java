package com.example.syder;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
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
import java.util.Objects;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class ActivityOrdering extends FragmentActivity implements OnMapReadyCallback {

    private ActivityOrderingBinding binding;
    private GoogleMap           mMap;
    private RequestQueue requestQueue;
    private SupportMapFragment mapFragment;
    private static int count;
    private static final String TAG = "activity_ordering";
    private int select;
    String activityName;
    String orderName;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOrderingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        requestQueue =  Volley.newRequestQueue(this);


        long now = System.currentTimeMillis() + MainActivity.travelTime * 60000; //*60000
        Log.d(TAG, "now : " + now);
        Date mDate = new Date(now);
        @SuppressLint("SimpleDateFormat") SimpleDateFormat mFormat = new SimpleDateFormat("hh:mm:ss");
        String getTime = mFormat.format(mDate);
        binding.arriveTime.setText(getTime);
        activityName = "";
        orderName = "";
        try {
            Intent getActivity = getIntent();
            activityName = Objects.requireNonNull(getActivity.getExtras()).getString("activity_name");
            orderName = getActivity.getExtras().getString("order_name");
        }catch (Exception e) {
            e.printStackTrace();
        }

        binding.textView4.setText(orderName + "님께서 배송요청을 하셨습니다.");

        binding.agree.setOnClickListener(v -> {
            select = 1;
            selected();
        });

        binding.reject.setOnClickListener(v -> {
            select = 0;
            selected();
        });

        mapFragment = (SupportMapFragment)getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }



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
        Log.d(TAG, "횟수 " + count);
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

    public void selected() {
        String url = "http://13.124.189.186/api/consent/response?order_id=" + ActivitySend.orderID + "&consent_or_not=" + select + "&guard=user";

        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {

            public void onResponse(String response) {
                Log.d(TAG, "순서대로 " + response);
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    String getMsg = jsonResponse.getString("message");
                    Log.d(TAG, "동의요청" + getMsg);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if(select == 1) {
                    Intent intent = new Intent(ActivityOrdering.this, ActivityOrderEnd.class);
                    intent.putExtra("activity_name" , activityName);
                    intent.putExtra("order_name", orderName);
                    startActivity(intent);
                    finish();
                }else if(select == 0) {
                    Intent intent = new Intent(ActivityOrdering.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
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
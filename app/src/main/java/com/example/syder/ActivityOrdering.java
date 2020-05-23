package com.example.syder;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;

import com.android.volley.RequestQueue;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ActivityOrdering extends FragmentActivity implements OnMapReadyCallback {

    private ActivityOrderingBinding binding;
    private GoogleMap           mMap;
    private RequestQueue requestQueue;
    private SupportMapFragment mapFragment;
    private static int count;
    private static final String TAG = "activity_ordering";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOrderingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        requestQueue =  Volley.newRequestQueue(this);
        mapFragment = (SupportMapFragment)getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        long now = System.currentTimeMillis() + 720000;
        Date mDate = new Date(now);
        SimpleDateFormat simpleDate = new SimpleDateFormat("hh:mm:ss");
        String getTime = simpleDate.format(mDate);
        binding.arriveTime.setText(getTime);
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
        Log.d(TAG, "d" + count);
        Bitmap markerDrawSelected_1= Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.baseline_pin_drop_white_24dp),
                100, 100, false);
        Bitmap markerDrawSelected_2= Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.baseline_pin_drop_black_24dp),
                100, 100, false);
        if(count == 1)
            mMap.addMarker(markerOptions.icon(BitmapDescriptorFactory.fromBitmap(markerDrawSelected_1)));
        else if(count == 2)
            mMap.addMarker(markerOptions.icon(BitmapDescriptorFactory.fromBitmap(markerDrawSelected_2)));
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
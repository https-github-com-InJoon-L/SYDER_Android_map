package com.example.syder;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.syder.databinding.ActivitySendWaitBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class ActivityWait extends AppCompatActivity {
    private ActivitySendWaitBinding binding;
    private static final String TAG = "activity_wait";
    private RequestQueue requestQueue;
    private Socket socket;
    private String Socket_URL_USER = "http://13.124.124.67:80/user";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySendWaitBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
//Socket Connect
        connectServer();
        Bundle bundle = getIntent().getExtras();
        if(bundle != null) {
            String fcmTokend = (String) bundle.get("receiver_fcm_token");
            Log.d(TAG, "wait fcm" + fcmTokend);
        }
        requestQueue =  Volley.newRequestQueue(this);
        binding.buttonQRcodeCheck.setOnClickListener(v->{
            authCheck();
        });

        binding.buttonMakeQRCode.setOnClickListener(v->{
            Intent intent = new Intent (this, ActivityMakeQRCode.class);
            startActivity(intent);
        });
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

        // 차량 출발 요청 모듈
        try {
            JSONObject locationInfo = new JSONObject();
            locationInfo.put("status", 210);
            locationInfo.put("carNumber", 1);
            locationInfo.put("path_id", 3);
//                locationInfo.put("path_way", "reverse");
            locationInfo.put("start_point", MainActivity.selectedTitle[0]);
            locationInfo.put("end_point", MainActivity.selectedTitle[1]);
            locationInfo.put("sender_token", ActivityLogin.FCMtoken);
            locationInfo.put("receiver_token", MyFirebaseInstanceIDService.receiverFcmToken);
            socket.emit("user_departureOrder", locationInfo);
            Log.d(TAG,"유저로부터 차량 출발 요청 받음!");
            Log.d(TAG,"서버로 데이터 전송");
        } catch (JSONException e) {
            Log.e(TAG, "Failed to create JSONObject", e);
        }
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
    public void authCheck() {
        //ActivityLogin.orderId
        String url = "http://13.124.189.186/api/authCheck?guard=user";
        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            public void onResponse(String response) {
                int authId = 0;
                Log.d(TAG,"auth 체크" + response);
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    authId = jsonResponse.getInt("id");
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e(TAG, "auth 체크 에러 하이고" );
                }
                if(authId == ActivityLogin.userId) {
                    startActivity(new Intent(getApplicationContext(), ActivityScanQR.class));
                    finish();
                }
            }
        },new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG,"auth 체크 에러 -> " + error.getMessage());
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization" , "Bearer " + ActivityLogin.loginResponse);
                return params;
            }
        };

        request.setShouldCache(false);
        requestQueue.add(request);
        Log.d(TAG, "auth 체크 요청보냄");
    }
}
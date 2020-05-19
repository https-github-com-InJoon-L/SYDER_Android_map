package com.example.syder;

        import androidx.appcompat.app.AppCompatActivity;

        import android.content.Intent;
        import android.os.Bundle;
        import android.util.Log;
        import android.view.View;

        import com.android.volley.AuthFailureError;
        import com.android.volley.NetworkResponse;
        import com.android.volley.Request;
        import com.android.volley.RequestQueue;
        import com.android.volley.Response;
        import com.android.volley.VolleyError;
        import com.android.volley.toolbox.StringRequest;
        import com.android.volley.toolbox.Volley;
        import com.example.syder.databinding.ActivitySendBinding;

        import org.json.JSONException;
        import org.json.JSONObject;

        import java.util.HashMap;
        import java.util.Map;

public class ActivitySend extends AppCompatActivity {
    private static final String TAG = "activity_send";
    private ActivitySendBinding binding;
    private RequestQueue requestQueue;
    static String receiverName;
    static int receiverID;
    static int orderID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestQueue = Volley.newRequestQueue(this);
        binding = ActivitySendBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.buttonCheckNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkSender();
            }
        });

        binding.callButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ActivitySend.this, ActivitySending.class);
                startActivity(intent);
            }
        });
    }

//    public void orderRequest() {
//        String url = "http://13.124.189.186/api/orders";
//
//        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
//            @Override
//            public void onResponse(String response) {
//                Log.d(TAG, "동의요청 보내면서 주문 정보 보내기");
//                Log.d(TAG, response);
//                try {
//                    JSONObject jsonObject = new JSONObject(response);
//                    JSONObject jsonWaypoint = jsonObject.getJSONObject("waypoint");
//                    orderID = jsonWaypoint.getInt("id");
//                    Log.d(TAG, "화면 전환 id: " + orderID);
//
//                    Intent intent = new Intent(activity_send.this, ActivitySending.class);
//                    startActivity(intent);
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//
//            }
//        }) {
//            protected Map<String, String> getParams() throws AuthFailureError{
//                Map<String, String> params = new HashMap<String, String>();
//                params.put("receiver", String.valueOf(receiverID));
//                params.put("order_cart", String.valueOf(1));    // 임시 값 일단 차가 없다
//                params.put("order_route", String.valueOf(1));   // 임시 값 경로가 없다
//                params.put("cartMove_needs", String.valueOf(0));   // 임시 값 일단 차가 없다
//                params.put("cartMove_route", String.valueOf(1));   // 임시 값 경로가 없다
//                params.put("guard", "user");
//
//                return params;
//            }
//
//            public Map<String, String> getHeaders() throws AuthFailureError {
//                Map<String, String> params = new HashMap<String, String>();
//                params.put("Authorization", "Bearer " + ActivityLogin.loginResponse);
//
//                return params;
//            }
//        };
//        request.setShouldCache(false);
//        requestQueue.add(request);
//        Log.i(TAG,"주문요청 보냄.");
//    }

    public void checkSender() {
        String phoneNumber = binding.senderPhonenumber.getText().toString();
        String url = "http://13.124.189.186/api/user/request?phone=" + phoneNumber + "&guard=user";

        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {

            public void onResponse(String response) {
                Log.d(TAG, "응답" + response);
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    JSONObject jsonReceiver = jsonResponse.getJSONObject("receiver");
                    int getID = jsonReceiver.getInt("id");
                    String getName = jsonReceiver.getString("name");
                    receiverID = getID;
                    receiverName = getName;
                    Log.d(TAG, "응답 : ID - " + receiverID + " Name - " + receiverName);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.d("응답", receiverName + receiverID);
                binding.senderName.setText(receiverName);
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

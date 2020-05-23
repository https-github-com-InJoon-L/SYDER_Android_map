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

        import org.json.JSONArray;
        import org.json.JSONException;
        import org.json.JSONObject;

        import java.util.HashMap;
        import java.util.Map;

public class ActivitySend extends AppCompatActivity {
    private static final String TAG = "activity_send";
    private ActivitySendBinding binding;
    private RequestQueue requestQueue;
    private String moveNeedSelect;
    private String routeReverse;
    private String routeID;
    static String receiverName;
    static int receiverID;
    static int orderID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestQueue = Volley.newRequestQueue(this);
        binding = ActivitySendBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        moveNeedSelect = MainActivity.moveNeed ? "1" : "0";
        routeReverseChecking();
        binding.buttonCheckNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkSender();
            }
        });

        binding.callButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                orderRequest();
                orderConsentRequest();
                authCheck();
            }
        });
    }

    public void routeReverseChecking() {
//        routeReverse
        for(int i = 0; i < MainActivity.routeList.size(); i++) {
            if (MainActivity.startingId.equals(MainActivity.routeList.get(i).getStartingId()) &&
            MainActivity.arrivalId.equals(MainActivity.routeList.get(i).getArrivalPoint())) {
                routeReverse = "0";
                routeID = MainActivity.routeList.get(i).getRouteId();
                break;
            }else if(MainActivity.startingId.equals(MainActivity.routeList.get(i).getArrivalPoint()) &&
                    MainActivity.arrivalId.equals(MainActivity.routeList.get(i).getStartingId())) {
                routeReverse = "1";
                routeID = MainActivity.routeList.get(i).getRouteId();
                break;
            }
        }
    }

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
                if(authId == ActivityLogin.orderId) {
                    Intent intent = new Intent(ActivitySend.this, ActivitySending.class);
                    startActivity(intent);
                }
            }
        },new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG,"auth 체크 에러 -> " + error.getMessage());
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError{
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization" , "Bearer " + ActivityLogin.loginResponse);
                return params;
            }
        };

        request.setShouldCache(false);
        requestQueue.add(request);
        Log.d(TAG, "auth 체크 요청보냄");
    }

    public void orderConsentRequest() {
        String url = "http://13.124.189.186/api/consent/request?order_id=6&guard=user"; //주문 아이디 값 적용해야됨

        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {

            public void onResponse(String response) {
                Log.d(TAG, "응답" + response);
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    String getMsg = jsonResponse.getString("message");
                } catch (JSONException e) {
                    e.printStackTrace();
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

    public void orderRequest() {
        String url = "http://13.124.189.186/api/orders";

        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "동의요청 보내면서 주문 정보 보내기");
                Log.d(TAG, response);
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    JSONObject jsonOrder = jsonResponse.getJSONObject("order");
                    orderID = jsonOrder.getInt("id");
                    Log.d(TAG, "화면 전환 id: " + orderID);

                    Intent intent = new Intent(ActivitySend.this, ActivitySending.class);
                    startActivity(intent);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            protected Map<String, String> getParams() throws AuthFailureError{
                Map<String, String> params = new HashMap<String, String>();
                params.put("receiver", "" + receiverID);
                params.put("order_cart", MainActivity.cartId);    // 임시 값 일단 차가 없다
                params.put("order_route", routeID);   // 임시 값 경로가 없다
                params.put("reverse_direction", routeReverse);
                params.put("cartMove_needs", "" + moveNeedSelect);   // 임시 값 일단 차가 없다
                params.put("cartMove_route", MainActivity.moveNeedRoute);   // 임시 값 경로가 없다
                params.put("guard", "user");

                return params;
            }

            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", "Bearer " + ActivityLogin.loginResponse);

                return params;
            }
        };
        request.setShouldCache(false);
        requestQueue.add(request);
        Log.i(TAG,"주문요청 보냄.");
    }

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

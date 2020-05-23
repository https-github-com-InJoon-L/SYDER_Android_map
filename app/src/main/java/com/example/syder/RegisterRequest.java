package com.example.syder;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;


public class registerRequest extends StringRequest {

    // 서버 URL 설정(라라벨 연동)
    final static private String URL = "http://13.124.189.186/api/register/users";
    private Map<String, String> map;

    public registerRequest(String account, String password, String password_confirmation, String name, String email, String phone, Response.Listener<String> listener){
        super(Method.POST, URL, listener, null);//해당 URL에 POST방식으로 파마미터들을 전송함
        map = new HashMap<>();
        map.put("account",               account);
        map.put("password",              password);
        map.put("password_confirmation", password_confirmation);
        map.put("name",                  name);
        map.put("email",                 email);
        map.put("phone",                 phone);

    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return map;
    }
}
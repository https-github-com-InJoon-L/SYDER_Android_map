package com.example.syder;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ActivityRegister extends AppCompatActivity {

    private static final String TAG =  "activity_register";

    private EditText edit_ID, edit_password, edit_password_check, edit_name, edit_email, edit_phonenumber;
    private Button btn_register;

    private ArrayAdapter adapter;
    private RequestQueue mQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) { // 액티비티 시장시 처음으로 실행되는 생명주기

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mQueue = Volley.newRequestQueue(this);

        // 아이디 값 찾아주기
        edit_ID              = (EditText)findViewById(R.id.edit_ID);
        edit_password        = (EditText)findViewById(R.id.edit_password);
        edit_password_check  = (EditText)findViewById(R.id.edit_password_confirm);
        edit_name            = (EditText)findViewById(R.id.edit_name);
        edit_email           = (EditText)findViewById(R.id.edit_email);
        edit_phonenumber     = (EditText)findViewById(R.id.edit_phonenumber);
        btn_register         = (Button)findViewById(R.id.register_button);


        //회원 가입 버튼이 눌렸을때
        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG,"회원가입 버튼을 눌렀습니다.");

                // EditText에 현재 입력되어있는 값을 get 해온다.
                String account = edit_ID.getText().toString();
                String password = edit_password.getText().toString();
                String password_confirmation = edit_password_check.getText().toString();
                String name = edit_name.getText().toString();
//                String birthday = textView_birthday.getText().toString();
//                String gender = spinner.getSelectedItem().toString();
                String email = edit_email.getText().toString();
                String phone = edit_phonenumber.getText().toString();


                //한칸이라도 빠뜨렸을 경우
                if(account.equals("")||password.equals("")||password_confirmation.equals("")||name.equals("")||email.equals("")||phone.equals("")){
                    Toast.makeText(getApplication(), "모두 입력해주십시오.",Toast.LENGTH_SHORT).show();
                    AlertDialog.Builder builder = new AlertDialog.Builder(ActivityRegister.this);
                    AlertDialog dialog = builder.setMessage("모두 입력해주십시오")
                            .setNegativeButton("OK", null)
                            .create();
                    dialog.show();
                    return;
                }

                // 비밀번호랑 비밀번호 재확인이 다를경우
                if(!password.equals(password_confirmation)){
                    Toast.makeText(getApplication(), "비밀번호가 일치하지 않습니다.",Toast.LENGTH_SHORT).show();
                }


                //회원가입 시작
                Response.Listener<String> responseListener = new Response.Listener<String>(){

                    @Override
                    public void onResponse(String response) {
                        try{
                            Toast.makeText(getApplication(), "회원 등록에 성공하였습니다.",Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(ActivityRegister.this, ActivityLogin.class);
                            startActivity(intent);
                            jsonParse();

                            Log.i(TAG,"성공 메세지");
                        }
                        catch(Exception e){
                            e.printStackTrace();
                            Toast.makeText(getApplication(), "회원 등록에 실패하였습니다.",Toast.LENGTH_SHORT).show();

                            Log.i(TAG,"오류 메세지");

                            return;
                        }
                    }
                };//Response.Listener 완료

//                 서버로 Volley를 이용해서 요청을 함.
                registerRequest registerRequest = new registerRequest(account, password, password, name, email, phone, responseListener);
                RequestQueue queue = Volley.newRequestQueue(ActivityRegister.this);
                queue.add(registerRequest);

            }
        });
    }


    private void jsonParse(){
        String url = "http://13.124.189.186/api/register/users";

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                JSONArray jsonArray;
                try {
                    jsonArray = response.getJSONArray("user");

                    for(int i = 0; i < jsonArray.length(); i++){
                        JSONObject user = jsonArray.getJSONObject(i);

                        String account = user.getString("account");
                        String name = user.getString("name");
                        String email = user.getString("email");
                        String phone = user.getString("phone");

                        Log.i(TAG,"account : " + account + "name : " + name +  "email : " + email + "phone : " + phone);
                    }
                } catch (JSONException e) {

                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        mQueue.add(request);
    }
    //     이메일 정규식
    public static final Pattern VALID_EMAIL_ADDRESS_REGEX
            = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    // 이메일 검사
    public static boolean validateEmail(String email) {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(email);
        return matcher.find();
    }

    //비밀번호 정규식
    public static final Pattern VALID_PASSWOLD_REGEX_ALPHA_NUM
            = Pattern.compile("^[a-zA-Z0-9!@.#$%^&*?_~]{4,16}$"); // 4자리 ~ 16자리까지 가능

    // 비밀번호 검사
    public static boolean validatePassword(String password) {
        Matcher matcher = VALID_PASSWOLD_REGEX_ALPHA_NUM.matcher(password);
        return matcher.matches();
    }
}

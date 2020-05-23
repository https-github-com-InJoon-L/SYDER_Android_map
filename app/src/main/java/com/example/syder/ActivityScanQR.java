package com.example.syder;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONException;
import org.json.JSONObject;

public class ActivityScanQR extends AppCompatActivity {
    private IntentIntegrator qrScan;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_q_r_activity);

        qrScan = new IntentIntegrator(this);
        qrScan.setOrientationLocked(false);
        qrScan.setPrompt("QR코드를 인식해주세요.");
        qrScan.initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        boolean qrcode_Result = false;
        if(result != null){
            if(result.getContents() == null){
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(this, ActivityWait.class);
                startActivity(intent);
            }else{
                Toast.makeText(this, "QR코드 스캔이 완료되었습니다.", Toast.LENGTH_LONG).show();
                try {
                    JSONObject jsonObject = new JSONObject(result.getContents());
                    String qrcode_Message = jsonObject.getString("message");
                    qrcode_Result = jsonObject.getBoolean("result");

                    Log.d("응답", "QR코드 응답상태 : " + qrcode_Message + "QR코드 Boolean : " + qrcode_Result);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            if(qrcode_Result == true){
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
            }else{
                Intent intent = new Intent(this, ActivityWait.class);
                startActivity(intent);
            }
        }else{
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}

package com.example.syder;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.example.syder.databinding.ActivitySendWaitBinding;

public class ActivityWait extends AppCompatActivity {
    private ActivitySendWaitBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySendWaitBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.buttonQRcodeCheck.setOnClickListener(v->{
            startActivity(new Intent(getApplicationContext(), ActivityScanQR.class));
        });

        binding.buttonMakeQRCode.setOnClickListener(v->{
            Intent intent = new Intent (this, ActivityMakeQRCode.class);
            startActivity(intent);
        });
    }
}

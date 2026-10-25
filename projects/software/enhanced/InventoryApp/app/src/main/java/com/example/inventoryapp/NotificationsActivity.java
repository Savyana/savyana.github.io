package com.example.inventoryapp;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.content.Intent;
import android.widget.EditText;
import android.widget.Toast;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

/** SMS opt-in with explicit recipient number and safe permission-denial behavior. */
public class NotificationsActivity extends AppCompatActivity {
    private EditText phone;
    private android.content.SharedPreferences preferences;
    private final androidx.activity.result.ActivityResultLauncher<String> permission = registerForActivityResult(new ActivityResultContracts.RequestPermission(), granted -> {
        preferences.edit().putBoolean("enabled",granted).apply();
        Toast.makeText(this,granted?"Low-stock SMS alerts enabled":"SMS declined; inventory still works",Toast.LENGTH_LONG).show();
    });
    @Override protected void onCreate(Bundle state){
        super.onCreate(state);setContentView(R.layout.activity_notifications);
        long userId=getSharedPreferences("session",MODE_PRIVATE).getLong("user_id",-1);
        if(userId<0){startActivity(new Intent(this,MainActivity.class));finish();return;}
        preferences=getSharedPreferences("sms_"+userId,MODE_PRIVATE);
        phone=findViewById(R.id.editPhone);phone.setText(preferences.getString("phone",""));
        findViewById(R.id.buttonAllowSms).setOnClickListener(v->{
            String number=phone.getText().toString().trim();
            if(!android.util.Patterns.PHONE.matcher(number).matches()){phone.setError("Enter your mobile number, including area code");return;}
            preferences.edit().putString("phone",number).apply();
            if(ContextCompat.checkSelfPermission(this,Manifest.permission.SEND_SMS)==PackageManager.PERMISSION_GRANTED){
                preferences.edit().putBoolean("enabled",true).apply();Toast.makeText(this,"SMS alerts enabled",Toast.LENGTH_SHORT).show();
            }else permission.launch(Manifest.permission.SEND_SMS);
        });
        findViewById(R.id.buttonDenySms).setOnClickListener(v->{preferences.edit().putBoolean("enabled",false).apply();Toast.makeText(this,"SMS alerts disabled",Toast.LENGTH_SHORT).show();finish();});
    }
}

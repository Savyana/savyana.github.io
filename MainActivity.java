package com.example.inventoryapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

/** Sign-in screen; first-time users can create their account here. */
public class MainActivity extends AppCompatActivity {
    @Override protected void onCreate(Bundle state) {
        super.onCreate(state); setContentView(R.layout.activity_main);
        DatabaseHelper database = new DatabaseHelper(this);
        EditText username = findViewById(R.id.editUsername);
        EditText password = findViewById(R.id.editPassword);
        Button signIn = findViewById(R.id.buttonLogin);
        Button create = findViewById(R.id.buttonCreateAccount);
        signIn.setOnClickListener(v -> {
            long userId = database.login(username.getText().toString(), password.getText().toString());
            if (userId < 0) { Toast.makeText(this,"Incorrect username or password",Toast.LENGTH_LONG).show(); return; }
            getSharedPreferences("session", MODE_PRIVATE).edit().putLong("user_id",userId).apply();
            startActivity(new Intent(this, InventoryActivity.class)); finish();
        });
        create.setOnClickListener(v -> {
            boolean ok = database.register(username.getText().toString(), password.getText().toString());
            Toast.makeText(this, ok ? "Account created! Tap Login." : "Use a unique username and password of at least 8 characters.", Toast.LENGTH_LONG).show();
        });
    }
}

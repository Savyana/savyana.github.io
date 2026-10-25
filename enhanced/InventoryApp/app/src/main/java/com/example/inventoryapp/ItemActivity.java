package com.example.inventoryapp;

import android.os.Bundle;
import android.content.Intent;
import android.database.Cursor;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

/** Shared add/edit form with validation and optional low-stock alert. */
public class ItemActivity extends AppCompatActivity {
    @Override protected void onCreate(Bundle saved) {
        super.onCreate(saved);setContentView(R.layout.activity_add_item);
        long userId=getSharedPreferences("session",MODE_PRIVATE).getLong("user_id",-1);
        if(userId<0){startActivity(new Intent(this,MainActivity.class));finish();return;}
        DatabaseHelper database=new DatabaseHelper(this);
        long itemId=getIntent().getLongExtra("item_id",-1);
        EditText name=findViewById(R.id.editItemName), qty=findViewById(R.id.editQuantity), description=findViewById(R.id.editDescription);
        final int[] previousQuantity={Integer.MAX_VALUE};
        if(itemId!=-1) try(Cursor c=database.getItem(userId,itemId)) {
            if(!c.moveToFirst()){finish();return;}
            name.setText(c.getString(1));qty.setText(String.valueOf(c.getInt(2)));description.setText(c.getString(3));previousQuantity[0]=c.getInt(2);
        }
        findViewById(R.id.buttonSaveItem).setOnClickListener(v -> {
            String title=name.getText().toString().trim(), rawQty=qty.getText().toString().trim();
            if(title.isEmpty()){name.setError("Item name required");return;}
            int number;try{number=Integer.parseInt(rawQty);}catch(NumberFormatException e){qty.setError("Enter a valid quantity");return;}
            if(number<0){qty.setError("Quantity cannot be negative");return;}
            if(database.saveItem(userId,itemId,title,number,description.getText().toString())) {
                if(InventoryRules.shouldAlert(previousQuantity[0],number,itemId==-1)) SmsAlerts.sendIfEnabled(this,userId,title,number);
                Toast.makeText(this,"Item saved",Toast.LENGTH_SHORT).show();finish();
            } else Toast.makeText(this,"Could not save item",Toast.LENGTH_LONG).show();
        });
        findViewById(R.id.buttonCancel).setOnClickListener(v -> finish());
    }
}

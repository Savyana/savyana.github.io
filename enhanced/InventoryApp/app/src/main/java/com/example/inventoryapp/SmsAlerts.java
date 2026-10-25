package com.example.inventoryapp;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.telephony.SmsManager;
import android.telephony.SubscriptionManager;
import android.util.Log;
import androidx.core.content.ContextCompat;

/** SMS is optional: inventory CRUD never depends on messaging permission. */
public final class SmsAlerts {
    private SmsAlerts() {}
    public static void sendIfEnabled(Context context,long userId,String item,int quantity) {
        String key="sms_"+userId;
        android.content.SharedPreferences prefs=context.getSharedPreferences(key,Context.MODE_PRIVATE);
        if(!prefs.getBoolean("enabled",false)) return;
        if(ContextCompat.checkSelfPermission(context,Manifest.permission.SEND_SMS)!=PackageManager.PERMISSION_GRANTED)return;
        String phone=prefs.getString("phone","");
        if(!android.util.Patterns.PHONE.matcher(phone).matches())return;
        try {
            SmsManager manager;
            if(android.os.Build.VERSION.SDK_INT>=31) manager=context.getSystemService(SmsManager.class);
            else manager=SmsManager.getDefault();
            if(manager!=null) manager.sendTextMessage(phone,null,"Inventory Manager: Low stock for "+item+" ("+quantity+" remaining).",null,null);
        }catch(Exception e){Log.e("InventorySMS","Unable to send optional inventory alert",e);}
    }
}

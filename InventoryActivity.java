package com.example.inventoryapp;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.graphics.Typeface;
import android.view.Gravity;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.EditText;
import android.text.TextWatcher;
import android.text.Editable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

/** A live grid of records loaded from SQLite, scoped to the signed-in user. */
public class InventoryActivity extends AppCompatActivity {
    private DatabaseHelper database;
    private long userId;
    private TableLayout grid;
    private EditText search;
    @Override protected void onCreate(Bundle state) {
        super.onCreate(state);
        userId = getSharedPreferences("session", MODE_PRIVATE).getLong("user_id", -1);
        if (userId < 0) { startActivity(new Intent(this, MainActivity.class)); finish(); return; }
        database = new DatabaseHelper(this);
        LinearLayout root = new LinearLayout(this); root.setOrientation(LinearLayout.VERTICAL); root.setPadding(20,24,20,16);
        TextView heading = new TextView(this); heading.setText("My Inventory"); heading.setTextSize(26); heading.setTypeface(null,Typeface.BOLD);root.addView(heading);
        TextView hint = new TextView(this); hint.setText("Tap Edit to change an item or Delete to remove it. Low stock: 5 or fewer.");root.addView(hint);
        search = new EditText(this);
        search.setSingleLine(true);
        search.setHint("Search inventory items");
        root.addView(search);
        search.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { if (grid != null) refresh(); }
            @Override public void afterTextChanged(Editable s) { }
        });
        Button add = new Button(this); add.setText("+ Add Item"); add.setOnClickListener(v -> startActivity(new Intent(this, ItemActivity.class)));root.addView(add);
        Button notifications = new Button(this);notifications.setText("SMS Notification Settings");notifications.setOnClickListener(v -> startActivity(new Intent(this, NotificationsActivity.class)));root.addView(notifications);
        ScrollView scroller = new ScrollView(this);grid = new TableLayout(this);grid.setStretchAllColumns(true);scroller.addView(grid);root.addView(scroller,new LinearLayout.LayoutParams(-1,0,1));
        Button logout = new Button(this);logout.setText("Log Out"); logout.setOnClickListener(v -> {getSharedPreferences("session",MODE_PRIVATE).edit().remove("user_id").apply();startActivity(new Intent(this,MainActivity.class));finish();});root.addView(logout);
        setContentView(root);
    }
    @Override protected void onResume() {super.onResume();if (grid!=null) refresh();}
    private TextView cell(String s) {TextView t = new TextView(this);t.setText(s);t.setPadding(6,14,6,14);t.setGravity(Gravity.CENTER_VERTICAL);return t;}
    private void refresh() {
        grid.removeAllViews();TableRow header = new TableRow(this);
        for(String label:new String[]{"Item / Details","Qty","Actions"}){TextView t=cell(label);t.setTypeface(null,Typeface.BOLD);header.addView(t);}grid.addView(header);
        try(Cursor cursor=database.findItems(userId,search.getText().toString())) {
            if(cursor.getCount()==0) { TableRow row=new TableRow(this);row.addView(cell("No items yet — tap Add Item."));grid.addView(row); }
            while(cursor.moveToNext()) {
                long id=cursor.getLong(0);String name=cursor.getString(1);int qty=cursor.getInt(2);String details=cursor.getString(3);
                TableRow row=new TableRow(this); row.addView(cell(name+(details.isEmpty()?"":"\n"+details)));
                TextView count=cell(String.valueOf(qty));if(InventoryRules.lowStock(qty))count.setTextColor(0xffb42318);row.addView(count);
                LinearLayout actions=new LinearLayout(this);actions.setOrientation(LinearLayout.VERTICAL);
                Button edit=new Button(this);edit.setText("Edit");edit.setTextSize(12);edit.setOnClickListener(v -> {Intent i=new Intent(this,ItemActivity.class);i.putExtra("item_id",id);startActivity(i);}); actions.addView(edit);
                Button delete=new Button(this);delete.setText("Delete");delete.setTextSize(12);delete.setOnClickListener(v -> new AlertDialog.Builder(this).setTitle("Delete "+name+"?").setMessage("This cannot be undone.").setNegativeButton("Cancel",null).setPositiveButton("Delete",(d,w)->{database.deleteItem(userId,id);refresh();}).show());actions.addView(delete);
                row.addView(actions);grid.addView(row);
            }
        }
    }
}

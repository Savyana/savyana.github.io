package com.example.inventoryapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.security.SecureRandom;
import java.security.spec.PBEKeySpec;
import javax.crypto.SecretKeyFactory;
import android.util.Base64;

/** Persistent local user accounts and inventory records. */
public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "inventory_manager.db";
    private static final int DATABASE_VERSION = 2;
    public DatabaseHelper(Context context) { super(context, DATABASE_NAME, null, DATABASE_VERSION); }
    @Override public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE users (id INTEGER PRIMARY KEY AUTOINCREMENT, username TEXT NOT NULL UNIQUE COLLATE NOCASE, salt TEXT NOT NULL, hash TEXT NOT NULL)");
        db.execSQL("CREATE TABLE items (id INTEGER PRIMARY KEY AUTOINCREMENT, owner_id INTEGER NOT NULL, name TEXT NOT NULL, quantity INTEGER NOT NULL CHECK(quantity>=0), description TEXT NOT NULL DEFAULT '', FOREIGN KEY(owner_id) REFERENCES users(id))");
        db.execSQL("CREATE INDEX idx_items_owner_name ON items(owner_id, name COLLATE NOCASE)");
    }
    @Override public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Preserve existing accounts and items when adding the inventory lookup index.
        if (oldVersion < 2) {
            db.execSQL("CREATE INDEX IF NOT EXISTS idx_items_owner_name ON items(owner_id, name COLLATE NOCASE)");
        }
    }
    private static String hashPassword(String password, byte[] salt) {
        try {
            PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 120000, 256);
            byte[] hashed = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).getEncoded();
            spec.clearPassword();
            return Base64.encodeToString(hashed, Base64.NO_WRAP);
        } catch (Exception e) { throw new IllegalStateException("Unable to protect password", e); }
    }
    public boolean register(String username, String password) {
        if (username.trim().isEmpty() || password.length() < 8) return false;
        byte[] salt = new byte[16]; new SecureRandom().nextBytes(salt);
        ContentValues values = new ContentValues();
        values.put("username", username.trim());
        values.put("salt", Base64.encodeToString(salt, Base64.NO_WRAP));
        values.put("hash", hashPassword(password, salt));
        return getWritableDatabase().insert("users", null, values) != -1;
    }
    public long login(String username, String password) {
        try (Cursor c = getReadableDatabase().query("users", new String[]{"id", "salt", "hash"}, "username=?", new String[]{username.trim()}, null, null, null)) {
            if (!c.moveToFirst()) return -1;
            byte[] salt = Base64.decode(c.getString(1), Base64.NO_WRAP);
            String actual = hashPassword(password, salt);
            byte[] a = actual.getBytes(java.nio.charset.StandardCharsets.UTF_8);
            byte[] b = c.getString(2).getBytes(java.nio.charset.StandardCharsets.UTF_8);
            return java.security.MessageDigest.isEqual(a, b) ? c.getLong(0) : -1;
        }
    }
    public Cursor allItems(long ownerId) {
        return findItems(ownerId, "");
    }
    /** Prepared selection parameters prevent user-entered search text from becoming SQL. */
    public Cursor findItems(long ownerId, String searchText) {
        String text = searchText == null ? "" : searchText.trim();
        String escaped = text.replace("!", "!!").replace("%", "!%").replace("_", "!_");
        String selection = "owner_id=?";
        String[] args = new String[]{String.valueOf(ownerId)};
        if (!text.isEmpty()) {
            selection += " AND name LIKE ? ESCAPE '!'";
            args = new String[]{String.valueOf(ownerId), "%" + escaped + "%"};
        }
        return getReadableDatabase().query("items", new String[]{"id", "name", "quantity", "description"}, selection, args, null, null, "name COLLATE NOCASE ASC");
    }
    public Cursor getItem(long ownerId, long itemId) {
        return getReadableDatabase().query("items", new String[]{"id", "name", "quantity", "description"}, "owner_id=? AND id=?", new String[]{String.valueOf(ownerId),String.valueOf(itemId)}, null, null, null);
    }
    public boolean saveItem(long ownerId, long itemId, String name, int quantity, String description) {
        if (ownerId < 0 || !InventoryRules.validItem(name, quantity)) return false;
        ContentValues v = new ContentValues(); v.put("name", name.trim()); v.put("quantity", quantity); v.put("description", description.trim());
        if (itemId == -1) {v.put("owner_id", ownerId); return getWritableDatabase().insert("items", null, v) != -1;}
        return getWritableDatabase().update("items", v, "id=? AND owner_id=?", new String[]{String.valueOf(itemId),String.valueOf(ownerId)}) > 0;
    }
    public void deleteItem(long ownerId, long itemId) {
        getWritableDatabase().delete("items", "id=? AND owner_id=?", new String[]{String.valueOf(itemId),String.valueOf(ownerId)});
    }
}

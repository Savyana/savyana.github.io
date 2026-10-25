package com.example.inventoryapp;

/** Business rules kept independent of Android views for unit testing. */
public final class InventoryRules {
    public static final int LOW_STOCK_THRESHOLD = 5;
    private InventoryRules() { }

    public static String normalizeName(String name) {
        return name == null ? "" : name.trim();
    }

    public static boolean validItem(String name, int quantity) {
        return !normalizeName(name).isEmpty() && quantity >= 0;
    }

    public static boolean lowStock(int quantity) {
        return quantity >= 0 && quantity <= LOW_STOCK_THRESHOLD;
    }

    /** Notify only when a new item starts low or an existing item crosses the threshold. */
    public static boolean shouldAlert(int previousQuantity, int currentQuantity, boolean isNew) {
        return lowStock(currentQuantity) && (isNew || previousQuantity > LOW_STOCK_THRESHOLD);
    }
}

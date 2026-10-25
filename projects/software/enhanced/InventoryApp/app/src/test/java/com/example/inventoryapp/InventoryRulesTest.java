package com.example.inventoryapp;

import org.junit.Test;
import static org.junit.Assert.*;

public class InventoryRulesTest {
    @Test public void rejectsEmptyNamesAndNegativeQuantities() {
        assertFalse(InventoryRules.validItem("   ", 1));
        assertFalse(InventoryRules.validItem("Printer paper", -1));
        assertTrue(InventoryRules.validItem("  Printer paper  ", 0));
    }

    @Test public void testsLowStockBoundary() {
        assertTrue(InventoryRules.lowStock(5));
        assertFalse(InventoryRules.lowStock(6));
        assertFalse(InventoryRules.lowStock(-1));
    }

    @Test public void alertsOnlyOnCrossingThresholdOrNewLowStock() {
        assertTrue(InventoryRules.shouldAlert(9, 4, false));
        assertFalse(InventoryRules.shouldAlert(4, 3, false));
        assertTrue(InventoryRules.shouldAlert(Integer.MAX_VALUE, 2, true));
        assertFalse(InventoryRules.shouldAlert(7, 7, false));
    }
}
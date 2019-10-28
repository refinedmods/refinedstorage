package com.raoulvdberge.refinedstorage;

public class RSOldConfig {
    //region Energy
    public int craftingMonitorUsage;
    //endregion

    //region Wireless Crafting Monitor
    public boolean wirelessCraftingMonitorUsesEnergy;
    public int wirelessCraftingMonitorCapacity;
    public int wirelessCraftingMonitorOpenUsage;
    public int wirelessCraftingMonitorCancelUsage;
    public int wirelessCraftingMonitorCancelAllUsage;
    //endregion

    //region Autocrafting
    public int calculationTimeoutMs;
    //endregion

    //region Categories
    private static final String ENERGY = "energy";
    private static final String WIRELESS_CRAFTING_MONITOR = "wirelessCraftingMonitor";
    private static final String AUTOCRAFTING = "autocrafting";
    //endregion

    /*private void loadConfig() {
        //region Energy
        craftingMonitorUsage = config.getInt("craftingMonitor", ENERGY, 2, 0, Integer.MAX_VALUE, "The energy used by Crafting Monitors");
        //endregion

        //region Wireless Crafting Monitor
        wirelessCraftingMonitorUsesEnergy = config.getBoolean("usesEnergy", WIRELESS_CRAFTING_MONITOR, true, "Whether the Wireless Crafting Monitor uses energy");
        wirelessCraftingMonitorCapacity = config.getInt("capacity", WIRELESS_CRAFTING_MONITOR, 3200, 0, Integer.MAX_VALUE, "The energy capacity of the Wireless Crafting Monitor");
        wirelessCraftingMonitorOpenUsage = config.getInt("open", WIRELESS_CRAFTING_MONITOR, 35, 0, Integer.MAX_VALUE, "The energy used by the Wireless Crafting Monitor to open");
        wirelessCraftingMonitorCancelUsage = config.getInt("cancel", WIRELESS_CRAFTING_MONITOR, 4, 0, Integer.MAX_VALUE, "The energy used by the Wireless Crafting Monitor to cancel a task");
        wirelessCraftingMonitorCancelAllUsage = config.getInt("cancelAll", WIRELESS_CRAFTING_MONITOR, 5, 0, Integer.MAX_VALUE, "The energy used by the Wireless Crafting Monitor to cancel all tasks");
        //endregion

        //region Autocrafting
        calculationTimeoutMs = config.getInt("calculationTimeoutMs", AUTOCRAFTING, 5000, 5000, Integer.MAX_VALUE, "The autocrafting calculation timeout in milliseconds, tasks taking longer than this to calculate (NOT execute) are cancelled to avoid server strain");
        //endregion
    }*/
}

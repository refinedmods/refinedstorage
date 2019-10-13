package com.raoulvdberge.refinedstorage;

public class RSOldConfig {
    //region Energy
    public int constructorUsage;
    public int crafterUsage;
    public int crafterPerPatternUsage;
    public int craftingMonitorUsage;
    public int crafterManagerUsage;
    public int destructorUsage;
    public int detectorUsage;
    public int externalStorageUsage;
    public int externalStoragePerStorageUsage;
    public int exporterUsage;
    public int importerUsage;
    public int interfaceUsage;
    public int fluidInterfaceUsage;
    public int relayUsage;
    public int wirelessTransmitterUsage;
    public int networkTransmitterUsage;
    public int networkReceiverUsage;
    public int diskManipulatorUsage;
    public int securityManagerUsage;
    public int securityManagerPerSecurityCardUsage;
    //endregion

    //region Wireless Transmitter
    public int wirelessTransmitterBaseRange;
    public int wirelessTransmitterRangePerUpgrade;
    //endregion

    //region Wireless Grid
    public boolean wirelessGridUsesEnergy;
    public int wirelessGridCapacity;
    public int wirelessGridOpenUsage;
    public int wirelessGridExtractUsage;
    public int wirelessGridInsertUsage;
    //endregion

    //region Portable Grid
    public boolean portableGridUsesEnergy;
    public int portableGridCapacity;
    public int portableGridOpenUsage;
    public int portableGridExtractUsage;
    public int portableGridInsertUsage;
    //endregion

    //region Wireless Fluid Grid
    public boolean wirelessFluidGridUsesEnergy;
    public int wirelessFluidGridCapacity;
    public int wirelessFluidGridOpenUsage;
    public int wirelessFluidGridExtractUsage;
    public int wirelessFluidGridInsertUsage;
    //endregion

    //region Wireless Crafting Monitor
    public boolean wirelessCraftingMonitorUsesEnergy;
    public int wirelessCraftingMonitorCapacity;
    public int wirelessCraftingMonitorOpenUsage;
    public int wirelessCraftingMonitorCancelUsage;
    public int wirelessCraftingMonitorCancelAllUsage;
    //endregion

    //region Readers and Writers
    public int readerUsage;
    public int writerUsage;
    public int readerWriterChannelEnergyCapacity;
    //endregion

    //region Covers
    public boolean hideCovers;
    //endregion

    //region Autocrafting
    public int calculationTimeoutMs;
    //endregion

    //region Categories
    private static final String ENERGY = "energy";
    private static final String WIRELESS_TRANSMITTER = "wirelessTransmitter";
    private static final String WIRELESS_GRID = "wirelessGrid";
    private static final String PORTABLE_GRID = "portableGrid";
    private static final String WIRELESS_FLUID_GRID = "wirelessFluidGrid";
    private static final String WIRELESS_CRAFTING_MONITOR = "wirelessCraftingMonitor";
    private static final String READER_WRITER = "readerWriter";
    private static final String COVERS = "covers";
    private static final String AUTOCRAFTING = "autocrafting";
    //endregion

    /*private void loadConfig() {
        //region Energy
        constructorUsage = config.getInt("constructor", ENERGY, 1, 0, Integer.MAX_VALUE, "The energy used by Constructors");
        crafterUsage = config.getInt("crafter", ENERGY, 2, 0, Integer.MAX_VALUE, "The base energy used by Crafters");
        crafterPerPatternUsage = config.getInt("crafterPerPattern", ENERGY, 1, 0, Integer.MAX_VALUE, "The additional energy used per Pattern in a Crafter");
        craftingMonitorUsage = config.getInt("craftingMonitor", ENERGY, 2, 0, Integer.MAX_VALUE, "The energy used by Crafting Monitors");
        crafterManagerUsage = config.getInt("crafterManager", ENERGY, 4, 0, Integer.MAX_VALUE, "The energy used by Crafter Managers");
        destructorUsage = config.getInt("destructor", ENERGY, 1, 0, Integer.MAX_VALUE, "The energy used by Destructors");
        detectorUsage = config.getInt("detector", ENERGY, 2, 0, Integer.MAX_VALUE, "The energy used by Detectors");
        externalStorageUsage = config.getInt("externalStorage", ENERGY, 0, 0, Integer.MAX_VALUE, "The base energy used by External Storages");
        externalStoragePerStorageUsage = config.getInt("externalStoragePerStorage", ENERGY, 1, 0, Integer.MAX_VALUE, "The additional energy used per connected storage to an External Storage");
        exporterUsage = config.getInt("exporter", ENERGY, 1, 0, Integer.MAX_VALUE, "The energy used by Exporters");
        importerUsage = config.getInt("importer", ENERGY, 1, 0, Integer.MAX_VALUE, "The energy used by Importers");
        interfaceUsage = config.getInt("interface", ENERGY, 3, 0, Integer.MAX_VALUE, "The energy used by Interfaces");
        fluidInterfaceUsage = config.getInt("fluidInterface", ENERGY, 3, 0, Integer.MAX_VALUE, "The energy used by Fluid Interfaces");
        relayUsage = config.getInt("relay", ENERGY, 1, 0, Integer.MAX_VALUE, "The energy used by Relays");
        wirelessTransmitterUsage = config.getInt("wirelessTransmitter", ENERGY, 8, 0, Integer.MAX_VALUE, "The energy used by Wireless Transmitters");
        networkTransmitterUsage = config.getInt("networkTransmitter", ENERGY, 64, 0, Integer.MAX_VALUE, "The energy used by Network Transmitters");
        networkReceiverUsage = config.getInt("networkReceiver", ENERGY, 0, 0, Integer.MAX_VALUE, "The energy used by Network Receivers");
        diskManipulatorUsage = config.getInt("diskManipulator", ENERGY, 3, 0, Integer.MAX_VALUE, "The energy used by Disk Manipulators");
        securityManagerUsage = config.getInt("securityManager", ENERGY, 4, 0, Integer.MAX_VALUE, "The base energy used by Security Managers");
        securityManagerPerSecurityCardUsage = config.getInt("securityManagerPerSecurityCard", ENERGY, 10, 0, Integer.MAX_VALUE, "The additional energy used by Security Cards in Security Managers");
        //endregion

        //region Wireless Transmitter
        wirelessTransmitterBaseRange = config.getInt("range", WIRELESS_TRANSMITTER, 16, 0, Integer.MAX_VALUE, "The base range of the Wireless Transmitter");
        wirelessTransmitterRangePerUpgrade = config.getInt("rangePerUpgrade", WIRELESS_TRANSMITTER, 8, 0, Integer.MAX_VALUE, "The additional range per Range Upgrade in the Wireless Transmitter");
        //endregion

        //region Wireless Grid
        wirelessGridUsesEnergy = config.getBoolean("usesEnergy", WIRELESS_GRID, true, "Whether the Wireless Grid uses energy");
        wirelessGridCapacity = config.getInt("capacity", WIRELESS_GRID, 3200, 0, Integer.MAX_VALUE, "The energy capacity of the Wireless Grid");
        wirelessGridOpenUsage = config.getInt("open", WIRELESS_GRID, 30, 0, Integer.MAX_VALUE, "The energy used by the Wireless Grid to open");
        wirelessGridInsertUsage = config.getInt("insert", WIRELESS_GRID, 3, 0, Integer.MAX_VALUE, "The energy used by the Wireless Grid to insert items");
        wirelessGridExtractUsage = config.getInt("extract", WIRELESS_GRID, 3, 0, Integer.MAX_VALUE, "The energy used by the Wireless Grid to extract items");
        //endregion

        //region Portable Grid
        portableGridUsesEnergy = config.getBoolean("usesEnergy", PORTABLE_GRID, true, "Whether the Portable Grid uses energy");
        portableGridCapacity = config.getInt("capacity", PORTABLE_GRID, 3200, 0, Integer.MAX_VALUE, "The energy capacity of the Portable Grid");
        portableGridOpenUsage = config.getInt("open", PORTABLE_GRID, 30, 0, Integer.MAX_VALUE, "The energy used by the Portable Grid to open");
        portableGridInsertUsage = config.getInt("insert", PORTABLE_GRID, 3, 0, Integer.MAX_VALUE, "The energy used by the Portable Grid to insert items");
        portableGridExtractUsage = config.getInt("extract", PORTABLE_GRID, 3, 0, Integer.MAX_VALUE, "The energy used by the Portable Grid to extract items");
        //endregion

        //region Wireless Fluid Grid
        wirelessFluidGridUsesEnergy = config.getBoolean("usesEnergy", WIRELESS_FLUID_GRID, true, "Whether the Fluid Wireless Grid uses energy");
        wirelessFluidGridCapacity = config.getInt("capacity", WIRELESS_FLUID_GRID, 3200, 0, Integer.MAX_VALUE, "The energy capacity of the Wireless Fluid Grid");
        wirelessFluidGridOpenUsage = config.getInt("open", WIRELESS_FLUID_GRID, 30, 0, Integer.MAX_VALUE, "The energy used by the Fluid Wireless Grid to open");
        wirelessFluidGridInsertUsage = config.getInt("insert", WIRELESS_FLUID_GRID, 3, 0, Integer.MAX_VALUE, "The energy used by the Wireless Fluid Grid to insert items");
        wirelessFluidGridExtractUsage = config.getInt("extract", WIRELESS_FLUID_GRID, 3, 0, Integer.MAX_VALUE, "The energy used by the Wireless Fluid Grid to extract items");
        //endregion

        //region Wireless Crafting Monitor
        wirelessCraftingMonitorUsesEnergy = config.getBoolean("usesEnergy", WIRELESS_CRAFTING_MONITOR, true, "Whether the Wireless Crafting Monitor uses energy");
        wirelessCraftingMonitorCapacity = config.getInt("capacity", WIRELESS_CRAFTING_MONITOR, 3200, 0, Integer.MAX_VALUE, "The energy capacity of the Wireless Crafting Monitor");
        wirelessCraftingMonitorOpenUsage = config.getInt("open", WIRELESS_CRAFTING_MONITOR, 35, 0, Integer.MAX_VALUE, "The energy used by the Wireless Crafting Monitor to open");
        wirelessCraftingMonitorCancelUsage = config.getInt("cancel", WIRELESS_CRAFTING_MONITOR, 4, 0, Integer.MAX_VALUE, "The energy used by the Wireless Crafting Monitor to cancel a task");
        wirelessCraftingMonitorCancelAllUsage = config.getInt("cancelAll", WIRELESS_CRAFTING_MONITOR, 5, 0, Integer.MAX_VALUE, "The energy used by the Wireless Crafting Monitor to cancel all tasks");
        //endregion

        //region Readers and Writers
        readerUsage = config.getInt("reader", READER_WRITER, 2, 0, Integer.MAX_VALUE, "The energy used by Readers");
        writerUsage = config.getInt("writer", READER_WRITER, 2, 0, Integer.MAX_VALUE, "The energy used by Writers");
        readerWriterChannelEnergyCapacity = config.getInt("channelEnergyCapacity", READER_WRITER, 16000, 0, Integer.MAX_VALUE, "The energy capacity of energy channels");
        //endregion

        //region Covers
        hideCovers = config.getBoolean("hideCovers", COVERS, false, "Whether to hide covers in the creative mode tabs and JEI");
        //endregion

        //region Autocrafting
        calculationTimeoutMs = config.getInt("calculationTimeoutMs", AUTOCRAFTING, 5000, 5000, Integer.MAX_VALUE, "The autocrafting calculation timeout in milliseconds, tasks taking longer than this to calculate (NOT execute) are cancelled to avoid server strain");
        //endregion
    }*/
}

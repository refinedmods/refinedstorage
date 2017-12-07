package com.raoulvdberge.refinedstorage;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.config.IConfigElement;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public final class RSConfig {
    private Configuration config;

    //region Energy
    public int controllerBaseUsage;
    public int cableUsage;
    public int constructorUsage;
    public int crafterUsage;
    public int crafterPerPatternUsage;
    public int craftingMonitorUsage;
    public int destructorUsage;
    public int detectorUsage;
    public int diskDriveUsage;
    public int diskDrivePerDiskUsage;
    public int externalStorageUsage;
    public int externalStoragePerStorageUsage;
    public int exporterUsage;
    public int importerUsage;
    public int interfaceUsage;
    public int fluidInterfaceUsage;
    public int relayUsage;
    public int soldererUsage;
    public int storageUsage;
    public int fluidStorageUsage;
    public int wirelessTransmitterUsage;
    public int gridUsage;
    public int craftingGridUsage;
    public int patternGridUsage;
    public int fluidGridUsage;
    public int networkTransmitterUsage;
    public float networkTransmitterPerBlockUsage;
    public int networkReceiverUsage;
    public int diskManipulatorUsage;
    public int readerUsage;
    public int writerUsage;
    public int securityManagerUsage;
    public int securityManagerPerSecurityCardUsage;
    //endregion

    //region Controller
    public int controllerCapacity;
    public boolean controllerUsesEnergy;
    //endregion

    //region Grid
    public int maxRowsStretch;
    public boolean largeFont;
    public boolean detailedTooltip;
    //endregion

    //region Wireless Transmitter
    public int wirelessTransmitterBaseRange;
    public int wirelessTransmitterRangePerUpgrade;
    //endregion

    //region Wireless Grid
    public boolean wirelessGridUsesEnergy;
    public int wirelessGridOpenUsage;
    public int wirelessGridExtractUsage;
    public int wirelessGridInsertUsage;
    //endregion

    //region Portable Grid
    public boolean portableGridUsesEnergy;
    public int portableGridOpenUsage;
    public int portableGridExtractUsage;
    public int portableGridInsertUsage;
    //endregion

    //region Wireless Fluid Grid
    public boolean wirelessFluidGridUsesEnergy;
    public int wirelessFluidGridOpenUsage;
    public int wirelessFluidGridExtractUsage;
    public int wirelessFluidGridInsertUsage;
    //endregion

    //region Wireless Crafting Monitor
    public boolean wirelessCraftingMonitorUsesEnergy;
    public int wirelessCraftingMonitorOpenUsage;
    public int wirelessCraftingMonitorCancelUsage;
    public int wirelessCraftingMonitorCancelAllUsage;
    //endregion

    //region Upgrades
    public int rangeUpgradeUsage;
    public int speedUpgradeUsage;
    public float soldererSpeedIncreasePerSpeedUpgrade;
    public int craftingUpgradeUsage;
    public int stackUpgradeUsage;
    public int interdimensionalUpgradeUsage;
    public int silkTouchUpgradeUsage;
    public int fortuneUpgradeUsagePerFortune;
    //endregion

    //region Categories
    private static final String ENERGY = "energy";
    private static final String CONTROLLER = "controller";
    private static final String GRID = "grid";
    private static final String WIRELESS_TRANSMITTER = "wirelessTransmitter";
    private static final String WIRELESS_GRID = "wirelessGrid";
    private static final String PORTABLE_GRID = "portableGrid";
    private static final String WIRELESS_FLUID_GRID = "wirelessFluidGrid";
    private static final String WIRELESS_CRAFTING_MONITOR = "wirelessCraftingMonitor";
    private static final String UPGRADES = "upgrades";
    //endregion

    public RSConfig(File configFile) {
        config = new Configuration(configFile);

        MinecraftForge.EVENT_BUS.register(this);

        loadConfig();
    }

    public Configuration getConfig() {
        return config;
    }

    @SubscribeEvent
    public void onConfigChangedEvent(ConfigChangedEvent.OnConfigChangedEvent event) {
        if (event.getModID().equalsIgnoreCase(RS.ID)) {
            loadConfig();
        }
    }

    private void loadConfig() {
        //region Energy
        controllerBaseUsage = config.getInt("controllerBase", ENERGY, 0, 0, Integer.MAX_VALUE, "The base energy used by the Controller");
        cableUsage = config.getInt("cable", ENERGY, 0, 0, Integer.MAX_VALUE, "The energy used by Cables");
        constructorUsage = config.getInt("constructor", ENERGY, 1, 0, Integer.MAX_VALUE, "The energy used by Constructors");
        crafterUsage = config.getInt("crafter", ENERGY, 2, 0, Integer.MAX_VALUE, "The base energy used by Crafters");
        crafterPerPatternUsage = config.getInt("crafterPerPattern", ENERGY, 1, 0, Integer.MAX_VALUE, "The additional energy used per Pattern in a Crafter");
        craftingMonitorUsage = config.getInt("craftingMonitor", ENERGY, 2, 0, Integer.MAX_VALUE, "The energy used by Crafting Monitors");
        destructorUsage = config.getInt("destructor", ENERGY, 1, 0, Integer.MAX_VALUE, "The energy used by Destructors");
        detectorUsage = config.getInt("detector", ENERGY, 2, 0, Integer.MAX_VALUE, "The energy used by Detectors");
        diskDriveUsage = config.getInt("diskDrive", ENERGY, 0, 0, Integer.MAX_VALUE, "The base energy used by Disk Drives");
        diskDrivePerDiskUsage = config.getInt("diskDrivePerDisk", ENERGY, 1, 0, Integer.MAX_VALUE, "The additional energy used by Storage Disks in Disk Drives");
        externalStorageUsage = config.getInt("externalStorage", ENERGY, 0, 0, Integer.MAX_VALUE, "The base energy used by External Storages");
        externalStoragePerStorageUsage = config.getInt("externalStoragePerStorage", ENERGY, 1, 0, Integer.MAX_VALUE, "The additional energy used per connected storage to an External Storage");
        exporterUsage = config.getInt("exporter", ENERGY, 1, 0, Integer.MAX_VALUE, "The energy used by Exporters");
        importerUsage = config.getInt("importer", ENERGY, 1, 0, Integer.MAX_VALUE, "The energy used by Importers");
        interfaceUsage = config.getInt("interface", ENERGY, 3, 0, Integer.MAX_VALUE, "The energy used by Interfaces");
        fluidInterfaceUsage = config.getInt("fluidInterface", ENERGY, 3, 0, Integer.MAX_VALUE, "The energy used by Fluid Interfaces");
        relayUsage = config.getInt("relay", ENERGY, 1, 0, Integer.MAX_VALUE, "The energy used by Relays");
        soldererUsage = config.getInt("solderer", ENERGY, 3, 0, Integer.MAX_VALUE, "The energy used by Solderers");
        storageUsage = config.getInt("storage", ENERGY, 1, 0, Integer.MAX_VALUE, "The energy used by Storage Blocks");
        fluidStorageUsage = config.getInt("fluidStorage", ENERGY, 1, 0, Integer.MAX_VALUE, "The energy used by Fluid Storage Blocks");
        wirelessTransmitterUsage = config.getInt("wirelessTransmitter", ENERGY, 8, 0, Integer.MAX_VALUE, "The energy used by Wireless Transmitters");
        gridUsage = config.getInt("grid", ENERGY, 2, 0, Integer.MAX_VALUE, "The energy used by Grids");
        craftingGridUsage = config.getInt("craftingGrid", ENERGY, 4, 0, Integer.MAX_VALUE, "The energy used by Crafting Grids");
        patternGridUsage = config.getInt("patternGrid", ENERGY, 3, 0, Integer.MAX_VALUE, "The energy used by Pattern Grids");
        fluidGridUsage = config.getInt("fluidGrid", ENERGY, 2, 0, Integer.MAX_VALUE, "The energy used by Fluid Grids");
        networkTransmitterUsage = config.getInt("networkTransmitter", ENERGY, 0, 0, Integer.MAX_VALUE, "The base energy used by Network Transmitters");
        networkTransmitterPerBlockUsage = config.getFloat("networkTransmitterPerBlock", ENERGY, 1, 0, Float.MAX_VALUE, "The additional energy per block that the Network Transmitter uses, gets rounded up");
        networkReceiverUsage = config.getInt("networkReceiver", ENERGY, 0, 0, Integer.MAX_VALUE, "The energy used by Network Receivers");
        diskManipulatorUsage = config.getInt("diskManipulator", ENERGY, 3, 0, Integer.MAX_VALUE, "The energy used by Disk Manipulators");
        readerUsage = config.getInt("reader", ENERGY, 2, 0, Integer.MAX_VALUE, "The energy used by Readers");
        writerUsage = config.getInt("writer", ENERGY, 2, 0, Integer.MAX_VALUE, "The energy used by Writers");
        securityManagerUsage = config.getInt("securityManager", ENERGY, 4, 0, Integer.MAX_VALUE, "The base energy used by Security Managers");
        securityManagerPerSecurityCardUsage = config.getInt("securityManagerPerSecurityCard", ENERGY, 10, 0, Integer.MAX_VALUE, "The additional energy used by Security Cards in Security Managers");
        //endregion

        //region Controller
        controllerCapacity = config.getInt("capacity", CONTROLLER, 32000, 0, Integer.MAX_VALUE, "The energy capacity of the Controller");
        controllerUsesEnergy = config.getBoolean("usesEnergy", CONTROLLER, true, "Whether the Controller uses energy");
        //endregion

        //region Grid
        maxRowsStretch = config.getInt("maxRowsStretch", GRID, Integer.MAX_VALUE, 3, Integer.MAX_VALUE, "The maximum amount of rows that the Grid can show when stretched");
        largeFont = config.getBoolean("largeFont", GRID, false, "Whether the Grid should use a large font for stack quantity display");
        detailedTooltip = config.getBoolean("detailedTooltip", GRID, true, "Whether the Grid should display a detailed tooltip when hovering over an item or fluid");
        //endregion

        //region Wireless Transmitter
        wirelessTransmitterBaseRange = config.getInt("range", WIRELESS_TRANSMITTER, 16, 0, Integer.MAX_VALUE, "The base range of the Wireless Transmitter");
        wirelessTransmitterRangePerUpgrade = config.getInt("rangePerUpgrade", WIRELESS_TRANSMITTER, 8, 0, Integer.MAX_VALUE, "The additional range per Range Upgrade in the Wireless Transmitter");
        //endregion

        //region Wireless Grid
        wirelessGridUsesEnergy = config.getBoolean("usesEnergy", WIRELESS_GRID, true, "Whether the Wireless Grid uses energy");
        wirelessGridOpenUsage = config.getInt("open", WIRELESS_GRID, 30, 0, Integer.MAX_VALUE, "The energy used by the Wireless Grid to open");
        wirelessGridInsertUsage = config.getInt("insert", WIRELESS_GRID, 3, 0, Integer.MAX_VALUE, "The energy used by the Wireless Grid to insert items");
        wirelessGridExtractUsage = config.getInt("extract", WIRELESS_GRID, 3, 0, Integer.MAX_VALUE, "The energy used by the Wireless Grid to extract items");
        //endregion

        //region Portable Grid
        portableGridUsesEnergy = config.getBoolean("usesEnergy", PORTABLE_GRID, true, "Whether the Portable Grid uses energy");
        portableGridOpenUsage = config.getInt("open", PORTABLE_GRID, 30, 0, Integer.MAX_VALUE, "The energy used by the Portable Grid to open");
        portableGridInsertUsage = config.getInt("insert", PORTABLE_GRID, 3, 0, Integer.MAX_VALUE, "The energy used by the Portable Grid to insert items");
        portableGridExtractUsage = config.getInt("extract", PORTABLE_GRID, 3, 0, Integer.MAX_VALUE, "The energy used by the Portable Grid to extract items");
        //endregion

        //region Wireless Grid
        wirelessFluidGridUsesEnergy = config.getBoolean("usesEnergy", WIRELESS_FLUID_GRID, true, "Whether the Fluid Wireless Grid uses energy");
        wirelessFluidGridOpenUsage = config.getInt("open", WIRELESS_FLUID_GRID, 30, 0, Integer.MAX_VALUE, "The energy used by the Fluid Wireless Grid to open");
        wirelessFluidGridInsertUsage = config.getInt("insert", WIRELESS_FLUID_GRID, 3, 0, Integer.MAX_VALUE, "The energy used by the Wireless Fluid Grid to insert items");
        wirelessFluidGridExtractUsage = config.getInt("extract", WIRELESS_FLUID_GRID, 3, 0, Integer.MAX_VALUE, "The energy used by the Wireless Fluid Grid to extract items");
        //endregion

        //region Wireless Crafting Monitor
        wirelessCraftingMonitorUsesEnergy = config.getBoolean("usesEnergy", WIRELESS_CRAFTING_MONITOR, true, "Whether the Wireless Crafting Monitor uses energy");
        wirelessCraftingMonitorOpenUsage = config.getInt("open", WIRELESS_CRAFTING_MONITOR, 35, 0, Integer.MAX_VALUE, "The energy used by the Wireless Crafting Monitor to open");
        wirelessCraftingMonitorCancelUsage = config.getInt("cancel", WIRELESS_CRAFTING_MONITOR, 4, 0, Integer.MAX_VALUE, "The energy used by the Wireless Crafting Monitor to cancel a task");
        wirelessCraftingMonitorCancelAllUsage = config.getInt("cancelAll", WIRELESS_CRAFTING_MONITOR, 5, 0, Integer.MAX_VALUE, "The energy used by the Wireless Crafting Monitor to cancel all tasks");
        //endregion

        //region Upgrades
        rangeUpgradeUsage = config.getInt("range", UPGRADES, 8, 0, Integer.MAX_VALUE, "The additional energy used per Range Upgrade");
        speedUpgradeUsage = config.getInt("speed", UPGRADES, 2, 0, Integer.MAX_VALUE, "The additional energy used per Speed Upgrade");
        soldererSpeedIncreasePerSpeedUpgrade = config.getFloat("soldererSpeedIncreasePerSpeedUpgrade", UPGRADES, 22.5F, 0F, 25F, "The speed increase percentage in the Solderer per Speed Upgrade");
        craftingUpgradeUsage = config.getInt("crafting", UPGRADES, 5, 0, Integer.MAX_VALUE, "The additional energy used per Crafting Upgrade");
        stackUpgradeUsage = config.getInt("stack", UPGRADES, 12, 0, Integer.MAX_VALUE, "The additional energy used per Stack Upgrade");
        interdimensionalUpgradeUsage = config.getInt("interdimensional", UPGRADES, 1000, 0, Integer.MAX_VALUE, "The additional energy used by the Interdimensional Upgrade");
        silkTouchUpgradeUsage = config.getInt("silkTouch", UPGRADES, 15, 0, Integer.MAX_VALUE, "The additional energy used by the Silk Touch Upgrade");
        fortuneUpgradeUsagePerFortune = config.getInt("fortune", UPGRADES, 10, 0, Integer.MAX_VALUE, "The additional energy used by the Fortune Upgrade, multiplied by the level of the enchantment");
        //endregion

        if (config.hasChanged()) {
            config.save();
        }
    }

    @SuppressWarnings("unchecked")
    public List<IConfigElement> getConfigElements() {
        List<IConfigElement> list = new ArrayList<>();

        list.add(new ConfigElement(config.getCategory(ENERGY)));
        list.add(new ConfigElement(config.getCategory(CONTROLLER)));
        list.add(new ConfigElement(config.getCategory(UPGRADES)));
        list.add(new ConfigElement(config.getCategory(WIRELESS_TRANSMITTER)));
        list.add(new ConfigElement(config.getCategory(GRID)));
        list.add(new ConfigElement(config.getCategory(WIRELESS_GRID)));
        list.add(new ConfigElement(config.getCategory(WIRELESS_FLUID_GRID)));
        list.add(new ConfigElement(config.getCategory(WIRELESS_CRAFTING_MONITOR)));
        list.add(new ConfigElement(config.getCategory(PORTABLE_GRID)));

        return list;
    }
}

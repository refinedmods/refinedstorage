package refinedstorage;

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
    public int euConversion;
    //endregion

    //region Controller
    public int controllerCapacity;
    public boolean controllerUsesEnergy;
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

    //region Upgrades
    public int rangeUpgradeUsage;
    public int speedUpgradeUsage;
    public int craftingUpgradeUsage;
    public int stackUpgradeUsage;
    public int interdimensionalUpgradeUsage;
    public int silktouchUpgradeUsage;
    //endregion

    //region Categories
    private static final String ENERGY = "energy";
    private static final String CONTROLLER = "controller";
    private static final String WIRELESS_TRANSMITTER = "wirelessTransmitter";
    private static final String WIRELESS_GRID = "wirelessGrid";
    private static final String UPGRADES = "upgrades";
    private static final String MISC = "misc";
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
        networkTransmitterUsage = config.getInt("networkTransmitter", ENERGY, 50, 0, Integer.MAX_VALUE, "The base energy used by Network Transmitters");
        networkTransmitterPerBlockUsage = config.getFloat("networkTransmitterPerBlock", ENERGY, 4, 0, Float.MAX_VALUE, "The additional energy per block that the Network Transmitter uses, gets rounded up");
        networkReceiverUsage = config.getInt("networkReceiver", ENERGY, 15, 0, Integer.MAX_VALUE, "The energy used by Network Receivers");
        diskManipulatorUsage = config.getInt("diskManipulator", ENERGY, 3, 0, Integer.MAX_VALUE, "The energy used by Disk Manipulators");
        euConversion = config.getInt("euConversion", ENERGY, 4, 0, Integer.MAX_VALUE, "The amount of RS that equals 1 EU");
        //endregion

        //region Controller
        controllerCapacity = config.getInt("capacity", CONTROLLER, 32000, 0, Integer.MAX_VALUE, "The energy capacity of the Controller");
        controllerUsesEnergy = config.getBoolean("usesEnergy", CONTROLLER, true, "Whether the Controller uses energy");
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

        //region Upgrades
        rangeUpgradeUsage = config.getInt("range", UPGRADES, 8, 0, Integer.MAX_VALUE, "The additional energy used per Range Upgrade");
        speedUpgradeUsage = config.getInt("speed", UPGRADES, 2, 0, Integer.MAX_VALUE, "The additional energy used per Speed Upgrade");
        craftingUpgradeUsage = config.getInt("crafting", UPGRADES, 5, 0, Integer.MAX_VALUE, "The additional energy used per Crafting Upgrade");
        stackUpgradeUsage = config.getInt("stack", UPGRADES, 12, 0, Integer.MAX_VALUE, "The additional energy used per Stack Upgrade");
        interdimensionalUpgradeUsage = config.getInt("interdimensional", UPGRADES, 1000, 0, Integer.MAX_VALUE, "The additional energy used by the Interdimensional Upgrade");
        silktouchUpgradeUsage = config.getInt("silktouch", UPGRADES, 15, 0, Integer.MAX_VALUE, "The additional energy used by the Silk Touch Upgrade");
        //endregion

        if (config.hasChanged()) {
            config.save();
        }
    }

    @SuppressWarnings("unchecked")
    public List<IConfigElement> getConfigElements() {
        List<IConfigElement> list = new ArrayList<>();

        list.addAll(new ConfigElement(config.getCategory(ENERGY)).getChildElements());
        list.addAll(new ConfigElement(config.getCategory(CONTROLLER)).getChildElements());
        list.addAll(new ConfigElement(config.getCategory(UPGRADES)).getChildElements());
        list.addAll(new ConfigElement(config.getCategory(WIRELESS_TRANSMITTER)).getChildElements());
        list.addAll(new ConfigElement(config.getCategory(WIRELESS_GRID)).getChildElements());
        list.addAll(new ConfigElement(config.getCategory(MISC)).getChildElements());

        return list;
    }
}

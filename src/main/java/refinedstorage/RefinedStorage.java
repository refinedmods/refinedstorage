package refinedstorage;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import refinedstorage.item.ItemStorageDisk;
import refinedstorage.proxy.CommonProxy;

@Mod(modid = RefinedStorage.ID, version = RefinedStorage.VERSION)
public final class RefinedStorage {
    public static final String ID = "refinedstorage";
    public static final String VERSION = "0.8.2";

    public static final SimpleNetworkWrapper NETWORK = NetworkRegistry.INSTANCE.newSimpleChannel(ID);

    public static final CreativeTabs TAB = new CreativeTabs(ID) {
        @Override
        public ItemStack getIconItemStack() {
            return new ItemStack(RefinedStorageItems.STORAGE_DISK, 1, ItemStorageDisk.TYPE_1K);
        }

        @Override
        public Item getTabIconItem() {
            return null;
        }
    };

    @SidedProxy(clientSide = "refinedstorage.proxy.ClientProxy", serverSide = "refinedstorage.proxy.ServerProxy")
    public static CommonProxy PROXY;

    @Instance
    public static RefinedStorage INSTANCE;

    public int cableRfUsage;
    public int constructorRfUsage;
    public int crafterRfUsage;
    public int crafterPerPatternRfUsage;
    public int craftingMonitorRfUsage;
    public int destructorRfUsage;
    public int detectorRfUsage;
    public int diskDriveRfUsage;
    public int diskDrivePerDiskRfUsage;
    public int externalStorageRfUsage;
    public int externalStoragePerStorageRfUsage;
    public int exporterRfUsage;
    public int importerRfUsage;
    public int interfaceRfUsage;
    public int relayRfUsage;
    public int soldererRfUsage;
    public int storageRfUsage;
    public int wirelessTransmitterRfUsage;
    public int gridRfUsage;
    public int craftingGridRfUsage;
    public int patternGridRfUsage;

    public boolean controllerUsesRf;

    public int wirelessTransmitterBaseRange;

    public int rangeUpgradeRfUsage;
    public int speedUpgradeRfUsage;
    public int craftingUpgradeRfUsage;
    public int stackUpgradeRfUsage;

    public int wirelessTransmitterRangePerUpgrade;
    public int soldererSpeedIncreasePerUpgrade;

    @EventHandler
    public void preInit(FMLPreInitializationEvent e) {
        PROXY.preInit(e);

        Configuration config = new Configuration(e.getSuggestedConfigurationFile());

        cableRfUsage = config.getInt("cable", "energy", 0, 0, Integer.MAX_VALUE, "The RF/t used by cables");
        constructorRfUsage = config.getInt("constructor", "energy", 1, 0, Integer.MAX_VALUE, "The RF/t used by Constructors");
        crafterRfUsage = config.getInt("crafter", "energy", 2, 0, Integer.MAX_VALUE, "The base RF/t used by Crafters");
        crafterPerPatternRfUsage = config.getInt("crafterPerPattern", "energy", 1, 0, Integer.MAX_VALUE, "The additional RF/t used per Pattern in a Crafter");
        craftingMonitorRfUsage = config.getInt("craftingMonitor", "energy", 2, 0, Integer.MAX_VALUE, "The RF/t used by Crafting Monitors");
        destructorRfUsage = config.getInt("destructor", "energy", 1, 0, Integer.MAX_VALUE, "The RF/t used by Destructors");
        detectorRfUsage = config.getInt("detector", "energy", 2, 0, Integer.MAX_VALUE, "The RF/t used by Detectors");
        diskDriveRfUsage = config.getInt("diskDrive", "energy", 0, 0, Integer.MAX_VALUE, "The base RF/t used by Disk Drives");
        diskDrivePerDiskRfUsage = config.getInt("diskDrivePerDisk", "energy", 1, 0, Integer.MAX_VALUE, "The additional RF/t used by Storage Disks in Disk Drives");
        externalStorageRfUsage = config.getInt("externalStorage", "energy", 0, 0, Integer.MAX_VALUE, "The base RF/t used by External Storages");
        externalStoragePerStorageRfUsage = config.getInt("externalStoragePerStorage", "energy", 1, 0, Integer.MAX_VALUE, "The additional RF/t used per connected storage to an External Storage");
        exporterRfUsage = config.getInt("exporter", "energy", 1, 0, Integer.MAX_VALUE, "The RF/t used by Exporters");
        importerRfUsage = config.getInt("importer", "energy", 1, 0, Integer.MAX_VALUE, "The RF/t used by Importers");
        interfaceRfUsage = config.getInt("interface", "energy", 3, 0, Integer.MAX_VALUE, "The RF/t used by Interfaces");
        relayRfUsage = config.getInt("relay", "energy", 1, 0, Integer.MAX_VALUE, "The RF/t used by Relays");
        soldererRfUsage = config.getInt("solderer", "energy", 3, 0, Integer.MAX_VALUE, "The RF/t used by Solderers");
        storageRfUsage = config.getInt("storage", "energy", 1, 0, Integer.MAX_VALUE, "The RF/t used by Storage Blocks");
        wirelessTransmitterRfUsage = config.getInt("wirelessTransmitter", "energy", 8, 0, Integer.MAX_VALUE, "The RF/t used by Wireless Transmitters");
        gridRfUsage = config.getInt("grid", "energy", 2, 0, Integer.MAX_VALUE, "The RF/t used by Grids");
        craftingGridRfUsage = config.getInt("craftingGrid", "energy", 4, 0, Integer.MAX_VALUE, "The RF/t used by Crafting Grids");
        patternGridRfUsage = config.getInt("patternGrid", "energy", 3, 0, Integer.MAX_VALUE, "The RF/t used by Pattern Grids");

        controllerUsesRf = config.getBoolean("controllerUsesRf", "energy", true, "Whether the controller uses RF");

        wirelessTransmitterBaseRange = config.getInt("range", "wirelessTransmitter", 16, 0, Integer.MAX_VALUE, "The base range of the Wireless Transmitter");

        rangeUpgradeRfUsage = config.getInt("range", "upgrades", 8, 0, Integer.MAX_VALUE, "The additional RF/t used per Range Upgrade");
        speedUpgradeRfUsage = config.getInt("speed", "upgrades", 2, 0, Integer.MAX_VALUE, "The additional RF/t used per Speed Upgrade");
        craftingUpgradeRfUsage = config.getInt("crafting", "upgrades", 5, 0, Integer.MAX_VALUE, "The additional RF/t used per Crafting Upgrade");
        stackUpgradeRfUsage = config.getInt("stack", "upgrades", 12, 0, Integer.MAX_VALUE, "The additional RF/t used per Stack Upgrade");

        wirelessTransmitterRangePerUpgrade = config.getInt("rangePerUpgrade", "upgrades", 8, 0, Integer.MAX_VALUE, "The additional range per Range Upgrade in the Wireless Transmitter");
        soldererSpeedIncreasePerUpgrade = config.getInt("soldererSpeedIncrasePerUpgrade", "upgrades", 1, 0, Integer.MAX_VALUE, "The additional speed increase per Speed Upgrade in the Solderer");

        config.save();
    }

    @EventHandler
    public void init(FMLInitializationEvent e) {
        PROXY.init(e);
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent e) {
        PROXY.postInit(e);
    }

    public static boolean hasJei() {
        return Loader.isModLoaded("JEI");
    }
}

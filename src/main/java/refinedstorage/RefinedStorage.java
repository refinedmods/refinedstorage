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
import refinedstorage.proxy.CommonProxy;

import java.util.ArrayList;
import java.util.List;

@Mod(modid = RefinedStorage.ID, version = RefinedStorage.VERSION)
public final class RefinedStorage {
    public static final String ID = "refinedstorage";
    public static final String VERSION = "0.8.9";

    @SidedProxy(clientSide = "refinedstorage.proxy.ClientProxy", serverSide = "refinedstorage.proxy.ServerProxy")
    public static CommonProxy PROXY;

    @Instance
    public static RefinedStorage INSTANCE;

    public final SimpleNetworkWrapper network = NetworkRegistry.INSTANCE.newSimpleChannel(ID);

    public final CreativeTabs tab = new CreativeTabs(ID) {
        @Override
        public ItemStack getIconItemStack() {
            return new ItemStack(RefinedStorageItems.STORAGE_HOUSING);
        }

        @Override
        public Item getTabIconItem() {
            return null;
        }
    };

    public List<ItemStack> items = new ArrayList<ItemStack>();

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
    public int relayUsage;
    public int soldererUsage;
    public int storageUsage;
    public int wirelessTransmitterUsage;
    public int gridUsage;
    public int craftingGridUsage;
    public int patternGridUsage;

    public int controller;
    public boolean controllerUsesEnergy;

    public int wirelessTransmitterBaseRange;

    public int rangeUpgradeUsage;
    public int speedUpgradeUsage;
    public int craftingUpgradeUsage;
    public int stackUpgradeUsage;
    public int wirelessTransmitterRangePerUpgrade;

    @EventHandler
    public void preInit(FMLPreInitializationEvent e) {
        PROXY.preInit(e);

        Configuration config = new Configuration(e.getSuggestedConfigurationFile());

        cableUsage = config.getInt("cable", "energy", 0, 0, Integer.MAX_VALUE, "The energy used by Cables");
        constructorUsage = config.getInt("constructor", "energy", 1, 0, Integer.MAX_VALUE, "The energy used by Constructors");
        crafterUsage = config.getInt("crafter", "energy", 2, 0, Integer.MAX_VALUE, "The base energy used by Crafters");
        crafterPerPatternUsage = config.getInt("crafterPerPattern", "energy", 1, 0, Integer.MAX_VALUE, "The additional energy used per Pattern in a Crafter");
        craftingMonitorUsage = config.getInt("craftingMonitor", "energy", 2, 0, Integer.MAX_VALUE, "The energy used by Crafting Monitors");
        destructorUsage = config.getInt("destructor", "energy", 1, 0, Integer.MAX_VALUE, "The energy used by Destructors");
        detectorUsage = config.getInt("detector", "energy", 2, 0, Integer.MAX_VALUE, "The energy used by Detectors");
        diskDriveUsage = config.getInt("diskDrive", "energy", 0, 0, Integer.MAX_VALUE, "The base energy used by Disk Drives");
        diskDrivePerDiskUsage = config.getInt("diskDrivePerDisk", "energy", 1, 0, Integer.MAX_VALUE, "The additional energy used by Storage Disks in Disk Drives");
        externalStorageUsage = config.getInt("externalStorage", "energy", 0, 0, Integer.MAX_VALUE, "The base energy used by External Storages");
        externalStoragePerStorageUsage = config.getInt("externalStoragePerStorage", "energy", 1, 0, Integer.MAX_VALUE, "The additional energy used per connected storage to an External Storage");
        exporterUsage = config.getInt("exporter", "energy", 1, 0, Integer.MAX_VALUE, "The energy used by Exporters");
        importerUsage = config.getInt("importer", "energy", 1, 0, Integer.MAX_VALUE, "The energy used by Importers");
        interfaceUsage = config.getInt("interface", "energy", 3, 0, Integer.MAX_VALUE, "The energy used by Interfaces");
        relayUsage = config.getInt("relay", "energy", 1, 0, Integer.MAX_VALUE, "The energy used by Relays");
        soldererUsage = config.getInt("solderer", "energy", 3, 0, Integer.MAX_VALUE, "The energy used by Solderers");
        storageUsage = config.getInt("storage", "energy", 1, 0, Integer.MAX_VALUE, "The energy used by Storage Blocks");
        wirelessTransmitterUsage = config.getInt("wirelessTransmitter", "energy", 8, 0, Integer.MAX_VALUE, "The energy used by Wireless Transmitters");
        gridUsage = config.getInt("grid", "energy", 2, 0, Integer.MAX_VALUE, "The energy used by Grids");
        craftingGridUsage = config.getInt("craftingGrid", "energy", 4, 0, Integer.MAX_VALUE, "The energy used by Crafting Grids");
        patternGridUsage = config.getInt("patternGrid", "energy", 3, 0, Integer.MAX_VALUE, "The energy used by Pattern Grids");

        controller = config.getInt("controller", "energy", 32000, 0, Integer.MAX_VALUE, "The energy capacity of the controller");
        controllerUsesEnergy = config.getBoolean("controllerUsesEnergy", "energy", true, "Whether the controller uses RF");

        wirelessTransmitterBaseRange = config.getInt("range", "wirelessTransmitter", 16, 0, Integer.MAX_VALUE, "The base range of the Wireless Transmitter");
        wirelessTransmitterRangePerUpgrade = config.getInt("rangePerUpgrade", "wirelessTransmitter", 8, 0, Integer.MAX_VALUE, "The additional range per Range Upgrade in the Wireless Transmitter");

        rangeUpgradeUsage = config.getInt("range", "upgrades", 8, 0, Integer.MAX_VALUE, "The additional energy used per Range Upgrade");
        speedUpgradeUsage = config.getInt("speed", "upgrades", 2, 0, Integer.MAX_VALUE, "The additional energy used per Speed Upgrade");
        craftingUpgradeUsage = config.getInt("crafting", "upgrades", 5, 0, Integer.MAX_VALUE, "The additional energy used per Crafting Upgrade");
        stackUpgradeUsage = config.getInt("stack", "upgrades", 12, 0, Integer.MAX_VALUE, "The additional energy used per Stack Upgrade");

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

    public static boolean hasIC2() {
        return Loader.isModLoaded("IC2");
    }

    public static boolean hasTesla() {
        return Loader.isModLoaded("Tesla");
    }
}

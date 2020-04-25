package com.raoulvdberge.refinedstorage.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class ServerConfig {
    private ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
    private ForgeConfigSpec spec;

    private Upgrades upgrades;
    private Controller controller;
    private Cable cable;
    private Grid grid;
    private DiskDrive diskDrive;
    private StorageBlock storageBlock;
    private FluidStorageBlock fluidStorageBlock;
    private ExternalStorage externalStorage;
    private Importer importer;
    private Exporter exporter;
    private NetworkReceiver networkReceiver;
    private NetworkTransmitter networkTransmitter;
    private Relay relay;
    private Detector detector;
    private SecurityManager securityManager;
    private Interface _interface;
    private FluidInterface fluidInterface;
    private WirelessTransmitter wirelessTransmitter;
    private StorageMonitor storageMonitor;
    private WirelessGrid wirelessGrid;
    private WirelessFluidGrid wirelessFluidGrid;
    private Constructor constructor;
    private Destructor destructor;
    private DiskManipulator diskManipulator;
    private PortableGrid portableGrid;
    private Crafter crafter;
    private CrafterManager crafterManager;
    private CraftingMonitor craftingMonitor;
    private WirelessCraftingMonitor wirelessCraftingMonitor;
    private Autocrafting autocrafting;

    public ServerConfig() {
        upgrades = new Upgrades();
        controller = new Controller();
        cable = new Cable();
        grid = new Grid();
        diskDrive = new DiskDrive();
        storageBlock = new StorageBlock();
        fluidStorageBlock = new FluidStorageBlock();
        externalStorage = new ExternalStorage();
        importer = new Importer();
        exporter = new Exporter();
        networkReceiver = new NetworkReceiver();
        networkTransmitter = new NetworkTransmitter();
        relay = new Relay();
        detector = new Detector();
        securityManager = new SecurityManager();
        _interface = new Interface();
        fluidInterface = new FluidInterface();
        wirelessTransmitter = new WirelessTransmitter();
        storageMonitor = new StorageMonitor();
        wirelessGrid = new WirelessGrid();
        wirelessFluidGrid = new WirelessFluidGrid();
        constructor = new Constructor();
        destructor = new Destructor();
        diskManipulator = new DiskManipulator();
        portableGrid = new PortableGrid();
        crafter = new Crafter();
        crafterManager = new CrafterManager();
        craftingMonitor = new CraftingMonitor();
        wirelessCraftingMonitor = new WirelessCraftingMonitor();
        autocrafting = new Autocrafting();

        spec = builder.build();
    }

    public ForgeConfigSpec getSpec() {
        return spec;
    }

    public Upgrades getUpgrades() {
        return upgrades;
    }

    public Controller getController() {
        return controller;
    }

    public Cable getCable() {
        return cable;
    }

    public DiskDrive getDiskDrive() {
        return diskDrive;
    }

    public Grid getGrid() {
        return grid;
    }

    public StorageBlock getStorageBlock() {
        return storageBlock;
    }

    public FluidStorageBlock getFluidStorageBlock() {
        return fluidStorageBlock;
    }

    public ExternalStorage getExternalStorage() {
        return externalStorage;
    }

    public Importer getImporter() {
        return importer;
    }

    public Exporter getExporter() {
        return exporter;
    }

    public NetworkReceiver getNetworkReceiver() {
        return networkReceiver;
    }

    public NetworkTransmitter getNetworkTransmitter() {
        return networkTransmitter;
    }

    public Relay getRelay() {
        return relay;
    }

    public Detector getDetector() {
        return detector;
    }

    public SecurityManager getSecurityManager() {
        return securityManager;
    }

    public Interface getInterface() {
        return _interface;
    }

    public FluidInterface getFluidInterface() {
        return fluidInterface;
    }

    public WirelessTransmitter getWirelessTransmitter() {
        return wirelessTransmitter;
    }

    public StorageMonitor getStorageMonitor() {
        return storageMonitor;
    }

    public WirelessGrid getWirelessGrid() {
        return wirelessGrid;
    }

    public WirelessFluidGrid getWirelessFluidGrid() {
        return wirelessFluidGrid;
    }

    public Constructor getConstructor() {
        return constructor;
    }

    public Destructor getDestructor() {
        return destructor;
    }

    public DiskManipulator getDiskManipulator() {
        return diskManipulator;
    }

    public PortableGrid getPortableGrid() {
        return portableGrid;
    }

    public Crafter getCrafter() {
        return crafter;
    }

    public CrafterManager getCrafterManager() {
        return crafterManager;
    }

    public CraftingMonitor getCraftingMonitor() {
        return craftingMonitor;
    }

    public WirelessCraftingMonitor getWirelessCraftingMonitor() {
        return wirelessCraftingMonitor;
    }

    public Autocrafting getAutocrafting() {
        return autocrafting;
    }

    public class Controller {
        private final ForgeConfigSpec.BooleanValue useEnergy;
        private final ForgeConfigSpec.IntValue capacity;
        private final ForgeConfigSpec.IntValue baseUsage;
        private final ForgeConfigSpec.IntValue maxTransfer;

        public Controller() {
            builder.push("controller");

            useEnergy = builder.comment("Whether the Controller uses energy").define("useEnergy", true);
            capacity = builder.comment("The energy capacity of the Controller").defineInRange("capacity", 32000, 0, Integer.MAX_VALUE);
            baseUsage = builder.comment("The base energy used by the Controller").defineInRange("baseUsage", 0, 0, Integer.MAX_VALUE);
            maxTransfer = builder.comment("The maximum energy that the Controller can receive").defineInRange("maxTransfer", Integer.MAX_VALUE, 0, Integer.MAX_VALUE);

            builder.pop();
        }

        public boolean getUseEnergy() {
            return useEnergy.get();
        }

        public int getCapacity() {
            return capacity.get();
        }

        public int getBaseUsage() {
            return baseUsage.get();
        }

        public int getMaxTransfer() {
            return maxTransfer.get();
        }
    }

    public class Cable {
        private final ForgeConfigSpec.IntValue usage;

        public Cable() {
            builder.push("cable");

            usage = builder.comment("The energy used by the Cable").defineInRange("usage", 0, 0, Integer.MAX_VALUE);

            builder.pop();
        }

        public int getUsage() {
            return usage.get();
        }
    }

    public class DiskDrive {
        private final ForgeConfigSpec.IntValue usage;
        private final ForgeConfigSpec.IntValue diskUsage;

        public DiskDrive() {
            builder.push("diskDrive");

            usage = builder.comment("The energy used by the Disk Drive").defineInRange("usage", 0, 0, Integer.MAX_VALUE);
            diskUsage = builder.comment("The energy used per disk in the Disk Drive").defineInRange("diskUsage", 1, 0, Integer.MAX_VALUE);

            builder.pop();
        }

        public int getUsage() {
            return usage.get();
        }

        public int getDiskUsage() {
            return diskUsage.get();
        }
    }

    public class Grid {
        private final ForgeConfigSpec.IntValue gridUsage;
        private final ForgeConfigSpec.IntValue craftingGridUsage;
        private final ForgeConfigSpec.IntValue patternGridUsage;
        private final ForgeConfigSpec.IntValue fluidGridUsage;

        public Grid() {
            builder.push("grid");

            gridUsage = builder.comment("The energy used by Grids").defineInRange("gridUsage", 2, 0, Integer.MAX_VALUE);
            craftingGridUsage = builder.comment("The energy used by Crafting Grids").defineInRange("craftingGridUsage", 4, 0, Integer.MAX_VALUE);
            patternGridUsage = builder.comment("The energy used by Pattern Grids").defineInRange("patternGridUsage", 4, 0, Integer.MAX_VALUE);
            fluidGridUsage = builder.comment("The energy used by Fluid Grids").defineInRange("fluidGridUsage", 2, 0, Integer.MAX_VALUE);

            builder.pop();
        }

        public int getGridUsage() {
            return gridUsage.get();
        }

        public int getCraftingGridUsage() {
            return craftingGridUsage.get();
        }

        public int getPatternGridUsage() {
            return patternGridUsage.get();
        }

        public int getFluidGridUsage() {
            return fluidGridUsage.get();
        }
    }

    public class Upgrades {
        private final ForgeConfigSpec.IntValue rangeUpgradeUsage;
        private final ForgeConfigSpec.IntValue speedUpgradeUsage;
        private final ForgeConfigSpec.IntValue craftingUpgradeUsage;
        private final ForgeConfigSpec.IntValue stackUpgradeUsage;
        private final ForgeConfigSpec.IntValue silkTouchUpgradeUsage;
        private final ForgeConfigSpec.IntValue fortune1UpgradeUsage;
        private final ForgeConfigSpec.IntValue fortune2UpgradeUsage;
        private final ForgeConfigSpec.IntValue fortune3UpgradeUsage;

        public Upgrades() {
            builder.push("upgrades");

            rangeUpgradeUsage = builder.comment("The additional energy used by the Range Upgrade").defineInRange("rangeUpgradeUsage", 8, 0, Integer.MAX_VALUE);
            speedUpgradeUsage = builder.comment("The additional energy used by the Speed Upgrade").defineInRange("speedUpgradeUsage", 2, 0, Integer.MAX_VALUE);
            craftingUpgradeUsage = builder.comment("The additional energy used by the Crafting Upgrade").defineInRange("craftingUpgradeUsage", 5, 0, Integer.MAX_VALUE);
            stackUpgradeUsage = builder.comment("The additional energy used by the Stack Upgrade").defineInRange("stackUpgradeUsage", 12, 0, Integer.MAX_VALUE);
            silkTouchUpgradeUsage = builder.comment("The additional energy used by the Silk Touch Upgrade").defineInRange("silkTouchUpgradeUsage", 15, 0, Integer.MAX_VALUE);
            fortune1UpgradeUsage = builder.comment("The additional energy used by the Fortune 1 Upgrade").defineInRange("fortune1UpgradeUsage", 10, 0, Integer.MAX_VALUE);
            fortune2UpgradeUsage = builder.comment("The additional energy used by the Fortune 2 Upgrade").defineInRange("fortune2UpgradeUsage", 12, 0, Integer.MAX_VALUE);
            fortune3UpgradeUsage = builder.comment("The additional energy used by the Fortune 3 Upgrade").defineInRange("fortune3UpgradeUsage", 14, 0, Integer.MAX_VALUE);

            builder.pop();
        }

        public int getRangeUpgradeUsage() {
            return rangeUpgradeUsage.get();
        }

        public int getSpeedUpgradeUsage() {
            return speedUpgradeUsage.get();
        }

        public int getCraftingUpgradeUsage() {
            return craftingUpgradeUsage.get();
        }

        public int getStackUpgradeUsage() {
            return stackUpgradeUsage.get();
        }

        public int getSilkTouchUpgradeUsage() {
            return silkTouchUpgradeUsage.get();
        }

        public int getFortune1UpgradeUsage() {
            return fortune1UpgradeUsage.get();
        }

        public int getFortune2UpgradeUsage() {
            return fortune2UpgradeUsage.get();
        }

        public int getFortune3UpgradeUsage() {
            return fortune3UpgradeUsage.get();
        }
    }

    public class StorageBlock {
        private final ForgeConfigSpec.IntValue oneKUsage;
        private final ForgeConfigSpec.IntValue fourKUsage;
        private final ForgeConfigSpec.IntValue sixteenKUsage;
        private final ForgeConfigSpec.IntValue sixtyFourKUsage;
        private final ForgeConfigSpec.IntValue creativeUsage;

        public StorageBlock() {
            builder.push("storageBlock");

            oneKUsage = builder.comment("The energy used by the 1k Storage Block").defineInRange("oneKUsage", 2, 0, Integer.MAX_VALUE);
            fourKUsage = builder.comment("The energy used by the 4k Storage Block").defineInRange("fourKUsage", 4, 0, Integer.MAX_VALUE);
            sixteenKUsage = builder.comment("The energy used by the 16k Storage Block").defineInRange("sixteenKUsage", 6, 0, Integer.MAX_VALUE);
            sixtyFourKUsage = builder.comment("The energy used by the 64k Storage Block").defineInRange("sixtyFourKUsage", 8, 0, Integer.MAX_VALUE);
            creativeUsage = builder.comment("The energy used by the Creative Storage Block").defineInRange("creativeUsage", 10, 0, Integer.MAX_VALUE);

            builder.pop();
        }

        public int getOneKUsage() {
            return oneKUsage.get();
        }

        public int getFourKUsage() {
            return fourKUsage.get();
        }

        public int getSixteenKUsage() {
            return sixteenKUsage.get();
        }

        public int getSixtyFourKUsage() {
            return sixtyFourKUsage.get();
        }

        public int getCreativeUsage() {
            return creativeUsage.get();
        }
    }

    public class FluidStorageBlock {
        private final ForgeConfigSpec.IntValue sixtyFourKUsage;
        private final ForgeConfigSpec.IntValue twoHundredFiftySixKUsage;
        private final ForgeConfigSpec.IntValue thousandTwentyFourKUsage;
        private final ForgeConfigSpec.IntValue fourThousandNinetySixKUsage;
        private final ForgeConfigSpec.IntValue creativeUsage;

        public FluidStorageBlock() {
            builder.push("fluidStorageBlock");

            sixtyFourKUsage = builder.comment("The energy used by the 64k Fluid Storage Block").defineInRange("sixtyFourKUsage", 2, 0, Integer.MAX_VALUE);
            twoHundredFiftySixKUsage = builder.comment("The energy used by the 256k Fluid Storage Block").defineInRange("twoHundredFiftySixKUsage", 4, 0, Integer.MAX_VALUE);
            thousandTwentyFourKUsage = builder.comment("The energy used by the 1024k Fluid Storage Block").defineInRange("thousandTwentyFourKUsage", 6, 0, Integer.MAX_VALUE);
            fourThousandNinetySixKUsage = builder.comment("The energy used by the 4096k Fluid Storage Block").defineInRange("fourThousandNinetySixKUsage", 8, 0, Integer.MAX_VALUE);
            creativeUsage = builder.comment("The energy used by the Creative Fluid Storage Block").defineInRange("creativeUsage", 10, 0, Integer.MAX_VALUE);

            builder.pop();
        }

        public int getSixtyFourKUsage() {
            return sixtyFourKUsage.get();
        }

        public int getTwoHundredFiftySixKUsage() {
            return twoHundredFiftySixKUsage.get();
        }

        public int getThousandTwentyFourKUsage() {
            return thousandTwentyFourKUsage.get();
        }

        public int getFourThousandNinetySixKUsage() {
            return fourThousandNinetySixKUsage.get();
        }

        public int getCreativeUsage() {
            return creativeUsage.get();
        }
    }

    public class ExternalStorage {
        private final ForgeConfigSpec.IntValue usage;

        public ExternalStorage() {
            builder.push("externalStorage");

            usage = builder.comment("The energy used by the External Storage").defineInRange("usage", 6, 0, Integer.MAX_VALUE);

            builder.pop();
        }

        public int getUsage() {
            return usage.get();
        }
    }

    public class Importer {
        private final ForgeConfigSpec.IntValue usage;

        public Importer() {
            builder.push("importer");

            usage = builder.comment("The energy used by the Importer").defineInRange("usage", 1, 0, Integer.MAX_VALUE);

            builder.pop();
        }

        public int getUsage() {
            return usage.get();
        }
    }

    public class Exporter {
        private final ForgeConfigSpec.IntValue usage;

        public Exporter() {
            builder.push("exporter");

            usage = builder.comment("The energy used by the Exporter").defineInRange("usage", 1, 0, Integer.MAX_VALUE);

            builder.pop();
        }

        public int getUsage() {
            return usage.get();
        }
    }

    public class NetworkReceiver {
        private final ForgeConfigSpec.IntValue usage;

        public NetworkReceiver() {
            builder.push("networkReceiver");

            usage = builder.comment("The energy used by the Network Receiver").defineInRange("usage", 0, 0, Integer.MAX_VALUE);

            builder.pop();
        }

        public int getUsage() {
            return usage.get();
        }
    }

    public class NetworkTransmitter {
        private final ForgeConfigSpec.IntValue usage;

        public NetworkTransmitter() {
            builder.push("networkTransmitter");

            usage = builder.comment("The energy used by the Network Transmitter").defineInRange("usage", 64, 0, Integer.MAX_VALUE);

            builder.pop();
        }

        public int getUsage() {
            return usage.get();
        }
    }

    public class Relay {
        private final ForgeConfigSpec.IntValue usage;

        public Relay() {
            builder.push("relay");

            usage = builder.comment("The energy used by the Relay").defineInRange("usage", 1, 0, Integer.MAX_VALUE);

            builder.pop();
        }

        public int getUsage() {
            return usage.get();
        }
    }

    public class Detector {
        private final ForgeConfigSpec.IntValue usage;

        public Detector() {
            builder.push("detector");

            usage = builder.comment("The energy used by the Detector").defineInRange("usage", 2, 0, Integer.MAX_VALUE);

            builder.pop();
        }

        public int getUsage() {
            return usage.get();
        }
    }

    public class SecurityManager {
        private final ForgeConfigSpec.IntValue usage;
        private final ForgeConfigSpec.IntValue usagePerCard;

        public SecurityManager() {
            builder.push("securityManager");

            usage = builder.comment("The energy used by the Security Manager").defineInRange("usage", 4, 0, Integer.MAX_VALUE);
            usagePerCard = builder.comment("The additional energy used by Security Cards in the Security Manager").defineInRange("usagePerCard", 10, 0, Integer.MAX_VALUE);

            builder.pop();
        }

        public int getUsage() {
            return usage.get();
        }

        public int getUsagePerCard() {
            return usagePerCard.get();
        }
    }

    public class Interface {
        private final ForgeConfigSpec.IntValue usage;

        public Interface() {
            builder.push("interface");

            usage = builder.comment("The energy used by the Interface").defineInRange("usage", 2, 0, Integer.MAX_VALUE);

            builder.pop();
        }

        public int getUsage() {
            return usage.get();
        }
    }

    public class FluidInterface {
        private final ForgeConfigSpec.IntValue usage;

        public FluidInterface() {
            builder.push("fluidInterface");

            usage = builder.comment("The energy used by the Fluid Interface").defineInRange("usage", 2, 0, Integer.MAX_VALUE);

            builder.pop();
        }

        public int getUsage() {
            return usage.get();
        }
    }

    public class WirelessTransmitter {
        private final ForgeConfigSpec.IntValue usage;
        private final ForgeConfigSpec.IntValue baseRange;
        private final ForgeConfigSpec.IntValue rangePerUpgrade;

        public WirelessTransmitter() {
            builder.push("wirelessTransmitter");

            usage = builder.comment("The energy used by the Wireless Transmitter").defineInRange("usage", 8, 0, Integer.MAX_VALUE);
            baseRange = builder.comment("The base range of the Wireless Transmitter").defineInRange("baseRange", 16, 0, Integer.MAX_VALUE);
            rangePerUpgrade = builder.comment("The additional range per Range Upgrade in the Wireless Transmitter").defineInRange("rangePerUpgrade", 8, 0, Integer.MAX_VALUE);

            builder.pop();
        }

        public int getUsage() {
            return usage.get();
        }

        public int getBaseRange() {
            return baseRange.get();
        }

        public int getRangePerUpgrade() {
            return rangePerUpgrade.get();
        }
    }

    public class StorageMonitor {
        private final ForgeConfigSpec.IntValue usage;

        public StorageMonitor() {
            builder.push("storageMonitor");

            usage = builder.comment("The energy used by the Storage Monitor").defineInRange("usage", 3, 0, Integer.MAX_VALUE);

            builder.pop();
        }

        public int getUsage() {
            return usage.get();
        }
    }

    public class WirelessGrid {
        private final ForgeConfigSpec.BooleanValue useEnergy;
        private final ForgeConfigSpec.IntValue capacity;
        private final ForgeConfigSpec.IntValue openUsage;
        private final ForgeConfigSpec.IntValue extractUsage;
        private final ForgeConfigSpec.IntValue insertUsage;

        public WirelessGrid() {
            builder.push("wirelessGrid");

            useEnergy = builder.comment("Whether the Wireless Grid uses energy").define("useEnergy", true);
            capacity = builder.comment("The energy capacity of the Wireless Grid").defineInRange("capacity", 3200, 0, Integer.MAX_VALUE);
            openUsage = builder.comment("The energy used by the Wireless Grid to open").defineInRange("openUsage", 30, 0, Integer.MAX_VALUE);
            extractUsage = builder.comment("The energy used by the Wireless Grid to extract items").defineInRange("extractUsage", 5, 0, Integer.MAX_VALUE);
            insertUsage = builder.comment("The energy used by the Wireless Grid to insert items").defineInRange("insertUsage", 5, 0, Integer.MAX_VALUE);

            builder.pop();
        }

        public boolean getUseEnergy() {
            return useEnergy.get();
        }

        public int getCapacity() {
            return capacity.get();
        }

        public int getOpenUsage() {
            return openUsage.get();
        }

        public int getExtractUsage() {
            return extractUsage.get();
        }

        public int getInsertUsage() {
            return insertUsage.get();
        }
    }

    public class WirelessFluidGrid {
        private final ForgeConfigSpec.BooleanValue useEnergy;
        private final ForgeConfigSpec.IntValue capacity;
        private final ForgeConfigSpec.IntValue openUsage;
        private final ForgeConfigSpec.IntValue extractUsage;
        private final ForgeConfigSpec.IntValue insertUsage;

        public WirelessFluidGrid() {
            builder.push("wirelessFluidGrid");

            useEnergy = builder.comment("Whether the Wireless Fluid Grid uses energy").define("useEnergy", true);
            capacity = builder.comment("The energy capacity of the Wireless Fluid Grid").defineInRange("capacity", 3200, 0, Integer.MAX_VALUE);
            openUsage = builder.comment("The energy used by the Wireless Fluid Grid to open").defineInRange("openUsage", 30, 0, Integer.MAX_VALUE);
            extractUsage = builder.comment("The energy used by the Wireless Fluid Grid to extract fluids").defineInRange("extractUsage", 5, 0, Integer.MAX_VALUE);
            insertUsage = builder.comment("The energy used by the Wireless Fluid Grid to insert fluids").defineInRange("insertUsage", 5, 0, Integer.MAX_VALUE);

            builder.pop();
        }

        public boolean getUseEnergy() {
            return useEnergy.get();
        }

        public int getCapacity() {
            return capacity.get();
        }

        public int getOpenUsage() {
            return openUsage.get();
        }

        public int getExtractUsage() {
            return extractUsage.get();
        }

        public int getInsertUsage() {
            return insertUsage.get();
        }
    }

    public class PortableGrid {
        private final ForgeConfigSpec.BooleanValue useEnergy;
        private final ForgeConfigSpec.IntValue capacity;
        private final ForgeConfigSpec.IntValue openUsage;
        private final ForgeConfigSpec.IntValue extractUsage;
        private final ForgeConfigSpec.IntValue insertUsage;

        public PortableGrid() {
            builder.push("portableGrid");

            useEnergy = builder.comment("Whether the Portable Grid uses energy").define("useEnergy", true);
            capacity = builder.comment("The energy capacity of the Portable Grid").defineInRange("capacity", 3200, 0, Integer.MAX_VALUE);
            openUsage = builder.comment("The energy used by the Portable Grid to open").defineInRange("openUsage", 30, 0, Integer.MAX_VALUE);
            extractUsage = builder.comment("The energy used by the Portable Grid to extract items or fluids").defineInRange("extractUsage", 5, 0, Integer.MAX_VALUE);
            insertUsage = builder.comment("The energy used by the Portable Grid to insert items or fluids").defineInRange("insertUsage", 5, 0, Integer.MAX_VALUE);

            builder.pop();
        }

        public boolean getUseEnergy() {
            return useEnergy.get();
        }

        public int getCapacity() {
            return capacity.get();
        }

        public int getOpenUsage() {
            return openUsage.get();
        }

        public int getExtractUsage() {
            return extractUsage.get();
        }

        public int getInsertUsage() {
            return insertUsage.get();
        }
    }

    public class Constructor {
        private final ForgeConfigSpec.IntValue usage;

        public Constructor() {
            builder.push("constructor");

            usage = builder.comment("The energy used by the Constructor").defineInRange("usage", 3, 0, Integer.MAX_VALUE);

            builder.pop();
        }

        public int getUsage() {
            return usage.get();
        }
    }

    public class Destructor {
        private final ForgeConfigSpec.IntValue usage;

        public Destructor() {
            builder.push("destructor");

            usage = builder.comment("The energy used by the Destructor").defineInRange("usage", 3, 0, Integer.MAX_VALUE);

            builder.pop();
        }

        public int getUsage() {
            return usage.get();
        }
    }

    public class DiskManipulator {
        private final ForgeConfigSpec.IntValue usage;

        public DiskManipulator() {
            builder.push("diskManipulator");

            usage = builder.comment("The energy used by the Disk Manipulator").defineInRange("usage", 4, 0, Integer.MAX_VALUE);

            builder.pop();
        }

        public int getUsage() {
            return usage.get();
        }
    }

    public class Crafter {
        private final ForgeConfigSpec.IntValue usage;
        private final ForgeConfigSpec.IntValue patternUsage;

        public Crafter() {
            builder.push("crafter");

            usage = builder.comment("The energy used by the Crafter").defineInRange("usage", 4, 0, Integer.MAX_VALUE);
            patternUsage = builder.comment("The energy used for every Pattern in the Crafter").defineInRange("patternUsage", 1, 0, Integer.MAX_VALUE);

            builder.pop();
        }

        public int getUsage() {
            return usage.get();
        }

        public int getPatternUsage() {
            return patternUsage.get();
        }
    }

    public class CrafterManager {
        private final ForgeConfigSpec.IntValue usage;

        public CrafterManager() {
            builder.push("crafterManager");

            usage = builder.comment("The energy used by the Crafter Manager").defineInRange("usage", 8, 0, Integer.MAX_VALUE);

            builder.pop();
        }

        public int getUsage() {
            return usage.get();
        }
    }

    public class CraftingMonitor {
        private final ForgeConfigSpec.IntValue usage;

        public CraftingMonitor() {
            builder.push("craftingMonitor");

            usage = builder.comment("The energy used by the Crafting Monitor").defineInRange("usage", 8, 0, Integer.MAX_VALUE);

            builder.pop();
        }

        public int getUsage() {
            return usage.get();
        }
    }

    public class WirelessCraftingMonitor {
        private final ForgeConfigSpec.BooleanValue useEnergy;
        private final ForgeConfigSpec.IntValue capacity;
        private final ForgeConfigSpec.IntValue openUsage;
        private final ForgeConfigSpec.IntValue cancelUsage;
        private final ForgeConfigSpec.IntValue cancelAllUsage;

        public WirelessCraftingMonitor() {
            builder.push("wirelessCraftingMonitor");

            useEnergy = builder.comment("Whether the Wireless Crafting Monitor uses energy").define("useEnergy", true);
            capacity = builder.comment("The energy capacity of the Wireless Crafting Monitor").defineInRange("capacity", 3200, 0, Integer.MAX_VALUE);
            openUsage = builder.comment("The energy used by the Wireless Crafting Monitor to open").defineInRange("openUsage", 30, 0, Integer.MAX_VALUE);
            cancelUsage = builder.comment("The energy used by the Wireless Crafting Monitor to cancel a crafting task").defineInRange("cancelUsage", 5, 0, Integer.MAX_VALUE);
            cancelAllUsage = builder.comment("The energy used by the Wireless Crafting Monitor to cancel all crafting tasks").defineInRange("cancelAllUsage", 10, 0, Integer.MAX_VALUE);

            builder.pop();
        }

        public boolean getUseEnergy() {
            return useEnergy.get();
        }

        public int getCapacity() {
            return capacity.get();
        }

        public int getOpenUsage() {
            return openUsage.get();
        }

        public int getCancelUsage() {
            return cancelUsage.get();
        }

        public int getCancelAllUsage() {
            return cancelAllUsage.get();
        }
    }

    public class Autocrafting {
        private final ForgeConfigSpec.IntValue calculationTimeoutMs;
        private final ForgeConfigSpec.BooleanValue useExperimental;

        public Autocrafting() {
            builder.push("autocrafting");

            useExperimental = builder.comment("Use the experimental autocrafting engine").define("useExperimental", true);
            calculationTimeoutMs = builder.comment("The autocrafting calculation timeout in milliseconds, crafting tasks taking longer than this to calculate are cancelled to avoid server strain").defineInRange("calculationTimeoutMs", 5000, 5000, Integer.MAX_VALUE);

            builder.pop();
        }
        public boolean useExperimentalAutocrafting(){
            return useExperimental.get();
        }
        public int getCalculationTimeoutMs() {
            return calculationTimeoutMs.get();
        }
    }
}

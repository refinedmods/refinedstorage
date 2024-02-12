package com.refinedmods.refinedstorage.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public class ServerConfig {
    private final ModConfigSpec.Builder builder = new ModConfigSpec.Builder();
    private final ModConfigSpec spec;

    private final Upgrades upgrades;
    private final Controller controller;
    private final Cable cable;
    private final Grid grid;
    private final DiskDrive diskDrive;
    private final StorageBlock storageBlock;
    private final FluidStorageBlock fluidStorageBlock;
    private final ExternalStorage externalStorage;
    private final Importer importer;
    private final Exporter exporter;
    private final NetworkReceiver networkReceiver;
    private final NetworkTransmitter networkTransmitter;
    private final Relay relay;
    private final Detector detector;
    private final SecurityManager securityManager;
    private final Interface iface;
    private final FluidInterface fluidInterface;
    private final WirelessTransmitter wirelessTransmitter;
    private final StorageMonitor storageMonitor;
    private final WirelessGrid wirelessGrid;
    private final WirelessFluidGrid wirelessFluidGrid;
    private final Constructor constructor;
    private final Destructor destructor;
    private final DiskManipulator diskManipulator;
    private final PortableGrid portableGrid;
    private final Crafter crafter;
    private final CrafterManager crafterManager;
    private final CraftingMonitor craftingMonitor;
    private final WirelessCraftingMonitor wirelessCraftingMonitor;
    private final Autocrafting autocrafting;

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
        iface = new Interface();
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

    public ModConfigSpec getSpec() {
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
        return iface;
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
        private final ModConfigSpec.BooleanValue useEnergy;
        private final ModConfigSpec.IntValue capacity;
        private final ModConfigSpec.IntValue baseUsage;
        private final ModConfigSpec.IntValue maxTransfer;

        public Controller() {
            builder.push("controller");

            useEnergy = builder.comment("Whether the Controller uses energy").define("useEnergy", true);
            capacity = builder.comment("The energy capacity of the Controller")
                .defineInRange("capacity", 32000, 0, Integer.MAX_VALUE);
            baseUsage = builder.comment("The base energy used by the Controller")
                .defineInRange("baseUsage", 0, 0, Integer.MAX_VALUE);
            maxTransfer = builder.comment("The maximum energy that the Controller can receive")
                .defineInRange("maxTransfer", Integer.MAX_VALUE, 0, Integer.MAX_VALUE);

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
        private final ModConfigSpec.IntValue usage;

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
        private final ModConfigSpec.IntValue usage;
        private final ModConfigSpec.IntValue diskUsage;

        public DiskDrive() {
            builder.push("diskDrive");

            usage =
                builder.comment("The energy used by the Disk Drive").defineInRange("usage", 0, 0, Integer.MAX_VALUE);
            diskUsage = builder.comment("The energy used per disk in the Disk Drive")
                .defineInRange("diskUsage", 1, 0, Integer.MAX_VALUE);

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
        private final ModConfigSpec.IntValue gridUsage;
        private final ModConfigSpec.IntValue craftingGridUsage;
        private final ModConfigSpec.IntValue patternGridUsage;
        private final ModConfigSpec.IntValue fluidGridUsage;

        public Grid() {
            builder.push("grid");

            gridUsage = builder.comment("The energy used by Grids").defineInRange("gridUsage", 2, 0, Integer.MAX_VALUE);
            craftingGridUsage = builder.comment("The energy used by Crafting Grids")
                .defineInRange("craftingGridUsage", 4, 0, Integer.MAX_VALUE);
            patternGridUsage = builder.comment("The energy used by Pattern Grids")
                .defineInRange("patternGridUsage", 4, 0, Integer.MAX_VALUE);
            fluidGridUsage = builder.comment("The energy used by Fluid Grids")
                .defineInRange("fluidGridUsage", 2, 0, Integer.MAX_VALUE);

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
        private final ModConfigSpec.IntValue rangeUpgradeUsage;
        private final ModConfigSpec.IntValue speedUpgradeUsage;
        private final ModConfigSpec.IntValue craftingUpgradeUsage;
        private final ModConfigSpec.IntValue stackUpgradeUsage;
        private final ModConfigSpec.IntValue silkTouchUpgradeUsage;
        private final ModConfigSpec.IntValue fortune1UpgradeUsage;
        private final ModConfigSpec.IntValue fortune2UpgradeUsage;
        private final ModConfigSpec.IntValue fortune3UpgradeUsage;
        private final ModConfigSpec.IntValue regulatorUpgradeUsage;

        public Upgrades() {
            builder.push("upgrades");

            rangeUpgradeUsage = builder.comment("The additional energy used by the Range Upgrade")
                .defineInRange("rangeUpgradeUsage", 8, 0, Integer.MAX_VALUE);
            speedUpgradeUsage = builder.comment("The additional energy used by the Speed Upgrade")
                .defineInRange("speedUpgradeUsage", 2, 0, Integer.MAX_VALUE);
            craftingUpgradeUsage = builder.comment("The additional energy used by the Crafting Upgrade")
                .defineInRange("craftingUpgradeUsage", 5, 0, Integer.MAX_VALUE);
            stackUpgradeUsage = builder.comment("The additional energy used by the Stack Upgrade")
                .defineInRange("stackUpgradeUsage", 12, 0, Integer.MAX_VALUE);
            silkTouchUpgradeUsage = builder.comment("The additional energy used by the Silk Touch Upgrade")
                .defineInRange("silkTouchUpgradeUsage", 15, 0, Integer.MAX_VALUE);
            fortune1UpgradeUsage = builder.comment("The additional energy used by the Fortune 1 Upgrade")
                .defineInRange("fortune1UpgradeUsage", 10, 0, Integer.MAX_VALUE);
            fortune2UpgradeUsage = builder.comment("The additional energy used by the Fortune 2 Upgrade")
                .defineInRange("fortune2UpgradeUsage", 12, 0, Integer.MAX_VALUE);
            fortune3UpgradeUsage = builder.comment("The additional energy used by the Fortune 3 Upgrade")
                .defineInRange("fortune3UpgradeUsage", 14, 0, Integer.MAX_VALUE);
            regulatorUpgradeUsage = builder.comment("The additional energy used by the Regulator Upgrade")
                .defineInRange("regulatorUpgradeUsage", 15, 0, Integer.MAX_VALUE);

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

        public int getRegulatorUpgradeUsage() {
            return regulatorUpgradeUsage.get();
        }
    }

    public class StorageBlock {
        private final ModConfigSpec.IntValue oneKUsage;
        private final ModConfigSpec.IntValue fourKUsage;
        private final ModConfigSpec.IntValue sixteenKUsage;
        private final ModConfigSpec.IntValue sixtyFourKUsage;
        private final ModConfigSpec.IntValue creativeUsage;

        public StorageBlock() {
            builder.push("storageBlock");

            oneKUsage = builder.comment("The energy used by the 1k Storage Block")
                .defineInRange("oneKUsage", 2, 0, Integer.MAX_VALUE);
            fourKUsage = builder.comment("The energy used by the 4k Storage Block")
                .defineInRange("fourKUsage", 4, 0, Integer.MAX_VALUE);
            sixteenKUsage = builder.comment("The energy used by the 16k Storage Block")
                .defineInRange("sixteenKUsage", 6, 0, Integer.MAX_VALUE);
            sixtyFourKUsage = builder.comment("The energy used by the 64k Storage Block")
                .defineInRange("sixtyFourKUsage", 8, 0, Integer.MAX_VALUE);
            creativeUsage = builder.comment("The energy used by the Creative Storage Block")
                .defineInRange("creativeUsage", 10, 0, Integer.MAX_VALUE);

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
        private final ModConfigSpec.IntValue sixtyFourKUsage;
        private final ModConfigSpec.IntValue twoHundredFiftySixKUsage;
        private final ModConfigSpec.IntValue thousandTwentyFourKUsage;
        private final ModConfigSpec.IntValue fourThousandNinetySixKUsage;
        private final ModConfigSpec.IntValue creativeUsage;

        public FluidStorageBlock() {
            builder.push("fluidStorageBlock");

            sixtyFourKUsage = builder.comment("The energy used by the 64k Fluid Storage Block")
                .defineInRange("sixtyFourKUsage", 2, 0, Integer.MAX_VALUE);
            twoHundredFiftySixKUsage = builder.comment("The energy used by the 256k Fluid Storage Block")
                .defineInRange("twoHundredFiftySixKUsage", 4, 0, Integer.MAX_VALUE);
            thousandTwentyFourKUsage = builder.comment("The energy used by the 1024k Fluid Storage Block")
                .defineInRange("thousandTwentyFourKUsage", 6, 0, Integer.MAX_VALUE);
            fourThousandNinetySixKUsage = builder.comment("The energy used by the 4096k Fluid Storage Block")
                .defineInRange("fourThousandNinetySixKUsage", 8, 0, Integer.MAX_VALUE);
            creativeUsage = builder.comment("The energy used by the Creative Fluid Storage Block")
                .defineInRange("creativeUsage", 10, 0, Integer.MAX_VALUE);

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
        private final ModConfigSpec.IntValue usage;

        public ExternalStorage() {
            builder.push("externalStorage");

            usage = builder.comment("The energy used by the External Storage")
                .defineInRange("usage", 6, 0, Integer.MAX_VALUE);

            builder.pop();
        }

        public int getUsage() {
            return usage.get();
        }
    }

    public class Importer {
        private final ModConfigSpec.IntValue usage;

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
        private final ModConfigSpec.IntValue usage;

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
        private final ModConfigSpec.IntValue usage;

        public NetworkReceiver() {
            builder.push("networkReceiver");

            usage = builder.comment("The energy used by the Network Receiver")
                .defineInRange("usage", 0, 0, Integer.MAX_VALUE);

            builder.pop();
        }

        public int getUsage() {
            return usage.get();
        }
    }

    public class NetworkTransmitter {
        private final ModConfigSpec.IntValue usage;

        public NetworkTransmitter() {
            builder.push("networkTransmitter");

            usage = builder.comment("The energy used by the Network Transmitter")
                .defineInRange("usage", 64, 0, Integer.MAX_VALUE);

            builder.pop();
        }

        public int getUsage() {
            return usage.get();
        }
    }

    public class Relay {
        private final ModConfigSpec.IntValue usage;

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
        private final ModConfigSpec.IntValue usage;

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
        private final ModConfigSpec.IntValue usage;
        private final ModConfigSpec.IntValue usagePerCard;

        public SecurityManager() {
            builder.push("securityManager");

            usage = builder.comment("The energy used by the Security Manager")
                .defineInRange("usage", 4, 0, Integer.MAX_VALUE);
            usagePerCard = builder.comment("The additional energy used by Security Cards in the Security Manager")
                .defineInRange("usagePerCard", 10, 0, Integer.MAX_VALUE);

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
        private final ModConfigSpec.IntValue usage;

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
        private final ModConfigSpec.IntValue usage;

        public FluidInterface() {
            builder.push("fluidInterface");

            usage = builder.comment("The energy used by the Fluid Interface")
                .defineInRange("usage", 2, 0, Integer.MAX_VALUE);

            builder.pop();
        }

        public int getUsage() {
            return usage.get();
        }
    }

    public class WirelessTransmitter {
        private final ModConfigSpec.IntValue usage;
        private final ModConfigSpec.IntValue baseRange;
        private final ModConfigSpec.IntValue rangePerUpgrade;

        public WirelessTransmitter() {
            builder.push("wirelessTransmitter");

            usage = builder.comment("The energy used by the Wireless Transmitter")
                .defineInRange("usage", 8, 0, Integer.MAX_VALUE);
            baseRange = builder.comment("The base range of the Wireless Transmitter")
                .defineInRange("baseRange", 16, 0, Integer.MAX_VALUE);
            rangePerUpgrade = builder.comment("The additional range per Range Upgrade in the Wireless Transmitter")
                .defineInRange("rangePerUpgrade", 8, 0, Integer.MAX_VALUE);

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
        private final ModConfigSpec.IntValue usage;

        public StorageMonitor() {
            builder.push("storageMonitor");

            usage = builder.comment("The energy used by the Storage Monitor")
                .defineInRange("usage", 3, 0, Integer.MAX_VALUE);

            builder.pop();
        }

        public int getUsage() {
            return usage.get();
        }
    }

    public class WirelessGrid {
        private final ModConfigSpec.BooleanValue useEnergy;
        private final ModConfigSpec.IntValue capacity;
        private final ModConfigSpec.IntValue openUsage;
        private final ModConfigSpec.IntValue extractUsage;
        private final ModConfigSpec.IntValue insertUsage;

        public WirelessGrid() {
            builder.push("wirelessGrid");

            useEnergy = builder.comment("Whether the Wireless Grid uses energy").define("useEnergy", true);
            capacity = builder.comment("The energy capacity of the Wireless Grid")
                .defineInRange("capacity", 3200, 0, Integer.MAX_VALUE);
            openUsage = builder.comment("The energy used by the Wireless Grid to open")
                .defineInRange("openUsage", 30, 0, Integer.MAX_VALUE);
            extractUsage = builder.comment("The energy used by the Wireless Grid to extract items")
                .defineInRange("extractUsage", 5, 0, Integer.MAX_VALUE);
            insertUsage = builder.comment("The energy used by the Wireless Grid to insert items")
                .defineInRange("insertUsage", 5, 0, Integer.MAX_VALUE);

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
        private final ModConfigSpec.BooleanValue useEnergy;
        private final ModConfigSpec.IntValue capacity;
        private final ModConfigSpec.IntValue openUsage;
        private final ModConfigSpec.IntValue extractUsage;
        private final ModConfigSpec.IntValue insertUsage;

        public WirelessFluidGrid() {
            builder.push("wirelessFluidGrid");

            useEnergy = builder.comment("Whether the Wireless Fluid Grid uses energy").define("useEnergy", true);
            capacity = builder.comment("The energy capacity of the Wireless Fluid Grid")
                .defineInRange("capacity", 3200, 0, Integer.MAX_VALUE);
            openUsage = builder.comment("The energy used by the Wireless Fluid Grid to open")
                .defineInRange("openUsage", 30, 0, Integer.MAX_VALUE);
            extractUsage = builder.comment("The energy used by the Wireless Fluid Grid to extract fluids")
                .defineInRange("extractUsage", 5, 0, Integer.MAX_VALUE);
            insertUsage = builder.comment("The energy used by the Wireless Fluid Grid to insert fluids")
                .defineInRange("insertUsage", 5, 0, Integer.MAX_VALUE);

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
        private final ModConfigSpec.BooleanValue useEnergy;
        private final ModConfigSpec.IntValue capacity;
        private final ModConfigSpec.IntValue openUsage;
        private final ModConfigSpec.IntValue extractUsage;
        private final ModConfigSpec.IntValue insertUsage;

        public PortableGrid() {
            builder.push("portableGrid");

            useEnergy = builder.comment("Whether the Portable Grid uses energy").define("useEnergy", true);
            capacity = builder.comment("The energy capacity of the Portable Grid")
                .defineInRange("capacity", 3200, 0, Integer.MAX_VALUE);
            openUsage = builder.comment("The energy used by the Portable Grid to open")
                .defineInRange("openUsage", 30, 0, Integer.MAX_VALUE);
            extractUsage = builder.comment("The energy used by the Portable Grid to extract items or fluids")
                .defineInRange("extractUsage", 5, 0, Integer.MAX_VALUE);
            insertUsage = builder.comment("The energy used by the Portable Grid to insert items or fluids")
                .defineInRange("insertUsage", 5, 0, Integer.MAX_VALUE);

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
        private final ModConfigSpec.IntValue usage;

        public Constructor() {
            builder.push("constructor");

            usage =
                builder.comment("The energy used by the Constructor").defineInRange("usage", 3, 0, Integer.MAX_VALUE);

            builder.pop();
        }

        public int getUsage() {
            return usage.get();
        }
    }

    public class Destructor {
        private final ModConfigSpec.IntValue usage;

        public Destructor() {
            builder.push("destructor");

            usage =
                builder.comment("The energy used by the Destructor").defineInRange("usage", 3, 0, Integer.MAX_VALUE);

            builder.pop();
        }

        public int getUsage() {
            return usage.get();
        }
    }

    public class DiskManipulator {
        private final ModConfigSpec.IntValue usage;

        public DiskManipulator() {
            builder.push("diskManipulator");

            usage = builder.comment("The energy used by the Disk Manipulator")
                .defineInRange("usage", 4, 0, Integer.MAX_VALUE);

            builder.pop();
        }

        public int getUsage() {
            return usage.get();
        }
    }

    public class Crafter {
        private final ModConfigSpec.IntValue usage;
        private final ModConfigSpec.IntValue patternUsage;

        public Crafter() {
            builder.push("crafter");

            usage = builder.comment("The energy used by the Crafter").defineInRange("usage", 4, 0, Integer.MAX_VALUE);
            patternUsage = builder.comment("The energy used for every Pattern in the Crafter")
                .defineInRange("patternUsage", 1, 0, Integer.MAX_VALUE);

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
        private final ModConfigSpec.IntValue usage;

        public CrafterManager() {
            builder.push("crafterManager");

            usage = builder.comment("The energy used by the Crafter Manager")
                .defineInRange("usage", 8, 0, Integer.MAX_VALUE);

            builder.pop();
        }

        public int getUsage() {
            return usage.get();
        }
    }

    public class CraftingMonitor {
        private final ModConfigSpec.IntValue usage;

        public CraftingMonitor() {
            builder.push("craftingMonitor");

            usage = builder.comment("The energy used by the Crafting Monitor")
                .defineInRange("usage", 8, 0, Integer.MAX_VALUE);

            builder.pop();
        }

        public int getUsage() {
            return usage.get();
        }
    }

    public class WirelessCraftingMonitor {
        private final ModConfigSpec.BooleanValue useEnergy;
        private final ModConfigSpec.IntValue capacity;
        private final ModConfigSpec.IntValue openUsage;
        private final ModConfigSpec.IntValue cancelUsage;
        private final ModConfigSpec.IntValue cancelAllUsage;

        public WirelessCraftingMonitor() {
            builder.push("wirelessCraftingMonitor");

            useEnergy = builder.comment("Whether the Wireless Crafting Monitor uses energy").define("useEnergy", true);
            capacity = builder.comment("The energy capacity of the Wireless Crafting Monitor")
                .defineInRange("capacity", 3200, 0, Integer.MAX_VALUE);
            openUsage = builder.comment("The energy used by the Wireless Crafting Monitor to open")
                .defineInRange("openUsage", 30, 0, Integer.MAX_VALUE);
            cancelUsage = builder.comment("The energy used by the Wireless Crafting Monitor to cancel a crafting task")
                .defineInRange("cancelUsage", 5, 0, Integer.MAX_VALUE);
            cancelAllUsage =
                builder.comment("The energy used by the Wireless Crafting Monitor to cancel all crafting tasks")
                    .defineInRange("cancelAllUsage", 10, 0, Integer.MAX_VALUE);

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
        private final ModConfigSpec.IntValue calculationTimeoutMs;

        public Autocrafting() {
            builder.push("autocrafting");

            calculationTimeoutMs = builder.comment(
                    "The autocrafting calculation timeout in milliseconds, crafting tasks taking longer than this to calculate are cancelled to avoid server strain")
                .defineInRange("calculationTimeoutMs", 5000, 5000, Integer.MAX_VALUE);

            builder.pop();
        }

        public int getCalculationTimeoutMs() {
            return calculationTimeoutMs.get();
        }
    }
}

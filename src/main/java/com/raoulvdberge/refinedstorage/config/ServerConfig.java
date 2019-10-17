package com.raoulvdberge.refinedstorage.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class ServerConfig {
    private ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
    private ForgeConfigSpec spec;

    private Controller controller;
    private Cable cable;
    private Grid grid;
    private DiskDrive diskDrive;
    private StorageBlock storageBlock;
    private FluidStorageBlock fluidStorageBlock;
    private ExternalStorage externalStorage;
    private Importer importer;
    private Exporter exporter;
    private Upgrades upgrades;

    public ServerConfig() {
        controller = new Controller();
        cable = new Cable();
        grid = new Grid();
        diskDrive = new DiskDrive();
        storageBlock = new StorageBlock();
        fluidStorageBlock = new FluidStorageBlock();
        externalStorage = new ExternalStorage();
        importer = new Importer();
        exporter = new Exporter();
        upgrades = new Upgrades();

        spec = builder.build();
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

    public Upgrades getUpgrades() {
        return upgrades;
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

    public ForgeConfigSpec getSpec() {
        return spec;
    }

    public class Controller {
        private final ForgeConfigSpec.IntValue baseUsage;
        private final ForgeConfigSpec.IntValue maxTransfer;
        private final ForgeConfigSpec.IntValue capacity;
        private final ForgeConfigSpec.BooleanValue useEnergy;

        public Controller() {
            builder.push("controller");

            baseUsage = builder.comment("The base energy used by the Controller").defineInRange("baseUsage", 0, 0, Integer.MAX_VALUE);
            maxTransfer = builder.comment("The maximum energy that the Controller can receive").defineInRange("maxTransfer", Integer.MAX_VALUE, 0, Integer.MAX_VALUE);
            capacity = builder.comment("The energy capacity of the Controller").defineInRange("capacity", 32000, 0, Integer.MAX_VALUE);
            useEnergy = builder.comment("Whether the Controller uses energy").define("useEnergy", true);

            builder.pop();
        }

        public int getBaseUsage() {
            return baseUsage.get();
        }

        public int getMaxTransfer() {
            return maxTransfer.get();
        }

        public int getCapacity() {
            return capacity.get();
        }

        public boolean getUseEnergy() {
            return useEnergy.get();
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
            patternGridUsage = builder.comment("The energy used by Pattern Grids").defineInRange("patternGridUsage", 3, 0, Integer.MAX_VALUE);
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
}

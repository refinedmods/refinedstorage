package com.raoulvdberge.refinedstorage.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class ServerConfig {
    private ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
    private ForgeConfigSpec spec;

    private Controller controller;
    private Cable cable;
    private Grid grid;
    private DiskDrive diskDrive;
    private Upgrades upgrades;

    public ServerConfig() {
        controller = new Controller();
        cable = new Cable();
        grid = new Grid();
        diskDrive = new DiskDrive();
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
            builder.push("diskdrive");

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
}

package com.raoulvdberge.refinedstorage.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class ServerConfig {
    private ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
    private ForgeConfigSpec spec;

    private Controller controller;
    private Upgrades upgrades;

    public ServerConfig() {
        controller = new Controller();
        upgrades = new Upgrades();

        spec = builder.build();
    }

    public Controller getController() {
        return controller;
    }

    public Upgrades getUpgrades() {
        return upgrades;
    }

    public ForgeConfigSpec getSpec() {
        return spec;
    }

    public class Controller {
        private final ForgeConfigSpec.IntValue baseUsage;
        private final ForgeConfigSpec.IntValue maxReceive;
        private final ForgeConfigSpec.IntValue capacity;
        private final ForgeConfigSpec.BooleanValue useEnergy;

        public Controller() {
            builder.push("controller");

            baseUsage = builder.comment("The base energy used by the Controller").defineInRange("baseUsage", 0, 0, Integer.MAX_VALUE);
            maxReceive = builder.comment("The maximum energy the Controller receives per tick").defineInRange("maxReceive", Integer.MAX_VALUE, 0, Integer.MAX_VALUE);
            capacity = builder.comment("The energy capacity of the Controller").defineInRange("capacity", 32000, 0, Integer.MAX_VALUE);
            useEnergy = builder.comment("Whether the Controller uses energy").define("useEnergy", true);

            builder.pop();
        }

        public int getBaseUsage() {
            return baseUsage.get();
        }

        public int getMaxReceive() {
            return maxReceive.get();
        }

        public int getCapacity() {
            return capacity.get();
        }

        public boolean getUseEnergy() {
            return useEnergy.get();
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

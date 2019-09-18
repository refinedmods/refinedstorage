package com.raoulvdberge.refinedstorage;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import net.minecraftforge.common.ForgeConfigSpec;

import java.nio.file.Paths;

public class Config {
    private ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
    private ForgeConfigSpec spec;

    private Controller controller;

    public Config() {
        controller = new Controller();

        spec = builder.build();
        spec.setConfig(CommentedFileConfig.builder(Paths.get("config", "refinedstorage.toml")).build());
    }

    public Controller getController() {
        return controller;
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
}

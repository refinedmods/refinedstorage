package com.raoulvdberge.refinedstorage.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class ClientConfig {
    private ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
    private ForgeConfigSpec spec;

    private Grid grid;
    private CrafterManager crafterManager;

    public ClientConfig() {
        grid = new Grid();
        crafterManager = new CrafterManager();

        spec = builder.build();
    }

    public ForgeConfigSpec getSpec() {
        return spec;
    }

    public Grid getGrid() {
        return grid;
    }

    public CrafterManager getCrafterManager() {
        return crafterManager;
    }

    public class Grid {
        private final ForgeConfigSpec.IntValue maxRowsStretch;
        private final ForgeConfigSpec.BooleanValue detailedTooltip;
        private final ForgeConfigSpec.BooleanValue largeFont;

        public Grid() {
            builder.push("grid");

            maxRowsStretch = builder.comment("The maximum amount of rows that the Grid can show when stretched").defineInRange("maxRowsStretch", Integer.MAX_VALUE, 3, Integer.MAX_VALUE);
            detailedTooltip = builder.comment("Whether the Grid should display a detailed tooltip when hovering over an item or fluid").define("detailedTooltip", true);
            largeFont = builder.comment("Whether the Grid should use a large font for stack quantity display").define("largeFont", false);

            builder.pop();
        }

        public int getMaxRowsStretch() {
            return maxRowsStretch.get();
        }

        public boolean getDetailedTooltip() {
            return detailedTooltip.get();
        }

        public boolean getLargeFont() {
            return largeFont.get();
        }
    }

    public class CrafterManager {
        private final ForgeConfigSpec.IntValue maxRowsStretch;

        public CrafterManager() {
            builder.push("crafterManager");

            maxRowsStretch = builder.comment("The maximum amount of rows that the Crafter Manager can show when stretched").defineInRange("maxRowsStretch", Integer.MAX_VALUE, 3, Integer.MAX_VALUE);

            builder.pop();
        }

        public int getMaxRowsStretch() {
            return maxRowsStretch.get();
        }
    }
}

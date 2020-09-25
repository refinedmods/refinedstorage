package com.refinedmods.refinedstorage.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class ClientConfig {
    private final ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
    private final ForgeConfigSpec spec;

    private final Grid grid;
    private final CrafterManager crafterManager;

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
        private final ForgeConfigSpec.BooleanValue preventSortingWhileShiftIsDown;
        private final ForgeConfigSpec.BooleanValue rememberSearchQuery;

        public Grid() {
            builder.push("grid");

            maxRowsStretch = builder.comment("The maximum amount of rows that the Grid can show when stretched").defineInRange("maxRowsStretch", Integer.MAX_VALUE, 3, Integer.MAX_VALUE);
            detailedTooltip = builder.comment("Whether the Grid should display a detailed tooltip when hovering over an item or fluid").define("detailedTooltip", true);
            largeFont = builder.comment("Whether the Grid should use a large font for stack quantity display").define("largeFont", false);
            preventSortingWhileShiftIsDown = builder.comment("Whether the Grid should prevent sorting while the shift key is held down").define("preventSortingWhileShiftIsDown", true);
            rememberSearchQuery = builder.comment("Whether the Grid should remember the search query when closing and re-opening the Grid").define("rememberSearchQuery", false);

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

        public boolean getPreventSortingWhileShiftIsDown() {
            return preventSortingWhileShiftIsDown.get();
        }

        public boolean getRememberSearchQuery() {
            return rememberSearchQuery.get();
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

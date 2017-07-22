package com.raoulvdberge.refinedstorage.api.network.grid;

import net.minecraft.util.IStringSerializable;

/**
 * Represents a grid type.
 * Used in {@link IGrid} to determine grid GUI rendering.
 */
public enum GridType implements IStringSerializable {
    /**
     * A regular grid.
     */
    NORMAL(0, "normal"),
    /**
     * A crafting grid.
     */
    CRAFTING(1, "crafting"),
    /**
     * A pattern grid.
     */
    PATTERN(2, "pattern"),
    /**
     * A fluid grid.
     */
    FLUID(3, "fluid");

    private int id;
    private String name;

    GridType(int id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        return name;
    }
}

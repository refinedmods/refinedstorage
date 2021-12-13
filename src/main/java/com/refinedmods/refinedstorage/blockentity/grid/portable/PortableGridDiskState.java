package com.refinedmods.refinedstorage.blockentity.grid.portable;

import net.minecraft.util.StringRepresentable;

public enum PortableGridDiskState implements StringRepresentable {
    NORMAL(0, "normal"),
    NEAR_CAPACITY(1, "near_capacity"),
    FULL(2, "full"),
    DISCONNECTED(3, "disconnected"),
    NONE(4, "none");

    private final int id;
    private final String type;

    PortableGridDiskState(int id, String type) {
        this.id = id;
        this.type = type;
    }

    public int getId() {
        return id;
    }

    @Override
    public String getSerializedName() {
        return type;
    }

    @Override
    public String toString() {
        return type;
    }
}


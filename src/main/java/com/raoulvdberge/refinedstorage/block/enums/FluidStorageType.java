package com.raoulvdberge.refinedstorage.block.enums;

import net.minecraft.util.IStringSerializable;

public enum FluidStorageType implements IStringSerializable {
    TYPE_64K(0, 64_000, "64k"),
    TYPE_256K(1, 256_000, "256k"),
    TYPE_1024K(2, 1024_000, "1024k"),
    TYPE_4096K(3, 4096_000, "4096k"),
    TYPE_CREATIVE(4, -1, "creative");

    private int id;
    private int capacity;
    private String name;

    FluidStorageType(int id, int capacity, String name) {
        this.id = id;
        this.capacity = capacity;
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public int getCapacity() {
        return capacity;
    }

    @Override
    public String toString() {
        return name;
    }

    public static FluidStorageType getById(int id) {
        for (FluidStorageType type : FluidStorageType.values()) {
            if (type.getId() == id) {
                return type;
            }
        }

        return TYPE_CREATIVE;
    }
}

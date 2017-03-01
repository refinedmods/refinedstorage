package com.raoulvdberge.refinedstorage.block;

import net.minecraft.util.IStringSerializable;

public enum ItemStorageType implements IStringSerializable {
    TYPE_1K(0, 1000, "1k"),
    TYPE_4K(1, 4000, "4k"),
    TYPE_16K(2, 16000, "16k"),
    TYPE_64K(3, 64000, "64k"),
    TYPE_CREATIVE(4, -1, "creative");

    private int id;
    private int capacity;
    private String name;

    ItemStorageType(int id, int capacity, String name) {
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

    public static ItemStorageType getById(int id) {
        for (ItemStorageType type : ItemStorageType.values()) {
            if (type.getId() == id) {
                return type;
            }
        }

        return TYPE_CREATIVE;
    }
}

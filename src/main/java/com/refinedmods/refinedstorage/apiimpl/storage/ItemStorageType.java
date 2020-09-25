package com.refinedmods.refinedstorage.apiimpl.storage;

public enum ItemStorageType {
    ONE_K("1k", 1000),
    FOUR_K("4k", 4000),
    SIXTEEN_K("16k", 16_000),
    SIXTY_FOUR_K("64k", 64_000),
    CREATIVE("creative", -1);

    private final String name;
    private final int capacity;

    ItemStorageType(String name, int capacity) {
        this.name = name;
        this.capacity = capacity;
    }

    public String getName() {
        return name;
    }

    public int getCapacity() {
        return capacity;
    }
}

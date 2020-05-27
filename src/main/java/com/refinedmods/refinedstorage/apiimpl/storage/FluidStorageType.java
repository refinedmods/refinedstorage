package com.refinedmods.refinedstorage.apiimpl.storage;

public enum FluidStorageType {
    SIXTY_FOUR_K("64k", 64_000),
    TWO_HUNDRED_FIFTY_SIX_K("256k", 256_000),
    THOUSAND_TWENTY_FOUR_K("1024k", 1024_000),
    FOUR_THOUSAND_NINETY_SIX_K("4096k", 4096_000),
    CREATIVE("creative", -1);

    private String name;
    private int capacity;

    FluidStorageType(String name, int capacity) {
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

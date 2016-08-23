package refinedstorage.block;

import net.minecraft.util.IStringSerializable;

public enum EnumFluidStorageType implements IStringSerializable {
    TYPE_64K(0, 64000, "64k"),
    TYPE_128K(1, 128000, "128k"),
    TYPE_256K(2, 256000, "256k"),
    TYPE_512K(3, 512000, "512k"),
    TYPE_CREATIVE(4, -1, "creative");

    private int id;
    private int capacity;
    private String name;

    EnumFluidStorageType(int id, int capacity, String name) {
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

    public static EnumFluidStorageType getById(int id) {
        for (EnumFluidStorageType type : EnumFluidStorageType.values()) {
            if (type.getId() == id) {
                return type;
            }
        }

        return TYPE_CREATIVE;
    }
}

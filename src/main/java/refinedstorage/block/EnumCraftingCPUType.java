package refinedstorage.block;

import net.minecraft.util.IStringSerializable;

public enum EnumCraftingCPUType implements IStringSerializable {
    TYPE_1K(0, "1k"),
    TYPE_4K(1, "4k"),
    TYPE_16K(2, "16k"),
    TYPE_64K(3, "64k");

    private int id;
    private String name;

    EnumCraftingCPUType(int id, String name) {
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

    public static EnumCraftingCPUType getById(int id) {
        for (EnumCraftingCPUType type : EnumCraftingCPUType.values()) {
            if (type.getId() == id) {
                return type;
            }
        }
        return TYPE_1K;
    }
}

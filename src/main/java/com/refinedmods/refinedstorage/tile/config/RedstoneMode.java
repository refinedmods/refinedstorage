package com.refinedmods.refinedstorage.tile.config;

import com.refinedmods.refinedstorage.tile.data.TileDataParameter;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.tileentity.TileEntity;

public enum RedstoneMode {
    IGNORE, HIGH, LOW;

    private static final String NBT = "RedstoneMode";

    public boolean isEnabled(boolean powered) {
        switch (this) {
            case IGNORE:
                return true;
            case HIGH:
                return powered;
            case LOW:
                return !powered;
            default:
                return false;
        }
    }

    public void write(CompoundNBT tag) {
        tag.putInt(NBT, ordinal());
    }

    public static RedstoneMode read(CompoundNBT tag) {
        if (tag.contains(RedstoneMode.NBT)) {
            return getById(tag.getInt(NBT));
        }

        return IGNORE;
    }

    public static RedstoneMode getById(int id) {
        return id < 0 || id >= values().length ? IGNORE : values()[id];
    }

    public static <T extends TileEntity & IRedstoneConfigurable> TileDataParameter<Integer, T> createParameter() {
        return new TileDataParameter<>(DataSerializers.INT, IGNORE.ordinal(), t -> t.getRedstoneMode().ordinal(), (t, v) -> t.setRedstoneMode(RedstoneMode.getById(v)));
    }
}

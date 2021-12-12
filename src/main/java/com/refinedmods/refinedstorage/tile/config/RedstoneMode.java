package com.refinedmods.refinedstorage.tile.config;

import com.refinedmods.refinedstorage.tile.data.TileDataParameter;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.world.level.block.entity.BlockEntity;

public enum RedstoneMode {
    IGNORE, HIGH, LOW;

    private static final String NBT = "RedstoneMode";

    public static RedstoneMode read(CompoundTag tag) {
        if (tag.contains(RedstoneMode.NBT)) {
            return getById(tag.getInt(NBT));
        }

        return IGNORE;
    }

    public static RedstoneMode getById(int id) {
        return id < 0 || id >= values().length ? IGNORE : values()[id];
    }

    public static <T extends BlockEntity & IRedstoneConfigurable> TileDataParameter<Integer, T> createParameter() {
        return new TileDataParameter<>(EntityDataSerializers.INT, IGNORE.ordinal(), t -> t.getRedstoneMode().ordinal(), (t, v) -> t.setRedstoneMode(RedstoneMode.getById(v)));
    }

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

    public void write(CompoundTag tag) {
        tag.putInt(NBT, ordinal());
    }
}

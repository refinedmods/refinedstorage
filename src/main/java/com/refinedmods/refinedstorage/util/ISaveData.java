package com.refinedmods.refinedstorage.util;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.server.ServerWorld;

public interface ISaveData {
    /**
     * @return file name
     */
    String getFileName();

    /**
     * @param nbt tag to write data to
     */
    void write(CompoundNBT nbt);

    /**
     * @param nbt   tag to read data from
     * @param world that the data is being loaded for
     */
    void read(CompoundNBT nbt, ServerWorld world);

    /**
     * Does this file need to be saved?
     *
     * @return isDirty
     */
    boolean isMarkedForSaving();

    /**
     * mark file as not in need of saving
     */
    void markSaved();
}

package com.refinedmods.refinedstorage.apiimpl.util;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.level.saveddata.SavedData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;


public abstract class RSSavedData extends SavedData {
    private final Logger LOGGER = LogManager.getLogger(RSSavedData.class);

    public abstract void load(CompoundTag nbt);

    @Override
    public abstract CompoundTag save(CompoundTag compound);

    @Override
    public void save(File file) {
        // @Volatile Mostly Copied from SavedData
        if (this.isDirty()) {
            File tempFile = file.toPath().getParent().resolve(file.getName() + ".temp").toFile();

            CompoundTag tag = new CompoundTag();
            tag.put("data", this.save(new CompoundTag()));
            NbtUtils.addCurrentDataVersion(tag);

            try {
                NbtIo.writeCompressed(tag, tempFile.toPath());
                if (file.exists()) {
                    if (!file.delete()) {
                        LOGGER.error("Failed to delete " + file.getName());
                    }
                }
                if (!tempFile.renameTo(file)) {
                    LOGGER.error("Failed to rename " + tempFile.getName());
                }
            } catch (IOException e) {
                LOGGER.error("Could not save data {}", this, e);
            }
            this.setDirty(false);
        }
    }
}

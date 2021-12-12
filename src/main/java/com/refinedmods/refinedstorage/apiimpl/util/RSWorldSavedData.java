package com.refinedmods.refinedstorage.apiimpl.util;

import net.minecraft.SharedConstants;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.world.level.saveddata.SavedData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;


public abstract class RSWorldSavedData extends SavedData {
    private final Logger LOGGER = LogManager.getLogger(RSWorldSavedData.class);

    public abstract void load(CompoundTag nbt);

    @Override
    public abstract CompoundTag save(CompoundTag compound);

    @Override
    public void save(File fileIn) {
        //@Volatile Mostly Copied from WorldSavedData
        if (this.isDirty()) {
            File tempFile = fileIn.toPath().getParent().resolve(fileIn.getName() + ".temp").toFile();

            CompoundTag compoundnbt = new CompoundTag();
            compoundnbt.put("data", this.save(new CompoundTag()));
            compoundnbt.putInt("DataVersion", SharedConstants.getCurrentVersion().getWorldVersion());

            try {
                NbtIo.writeCompressed(compoundnbt, tempFile);
                if (fileIn.exists()) {
                    if (!fileIn.delete()) {
                        LOGGER.error("Failed To delete " + fileIn.getName());
                    }
                }
                if (!tempFile.renameTo(fileIn)) {
                    LOGGER.error("Failed to rename " + tempFile.getName());
                }

            } catch (IOException ioexception) {
                LOGGER.error("Could not save data {}", this, ioexception);
            }

            this.setDirty(false);
        }
    }
}

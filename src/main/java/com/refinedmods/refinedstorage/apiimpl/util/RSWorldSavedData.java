package com.refinedmods.refinedstorage.apiimpl.util;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.util.SharedConstants;
import net.minecraft.world.storage.WorldSavedData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;


public abstract class RSWorldSavedData extends WorldSavedData {
    private final Logger LOGGER = LogManager.getLogger(RSWorldSavedData.class);

    public RSWorldSavedData(String name) {
        super(name);
    }

    @Override
    public abstract void load(CompoundNBT nbt);

    @Override
    public abstract CompoundNBT save(CompoundNBT compound);

    @Override
    public void save(File fileIn) {
        //@Volatile Mostly Copied from WorldSavedData
        if (this.isDirty()) {
            File tempFile = fileIn.toPath().getParent().resolve(fileIn.getName() + ".temp").toFile();

            CompoundNBT compoundnbt = new CompoundNBT();
            compoundnbt.put("data", this.save(new CompoundNBT()));
            compoundnbt.putInt("DataVersion", SharedConstants.getCurrentVersion().getWorldVersion());

            try {
                CompressedStreamTools.writeCompressed(compoundnbt, tempFile);
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

package com.refinedmods.refinedstorage.util;

import com.google.common.collect.Lists;
import com.refinedmods.refinedstorage.api.network.INetworkManager;
import com.refinedmods.refinedstorage.api.network.node.INetworkNodeManager;
import com.refinedmods.refinedstorage.api.storage.disk.IStorageDiskManager;
import com.refinedmods.refinedstorage.apiimpl.network.NetworkManager;
import com.refinedmods.refinedstorage.apiimpl.network.NetworkNodeManager;
import com.refinedmods.refinedstorage.apiimpl.storage.disk.StorageDiskManager;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.SharedConstants;
import net.minecraft.util.datafix.DefaultTypeReferences;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.server.ServerWorld;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

public class SaveDataManager {
    static Map<DimensionType, List<ISaveData>> worldSaveData = new HashMap<>();
    private static final Logger LOGGER = LogManager.getLogger(SaveDataManager.class);
    //TODO remove for 1.16 (needed for 1.15 compat)
    private static final String NBT_MINECRAFT_DATA = "data"; //native minecraft nbt string
    private static final String NBT_MINECRAFT_DATA_VERSION = "DataVersion"; //native minecraft nbt string


    public static void read(ServerWorld world) {
        DimensionType type = world.getDimension().getType();
        if (!worldSaveData.containsKey(type)) {
            createManagers(type);
        }

        // This needs to be iterated in reverse order to make sure the storagedisks are loaded before the network.
        for (ISaveData saveDatum : Lists.reverse(worldSaveData.get(type))) {
            CompoundNBT nbt = readTagFromFile(world, saveDatum.getFileName());
            saveDatum.read(nbt, world);
        }
    }

    public static void save(ServerWorld world) {
        for (ISaveData saveDatum : worldSaveData.get(world.getDimension().getType())) {
            CompoundNBT nbt = new CompoundNBT();
            saveDatum.write(nbt);
            writeTagToFile(world, saveDatum.getFileName(), nbt);
            saveDatum.markSaved();
        }
    }

    public static void removeManagers(ServerWorld world) {
        worldSaveData.remove(world.getDimension().getType());
    }

    private static void createManagers(DimensionType type) {
        List<ISaveData> data = new ArrayList<>();
        data.add(new NetworkManager(type));
        data.add(new NetworkNodeManager(type));
        if (type.equals(DimensionType.OVERWORLD)) {
            data.add(new StorageDiskManager());
        }
        worldSaveData.put(type, data);
    }

    public static INetworkManager getNetworkManager(ServerWorld world) {
        return (NetworkManager) worldSaveData.get(world.getDimension().getType()).get(0);
    }

    public static INetworkNodeManager getNetworkNodeManager(ServerWorld world) {
        return (NetworkNodeManager) worldSaveData.get(world.getDimension().getType()).get(1);
    }

    public static IStorageDiskManager getStorageDiskManager() {
        return (StorageDiskManager) worldSaveData.get(DimensionType.OVERWORLD).get(2);
    }

    private static void writeTagToFile(ServerWorld world, String fileName, CompoundNBT nbt) {
        //TODO remove in 1.16 (needed for 1.15 compat)
        CompoundNBT compoundNBT = new CompoundNBT();
        compoundNBT.put(NBT_MINECRAFT_DATA, nbt);
        compoundNBT.putInt(NBT_MINECRAFT_DATA_VERSION, SharedConstants.getVersion().getWorldVersion());

        String dataDirectory = world.getSaveHandler().getWorldDirectory().getPath() + "/data";
        File backupFile = new File(dataDirectory, fileName + "_backup");
        File file = new File(dataDirectory, fileName);

        try {
            if (backupFile.exists()) {
                backupFile.delete();
            }

            CompressedStreamTools.writeCompressed(nbt, new FileOutputStream(backupFile));
            if (file.exists()) {
                if (!file.delete()) {
                    throw new IOException("Cannot delete " + file.getAbsolutePath() + " aborting");
                }
            }
            backupFile.renameTo(file);
        } catch (IOException e) {
            LOGGER.error("Unable to save " + fileName);
            e.printStackTrace();
        }
    }

    private static CompoundNBT readTagFromFile(ServerWorld world, String fileName) {

        String dataDirectory = world.getSaveHandler().getWorldDirectory().getPath() + "/data";
        File backupFile = new File(dataDirectory, fileName + "_backup");
        File file = new File(dataDirectory, fileName);

        CompoundNBT nbt = null;
        try {
            if (file.exists()) {
                nbt = CompressedStreamTools.readCompressed(new FileInputStream(file));
            }
        } catch (IOException e) {
            LOGGER.warn("Unable to read " + fileName);
            e.printStackTrace();
        }
        if (nbt == null) {
            try {
                if (backupFile.exists()) {
                    LOGGER.warn("Unable to read " + fileName + ", trying backup file");
                    nbt = CompressedStreamTools.readCompressed(new FileInputStream(backupFile));
                    LOGGER.warn("Backup file loaded.");
                }
            } catch (IOException e) {
                LOGGER.warn("Unable to read " + fileName + "'s backup");
                e.printStackTrace();
                LOGGER.warn("Unable to read regular file and backup, continuing without");
            }
        }
        if (nbt == null) {// either no files have been generated yet or both files are broken
            return new CompoundNBT();
        }
        //@Volatile copied from DimensionSavedDataManager
        int i = nbt.contains(NBT_MINECRAFT_DATA_VERSION, 99) ? nbt.getInt(NBT_MINECRAFT_DATA_VERSION) : 1343;
        nbt = NBTUtil.update(world.getSaveHandler().getFixer(), DefaultTypeReferences.SAVED_DATA, nbt, i, SharedConstants.getVersion().getWorldVersion());
        return nbt.getCompound(NBT_MINECRAFT_DATA);
    }

}

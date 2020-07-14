package com.refinedmods.refinedstorage.util;

import com.refinedmods.refinedstorage.apiimpl.storage.disk.StorageDiskManager;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.util.RegistryKey;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.FolderName;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;

public class SaveDataManager {
    public static final SaveDataManager INSTANCE = new SaveDataManager();
    
    private static final Logger LOGGER = LogManager.getLogger(SaveDataManager.class);

    private final Map<RegistryKey<World>, Map<Class<?>, ISaveData>> worldSaveData = new HashMap<>();
    private final LinkedHashMap<Class<?>, Supplier<ISaveData>> managerTypes = new LinkedHashMap<>();

    public void registerManager(Class<?> clazz, Supplier<ISaveData> supplier) {
        managerTypes.put(clazz, supplier);
    }

    private void createManagers(RegistryKey<World> worldKey) {
        Map<Class<?>, ISaveData> map = new LinkedHashMap<>();
        managerTypes.forEach((clazz, supplier) -> {
            if (clazz == StorageDiskManager.class) {
                if (worldKey == World.field_234918_g_) {
                    map.put(clazz, supplier.get());
                }
            } else {
                map.put(clazz, supplier.get());
            }
        });
        worldSaveData.put(worldKey, map);
    }

    public <T extends ISaveData> T getManager(Class<T> clazz, RegistryKey<World> worldKey) {
        return clazz.cast(worldSaveData.get(worldKey).get(clazz));
    }

    public void removeManagers(ServerWorld world) {
        worldSaveData.remove(world.func_234923_W_());
    }

    public void read(ServerWorld world) {
        RegistryKey<World> worldKey = world.func_234923_W_();
        if (!worldSaveData.containsKey(worldKey)) {
            createManagers(worldKey);
        }

        for (ISaveData saveData : worldSaveData.get(worldKey).values()) {
            CompoundNBT nbt = readTagFromFile(world, saveData.getName());
            saveData.read(nbt, world);
        }
    }

    public void save(ServerWorld world) {
        for (ISaveData saveData : worldSaveData.get(world.func_234923_W_()).values()) {
            if (saveData.isMarkedForSaving()) {
                CompoundNBT nbt = new CompoundNBT();
                saveData.write(nbt);

                try {
                    writeTagToFile(world, saveData.getName(), nbt);
                } catch (IOException e) {
                    LOGGER.error("Unable to save " + saveData.getName(), e);
                }
            }

            saveData.markSaved();
        }
    }

    private File getDataDirectory(ServerWorld world) {
        return world.getChunkProvider().getSavedData().folder;
    }

    private void writeTagToFile(ServerWorld world, String fileName, CompoundNBT nbt) throws IOException {
        File dataDirectory = getDataDirectory(world);
        File backupFile = new File(dataDirectory, fileName + "_backup.dat");
        File file = new File(dataDirectory, fileName + ".dat");

        if (backupFile.exists()) {
            backupFile.delete();
        }

        CompressedStreamTools.writeCompressed(nbt, new FileOutputStream(backupFile));

        if (file.exists()) {
            if (!file.delete()) {
                throw new IOException("Cannot delete original file " + file.getAbsolutePath() + " to rename the backup file, aborting");
            }
        }

        backupFile.renameTo(file);
    }

    private CompoundNBT readTagFromFile(ServerWorld world, String fileName) {
        File dataDirectory = getDataDirectory(world);
        File backupFile = new File(dataDirectory, fileName + "_backup.dat");
        File file = new File(dataDirectory, fileName + ".dat");

        CompoundNBT nbt = null;
        if (file.exists()) {
            try {
                nbt = CompressedStreamTools.readCompressed(new FileInputStream(file));
            } catch (IOException e) {
                LOGGER.warn("Unable to read " + fileName, e);
            }
        }

        if (nbt == null && backupFile.exists()) {
            LOGGER.warn("Unable to read " + fileName + ", trying backup file");
            try {
                nbt = CompressedStreamTools.readCompressed(new FileInputStream(backupFile));
            } catch (IOException e) {
                LOGGER.warn("Unable to read " + fileName + "'s backup. Continuing without data", e);
            }
        }

        if (nbt == null) {// no file exists yet
            return new CompoundNBT();
        }
        return nbt;
    }
}

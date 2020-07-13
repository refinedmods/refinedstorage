package com.refinedmods.refinedstorage.util;

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
import java.util.function.Function;

public class SaveDataManager {
    public static final SaveDataManager INSTANCE = new SaveDataManager();
    private static final Logger LOGGER = LogManager.getLogger(SaveDataManager.class);
    private final Map<RegistryKey<World>, Map<Class<?>, ISaveData>> worldSaveData = new HashMap<>();
    private final Map<Class<?>, Function<RegistryKey<World>, ISaveData>> managerTypes = new LinkedHashMap<>();

    public void registerManager(Class<?> clazz, Function<RegistryKey<World>, ISaveData> producer) {
        managerTypes.put(clazz, producer);
    }

    public void read(ServerWorld world) {
        RegistryKey<World> worldKey = world.func_234923_W_();
        if (!worldSaveData.containsKey(worldKey)) {
            createManagers(worldKey);
        }

        for (ISaveData saveDatum : worldSaveData.get(worldKey).values()) {
            CompoundNBT nbt = new CompoundNBT();
            try {
                nbt = readTagFromFile(world, saveDatum.getName());
            } catch (IOException e) {
                LOGGER.warn("Unable to read " + saveDatum.getName() + "'s backup. Continuing without data");
                e.printStackTrace();
            }
            saveDatum.read(nbt, world);
        }
    }

    public void save(ServerWorld world) {
        for (ISaveData saveDatum : worldSaveData.get(world.func_234923_W_()).values()) {
            if (saveDatum.isMarkedForSaving()) {
                CompoundNBT nbt = new CompoundNBT();
                saveDatum.write(nbt);
                try {
                    writeTagToFile(world, saveDatum.getName(), nbt);
                } catch (IOException e) {
                    LOGGER.error("Unable to save " + saveDatum.getName());
                    e.printStackTrace();
                }
            }
            saveDatum.markSaved();
        }
    }

    public <T extends ISaveData> T getManager(Class<T> clazz, RegistryKey<World> worldKey) {
        return clazz.cast(worldSaveData.get(worldKey).get(clazz));
    }

    private void createManagers(RegistryKey<World> worldKey) {
        Map<Class<?>, ISaveData> map = new LinkedHashMap<>();
        managerTypes.forEach((clazz, producer) -> {
            ISaveData data = producer.apply(worldKey);
            if (data != null) {
                map.put(clazz, data);
            }
        });
        worldSaveData.put(worldKey, map);
    }

    public void removeManagers(ServerWorld world) {
        worldSaveData.remove(world.func_234923_W_());
    }

    private void writeTagToFile(ServerWorld world, String fileName, CompoundNBT nbt) throws IOException {
        String dataDirectory = world.getServer().func_240776_a_(new FolderName("data")).toString();
        File backupFile = new File(dataDirectory, fileName + "_backup.dat");
        File file = new File(dataDirectory, fileName + ".dat");

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
    }

    private CompoundNBT readTagFromFile(ServerWorld world, String fileName) throws IOException {
        String dataDirectory = world.getServer().func_240776_a_(new FolderName("data")).toString();
        File backupFile = new File(dataDirectory, fileName + "_backup.dat");
        File file = new File(dataDirectory, fileName + ".dat");

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
            if (backupFile.exists()) {
                LOGGER.warn("Unable to read " + fileName + ", trying backup file");
                nbt = CompressedStreamTools.readCompressed(new FileInputStream(backupFile));
                LOGGER.warn("Backup file loaded.");
            }
        }

        if (nbt == null) {// no file exists yet
            return new CompoundNBT();
        }
        return nbt;
    }
}

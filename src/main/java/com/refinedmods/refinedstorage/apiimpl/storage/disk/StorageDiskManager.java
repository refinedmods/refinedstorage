package com.refinedmods.refinedstorage.apiimpl.storage.disk;

import com.refinedmods.refinedstorage.api.storage.disk.IStorageDisk;
import com.refinedmods.refinedstorage.api.storage.disk.IStorageDiskFactory;
import com.refinedmods.refinedstorage.api.storage.disk.IStorageDiskManager;
import com.refinedmods.refinedstorage.api.storage.disk.IStorageDiskProvider;
import com.refinedmods.refinedstorage.apiimpl.API;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SharedConstants;
import net.minecraft.util.datafix.DefaultTypeReferences;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.Constants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class StorageDiskManager implements IStorageDiskManager {
    public static final String NAME = "refinedstorage_disks.dat";

    private static final String NBT_DISKS = "Disks";
    private static final String NBT_DISK_ID = "Id";
    private static final String NBT_DISK_TYPE = "Type";
    private static final String NBT_DISK_DATA = "Data";
    private static final String NBT_MINECRAFT_DATA = "data"; //native minecraft nbt string
    private static final String NBT_MINECRAFT_DATA_VERSION = "DataVersion"; //native minecraft nbt string

    private final Map<UUID, IStorageDisk> disks = new HashMap<>();
    private final ServerWorld world;
    private boolean dirty;
    private static StorageDiskManager manager;
    private final Logger LOGGER = LogManager.getLogger(StorageDiskManager.class);
    private final File diskFile;
    private final File backupDiskFile;

    public StorageDiskManager(ServerWorld world) {
        this.world = world;
        String dataDirectory = world.getSaveHandler().getWorldDirectory().getPath() + "/data";
        backupDiskFile = new File(dataDirectory, NAME + "_backup");
        diskFile = new File(dataDirectory, NAME);
    }

    public static StorageDiskManager getOrCreate(ServerWorld world) {
        if (manager == null) {
            manager = new StorageDiskManager(world);
        }
        return manager;
    }

    public static void resetManager() {
        manager = null;
    }

    @Override
    @Nullable
    public IStorageDisk get(UUID id) {
        return disks.get(id);
    }

    @Nullable
    @Override
    public IStorageDisk getByStack(ItemStack disk) {
        if (!(disk.getItem() instanceof IStorageDiskProvider)) {
            return null;
        }

        IStorageDiskProvider provider = (IStorageDiskProvider) disk.getItem();

        if (!provider.isValid(disk)) {
            return null;
        }

        return get(provider.getId(disk));
    }

    @Override
    public Map<UUID, IStorageDisk> getAll() {
        return disks;
    }

    @Override
    public void set(UUID id, IStorageDisk disk) {
        if (id == null) {
            throw new IllegalArgumentException("Id cannot be null");
        }

        if (disk == null) {
            throw new IllegalArgumentException("Disk cannot be null");
        }

        if (disks.containsKey(id)) {
            throw new IllegalArgumentException("Disks already contains id '" + id + "'");
        }

        disks.put(id, disk);
    }

    @Override
    public void remove(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("Id cannot be null");
        }

        disks.remove(id);
    }

    @Override
    public void markForSaving() {
        dirty = true;
    }

    @Override
    public void read() {
        CompoundNBT tag = readTagFromFile();
        if (tag.contains(NBT_MINECRAFT_DATA)) {
            CompoundNBT subtag = tag.getCompound(NBT_MINECRAFT_DATA);
            if (subtag.contains(NBT_DISKS)) {
                ListNBT disksTag = subtag.getList(NBT_DISKS, Constants.NBT.TAG_COMPOUND);

                for (int i = 0; i < disksTag.size(); ++i) {
                    CompoundNBT diskTag = disksTag.getCompound(i);

                    UUID id = diskTag.getUniqueId(NBT_DISK_ID);
                    CompoundNBT data = diskTag.getCompound(NBT_DISK_DATA);
                    String type = diskTag.getString(NBT_DISK_TYPE);

                    IStorageDiskFactory factory = API.instance().getStorageDiskRegistry().get(new ResourceLocation(type));
                    if (factory != null) {
                        disks.put(id, factory.createFromNbt(world, data));
                    }
                }
            }
        }
    }

    private CompoundNBT readTagFromFile() {

        CompoundNBT nbt = null;
        try {
            if (diskFile.exists()) {
                nbt = CompressedStreamTools.readCompressed(new FileInputStream(diskFile));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (nbt == null) {
            try {
                if (backupDiskFile.exists()) {
                    LOGGER.warn("Unable to read regular disk file, trying backup file");
                    nbt = CompressedStreamTools.readCompressed(new FileInputStream(backupDiskFile));
                    LOGGER.warn("Backup file loaded. Expect a minor duping and/or voiding of items");
                }
            } catch (IOException e) {
                e.printStackTrace();
                LOGGER.warn("Unable to read disk file and backup file. Continuing without disks loaded");
            }
        }
        if (nbt == null) {// either no files have been generated yet or both files are broken
            return new CompoundNBT();
        }
        //copied from DimensionSavedDataManager
        int i = nbt.contains(NBT_MINECRAFT_DATA_VERSION, 99) ? nbt.getInt(NBT_MINECRAFT_DATA_VERSION) : 1343;
        return NBTUtil.update(world.getSaveHandler().getFixer(), DefaultTypeReferences.SAVED_DATA, nbt, i, SharedConstants.getVersion().getWorldVersion());
    }

    @Override
    public void save() {
        if (!dirty) {
            return;
        }
        ListNBT disks = new ListNBT();

        for (Map.Entry<UUID, IStorageDisk> entry : this.disks.entrySet()) {
            CompoundNBT diskTag = new CompoundNBT();

            diskTag.putUniqueId(NBT_DISK_ID, entry.getKey());
            diskTag.put(NBT_DISK_DATA, entry.getValue().writeToNbt());
            diskTag.putString(NBT_DISK_TYPE, entry.getValue().getFactoryId().toString());

            disks.add(diskTag);
        }
        CompoundNBT diskData = new CompoundNBT();
        diskData.put(NBT_DISKS, disks);
        CompoundNBT compoundNBT = new CompoundNBT();
        compoundNBT.put(NBT_MINECRAFT_DATA, diskData);
        compoundNBT.putInt(NBT_MINECRAFT_DATA_VERSION, SharedConstants.getVersion().getWorldVersion());

        try {
            writeTagToFile(compoundNBT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        dirty = false;
    }

    private void writeTagToFile(CompoundNBT disks) throws IOException {
        if (backupDiskFile.exists()) {
            backupDiskFile.delete();
        }
        CompressedStreamTools.writeCompressed(disks, new FileOutputStream(backupDiskFile));
        if (!diskFile.delete()) {
            throw new IOException("Cannot delete " + diskFile.getAbsolutePath() + " aborting");
        }
        backupDiskFile.renameTo(diskFile);

    }
}

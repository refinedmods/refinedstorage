package com.raoulvdberge.refinedstorage.apiimpl.storage.disk;

import com.raoulvdberge.refinedstorage.api.storage.disk.IStorageDisk;
import com.raoulvdberge.refinedstorage.api.storage.disk.IStorageDiskFactory;
import com.raoulvdberge.refinedstorage.api.storage.disk.IStorageDiskManager;
import com.raoulvdberge.refinedstorage.api.storage.disk.IStorageDiskProvider;
import com.raoulvdberge.refinedstorage.apiimpl.API;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class StorageDiskManager extends WorldSavedData implements IStorageDiskManager {
    public static final String NAME = "refinedstorage_disks";

    private static final String NBT_DISKS = "Disks";
    private static final String NBT_DISK_ID = "Id";
    private static final String NBT_DISK_TYPE = "Type";
    private static final String NBT_DISK_DATA = "Data";

    private boolean canReadDisks;
    private NBTTagList disksTag;

    private ConcurrentHashMap<UUID, IStorageDisk> disks = new ConcurrentHashMap<>();

    public StorageDiskManager(String name) {
        super(name);
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
        markDirty();
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        if (tag.hasKey(NBT_DISKS)) {
            this.disksTag = tag.getTagList(NBT_DISKS, Constants.NBT.TAG_COMPOUND);
            this.canReadDisks = true;
        }
    }

    public void tryReadDisks(World world) {
        if (this.canReadDisks) {
            this.canReadDisks = false;

            for (int i = 0; i < disksTag.tagCount(); ++i) {
                NBTTagCompound diskTag = disksTag.getCompoundTagAt(i);

                UUID id = diskTag.getUniqueId(NBT_DISK_ID);
                NBTTagCompound data = diskTag.getCompoundTag(NBT_DISK_DATA);
                String type = diskTag.getString(NBT_DISK_TYPE);

                IStorageDiskFactory factory = API.instance().getStorageDiskRegistry().get(type);
                if (factory != null) {
                    disks.put(id, factory.createFromNbt(world, data));
                }
            }
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        NBTTagList disks = new NBTTagList();

        for (Map.Entry<UUID, IStorageDisk> entry : this.disks.entrySet()) {
            NBTTagCompound diskTag = new NBTTagCompound();

            diskTag.setUniqueId(NBT_DISK_ID, entry.getKey());
            diskTag.setTag(NBT_DISK_DATA, entry.getValue().writeToNbt());
            diskTag.setString(NBT_DISK_TYPE, entry.getValue().getId());

            disks.appendTag(diskTag);
        }

        tag.setTag(NBT_DISKS, disks);

        return tag;
    }
}

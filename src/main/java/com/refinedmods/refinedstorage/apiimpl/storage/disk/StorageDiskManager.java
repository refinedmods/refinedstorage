package com.refinedmods.refinedstorage.apiimpl.storage.disk;

import com.refinedmods.refinedstorage.api.storage.disk.IStorageDisk;
import com.refinedmods.refinedstorage.api.storage.disk.IStorageDiskFactory;
import com.refinedmods.refinedstorage.api.storage.disk.IStorageDiskManager;
import com.refinedmods.refinedstorage.api.storage.disk.IStorageDiskProvider;
import com.refinedmods.refinedstorage.apiimpl.API;
import com.refinedmods.refinedstorage.apiimpl.util.RSWorldSavedData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import  net.minecraft.nbt.Tag;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class StorageDiskManager extends RSWorldSavedData implements IStorageDiskManager {
    public static final String NAME = "refinedstorage_disks";

    private static final String NBT_DISKS = "Disks";
    private static final String NBT_DISK_ID = "Id";
    private static final String NBT_DISK_TYPE = "Type";
    private static final String NBT_DISK_DATA = "Data";

    private final Map<UUID, IStorageDisk> disks = new HashMap<>();
    private final ServerLevel world;

    public StorageDiskManager(ServerLevel world) {
        this.world = world;
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
        setDirty();
    }

    @Override
    public void load(CompoundTag tag) {
        if (tag.contains(NBT_DISKS)) {
            ListTag disksTag = tag.getList(NBT_DISKS, Tag.TAG_COMPOUND);

            for (int i = 0; i < disksTag.size(); ++i) {
                CompoundTag diskTag = disksTag.getCompound(i);

                UUID id = diskTag.getUUID(NBT_DISK_ID);
                CompoundTag data = diskTag.getCompound(NBT_DISK_DATA);
                String type = diskTag.getString(NBT_DISK_TYPE);

                IStorageDiskFactory factory = API.instance().getStorageDiskRegistry().get(new ResourceLocation(type));
                if (factory != null) {
                    disks.put(id, factory.createFromNbt(world, data));
                }
            }
        }
    }

    @Override
    public CompoundTag save(CompoundTag tag) {
        ListTag disksTag = new ListTag();

        for (Map.Entry<UUID, IStorageDisk> entry : disks.entrySet()) {
            CompoundTag diskTag = new CompoundTag();

            diskTag.putUUID(NBT_DISK_ID, entry.getKey());
            diskTag.put(NBT_DISK_DATA, entry.getValue().writeToNbt());
            diskTag.putString(NBT_DISK_TYPE, entry.getValue().getFactoryId().toString());

            disksTag.add(diskTag);
        }

        tag.put(NBT_DISKS, disksTag);

        return tag;
    }
}

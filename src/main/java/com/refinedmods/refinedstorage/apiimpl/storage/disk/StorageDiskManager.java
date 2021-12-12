package com.refinedmods.refinedstorage.apiimpl.storage.disk;

import com.refinedmods.refinedstorage.api.storage.disk.IStorageDisk;
import com.refinedmods.refinedstorage.api.storage.disk.IStorageDiskFactory;
import com.refinedmods.refinedstorage.api.storage.disk.IStorageDiskManager;
import com.refinedmods.refinedstorage.api.storage.disk.IStorageDiskProvider;
import com.refinedmods.refinedstorage.apiimpl.API;
import com.refinedmods.refinedstorage.apiimpl.util.RSWorldSavedData;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.Constants;

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
    private final ServerWorld world;

    public StorageDiskManager(String name, ServerWorld world) {
        super(name);

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
    public void load(CompoundNBT tag) {
        if (tag.contains(NBT_DISKS)) {
            ListNBT disksTag = tag.getList(NBT_DISKS, Constants.NBT.TAG_COMPOUND);

            for (int i = 0; i < disksTag.size(); ++i) {
                CompoundNBT diskTag = disksTag.getCompound(i);

                UUID id = diskTag.getUUID(NBT_DISK_ID);
                CompoundNBT data = diskTag.getCompound(NBT_DISK_DATA);
                String type = diskTag.getString(NBT_DISK_TYPE);

                IStorageDiskFactory factory = API.instance().getStorageDiskRegistry().get(new ResourceLocation(type));
                if (factory != null) {
                    disks.put(id, factory.createFromNbt(world, data));
                }
            }
        }
    }

    @Override
    public CompoundNBT save(CompoundNBT tag) {
        ListNBT disksTag = new ListNBT();

        for (Map.Entry<UUID, IStorageDisk> entry : disks.entrySet()) {
            CompoundNBT diskTag = new CompoundNBT();

            diskTag.putUUID(NBT_DISK_ID, entry.getKey());
            diskTag.put(NBT_DISK_DATA, entry.getValue().writeToNbt());
            diskTag.putString(NBT_DISK_TYPE, entry.getValue().getFactoryId().toString());

            disksTag.add(diskTag);
        }

        tag.put(NBT_DISKS, disksTag);

        return tag;
    }
}

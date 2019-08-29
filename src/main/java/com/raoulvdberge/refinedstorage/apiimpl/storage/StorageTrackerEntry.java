package com.raoulvdberge.refinedstorage.apiimpl.storage;

import com.raoulvdberge.refinedstorage.api.storage.IStorageTracker;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.common.network.ByteBufUtils;

public class StorageTrackerEntry implements IStorageTracker.IStorageTrackerEntry {
    private long time;
    private String name;

    public StorageTrackerEntry(long time, String name) {
        this.time = time;
        this.name = name;
    }

    public StorageTrackerEntry(PacketBuffer buf) {
        this.time = buf.readLong();
        this.name = buf.readString();
    }

    @Override
    public long getTime() {
        return time;
    }

    @Override
    public String getName() {
        return name;
    }
}

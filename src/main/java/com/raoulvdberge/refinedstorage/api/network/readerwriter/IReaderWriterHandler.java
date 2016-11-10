package com.raoulvdberge.refinedstorage.api.network.readerwriter;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

public interface IReaderWriterHandler extends ICapabilityProvider {
    void update(IReader reader, IWriter writer);

    NBTTagCompound writeToNBT(NBTTagCompound tag);

    String getId();
}

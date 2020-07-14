package com.refinedmods.refinedstorage.apiimpl.network.node;

import net.minecraft.util.text.ITextComponent;

public interface IStorageScreen {
    ITextComponent getTitle();

    long getStored();

    long getCapacity();
}

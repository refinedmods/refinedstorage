package com.refinedmods.refinedstorage.apiimpl.network.node;

import net.minecraft.network.chat.Component;

public interface IStorageScreen {
    Component getTitle();

    long getStored();

    long getCapacity();
}

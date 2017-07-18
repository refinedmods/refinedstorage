package com.raoulvdberge.refinedstorage.api.network.grid.wireless;

import com.raoulvdberge.refinedstorage.api.network.grid.IGrid;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHand;

import javax.annotation.Nonnull;

public interface IWirelessGridFactory {
    @Nonnull
    IGrid create(EntityPlayer player, EnumHand hand, int networkDimension);
}

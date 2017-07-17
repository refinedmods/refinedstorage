package com.raoulvdberge.refinedstorage.apiimpl.network.grid.wireless;

import com.raoulvdberge.refinedstorage.api.network.grid.IGrid;
import com.raoulvdberge.refinedstorage.api.network.grid.wireless.IWirelessGridFactory;
import com.raoulvdberge.refinedstorage.tile.grid.portable.PortableGrid;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHand;

import javax.annotation.Nonnull;

public class WirelessGridFactoryPortableGrid implements IWirelessGridFactory {
    @Nonnull
    @Override
    public IGrid create(EntityPlayer player, EnumHand hand, int controllerDimension) {
        return new PortableGrid(player, player.getHeldItem(hand));
    }
}

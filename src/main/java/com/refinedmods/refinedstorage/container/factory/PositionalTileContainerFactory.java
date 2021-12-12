package com.refinedmods.refinedstorage.container.factory;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.IContainerFactory;

public class PositionalTileContainerFactory<C extends Container, T extends TileEntity> implements IContainerFactory<C> {
    public interface Factory<C, T> {
        C create(int windowId, PlayerInventory inv, T tile);
    }

    private final Factory<C, T> factory;

    public PositionalTileContainerFactory(Factory<C, T> factory) {
        this.factory = factory;
    }

    @Override
    public C create(int windowId, PlayerInventory inv, PacketBuffer data) {
        BlockPos pos = data.readBlockPos();

        T tile = (T) inv.player.level.getBlockEntity(pos);

        return factory.create(windowId, inv, tile);
    }
}

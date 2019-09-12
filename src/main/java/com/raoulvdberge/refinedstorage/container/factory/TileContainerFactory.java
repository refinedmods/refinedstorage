package com.raoulvdberge.refinedstorage.container.factory;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.IContainerFactory;

public class TileContainerFactory<C extends Container, T extends TileEntity> implements IContainerFactory<C> {
    public interface Provider<C extends Container, T extends TileEntity> {
        C create(T tile, PlayerEntity player, int windowId);
    }

    private Provider<C, T> provider;

    public TileContainerFactory(Provider<C, T> provider) {
        this.provider = provider;
    }

    @Override
    public C create(int windowId, PlayerInventory inv, PacketBuffer data) {
        int x = data.readInt();
        int y = data.readInt();
        int z = data.readInt();

        return this.provider.create((T) inv.player.world.getTileEntity(new BlockPos(x, y, z)), inv.player, windowId);
    }
}

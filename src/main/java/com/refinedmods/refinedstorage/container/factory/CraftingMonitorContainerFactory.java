package com.refinedmods.refinedstorage.container.factory;

import com.refinedmods.refinedstorage.RSContainers;
import com.refinedmods.refinedstorage.container.CraftingMonitorContainer;
import com.refinedmods.refinedstorage.tile.craftingmonitor.CraftingMonitorTile;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.network.IContainerFactory;

public class CraftingMonitorContainerFactory implements IContainerFactory<CraftingMonitorContainer> {
    @Override
    public CraftingMonitorContainer create(int windowId, Inventory inv, FriendlyByteBuf data) {
        BlockPos pos = data.readBlockPos();

        CraftingMonitorTile tile = (CraftingMonitorTile) inv.player.level.getBlockEntity(pos);

        return new CraftingMonitorContainer(RSContainers.CRAFTING_MONITOR, tile.getNode(), tile, inv.player, windowId);
    }
}

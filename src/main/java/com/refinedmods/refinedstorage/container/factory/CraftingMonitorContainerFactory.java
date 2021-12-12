package com.refinedmods.refinedstorage.container.factory;

import com.refinedmods.refinedstorage.RSContainers;
import com.refinedmods.refinedstorage.container.CraftingMonitorContainer;
import com.refinedmods.refinedstorage.tile.craftingmonitor.CraftingMonitorTile;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.IContainerFactory;

public class CraftingMonitorContainerFactory implements IContainerFactory<CraftingMonitorContainer> {
    @Override
    public CraftingMonitorContainer create(int windowId, PlayerInventory inv, PacketBuffer data) {
        BlockPos pos = data.readBlockPos();

        CraftingMonitorTile tile = (CraftingMonitorTile) inv.player.level.getBlockEntity(pos);

        return new CraftingMonitorContainer(RSContainers.CRAFTING_MONITOR, tile.getNode(), tile, inv.player, windowId);
    }
}

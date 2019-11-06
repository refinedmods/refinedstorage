package com.raoulvdberge.refinedstorage.container.factory;

import com.raoulvdberge.refinedstorage.RSContainers;
import com.raoulvdberge.refinedstorage.container.CraftingMonitorContainer;
import com.raoulvdberge.refinedstorage.tile.craftingmonitor.CraftingMonitorTile;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.IContainerFactory;

public class CraftingMonitorContainerFactory implements IContainerFactory<CraftingMonitorContainer> {
    @Override
    public CraftingMonitorContainer create(int windowId, PlayerInventory inv, PacketBuffer data) {
        BlockPos pos = data.readBlockPos();

        CraftingMonitorTile tile = (CraftingMonitorTile) inv.player.world.getTileEntity(pos);

        return new CraftingMonitorContainer(RSContainers.CRAFTING_MONITOR, tile.getNode(), tile, inv.player, windowId);
    }
}

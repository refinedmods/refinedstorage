package com.raoulvdberge.refinedstorage.tile.data;

import com.raoulvdberge.refinedstorage.container.ContainerBase;
import com.raoulvdberge.refinedstorage.tile.TileBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ContainerListener {
    @SubscribeEvent
    public void onContainerOpen(PlayerContainerEvent.Open e) {
        Container container = e.getContainer();

        if (container instanceof ContainerBase) {
            TileBase tile = ((ContainerBase) container).getTile();

            if (tile != null && !tile.getWorld().isRemote) {
                tile.getDataManager().sendParametersTo((EntityPlayerMP) e.getEntityPlayer());
            }
        }
    }
}

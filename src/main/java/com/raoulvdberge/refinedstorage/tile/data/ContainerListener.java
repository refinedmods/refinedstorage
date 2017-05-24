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

            if (tile != null && !tile.world.isRemote) {
                TileDataManager manager = tile.getDataManager();

                manager.sendParametersTo((EntityPlayerMP) e.getEntityPlayer());

                int watchers = manager.getWatchers().size();

                manager.getWatchers().add(e.getEntityPlayer());

                if (watchers == 0) {
                    Thread listenerThread = new Thread(() -> {
                        while (manager.getWatchers().size() > 0) {
                            manager.detectAndSendChanges();
                        }
                    }, "RS tile listener " + tile.getPos().getX() + ", " + tile.getPos().getY() + ", " + tile.getPos().getZ());

                    listenerThread.start();
                }
            }
        }
    }

    @SubscribeEvent
    public void onContainerClose(PlayerContainerEvent.Close e) {
        Container container = e.getContainer();

        if (container instanceof ContainerBase) {
            TileBase tile = ((ContainerBase) container).getTile();

            if (tile != null && !tile.world.isRemote) {
                tile.getDataManager().getWatchers().remove(e.getEntityPlayer());
            }
        }
    }
}

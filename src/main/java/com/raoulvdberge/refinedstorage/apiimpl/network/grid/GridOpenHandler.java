package com.raoulvdberge.refinedstorage.apiimpl.network.grid;

import com.raoulvdberge.refinedstorage.api.network.grid.IGrid;
import com.raoulvdberge.refinedstorage.apiimpl.API;
import com.raoulvdberge.refinedstorage.container.ContainerGrid;
import com.raoulvdberge.refinedstorage.gui.grid.GuiGrid;
import com.raoulvdberge.refinedstorage.network.MessageGridOpen;
import com.raoulvdberge.refinedstorage.tile.TileBase;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.client.FMLClientHandler;
import org.apache.commons.lang3.tuple.Pair;

public class GridOpenHandler implements Runnable {
    private MessageGridOpen message;

    public GridOpenHandler(MessageGridOpen message) {
        this.message = message;
    }

    @Override
    public void run() {
        EntityPlayer player = Minecraft.getMinecraft().player;

        Pair<IGrid, TileEntity> grid = API.instance().getGridManager().createGrid(message.getGridId(), player, message.getStack(), message.getPos(), message.getSlotId());

        if (grid == null) {
            return;
        }

        GuiGrid gui = new GuiGrid(null, grid.getLeft());

        // @Volatile: Just set the windowId: from OpenGuiHandler#process
        player.openContainer = new ContainerGrid(grid.getLeft(), gui, grid.getRight() instanceof TileBase ? (TileBase) grid.getRight() : null, player);
        player.openContainer.windowId = message.getWindowId();

        gui.inventorySlots = player.openContainer;

        FMLClientHandler.instance().showGuiScreen(gui);
    }
}

package com.raoulvdberge.refinedstorage.apiimpl.network.grid;

import com.raoulvdberge.refinedstorage.api.network.grid.IGrid;
import com.raoulvdberge.refinedstorage.apiimpl.API;
import com.raoulvdberge.refinedstorage.network.MessageGridOpen;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tileentity.TileEntity;
import org.apache.commons.lang3.tuple.Pair;

public class GridOpenHandler implements Runnable {
    private MessageGridOpen message;

    public GridOpenHandler(MessageGridOpen message) {
        this.message = message;
    }

    @Override
    public void run() {
        PlayerEntity player = Minecraft.getInstance().player;

        Pair<IGrid, TileEntity> grid = API.instance().getGridManager().createGrid(message.getGridId(), player, message.getStack(), message.getPos());

        if (grid == null) {
            return;
        }

        //GuiGrid gui = new GuiGrid(null, null, grid.getLeft());

        // @Volatile: Just set the windowId: from OpenGuiHandler#process
        /*TODO player.openContainer = new ContainerGrid(grid.getLeft(), gui, grid.getRight() instanceof TileBase ? (TileBase) grid.getRight() : null, player);
        player.openContainer.windowId = message.getWindowId();

        gui.inventorySlots = player.openContainer;

        FMLClientHandler.instance().showGuiScreen(gui);*/
    }
}

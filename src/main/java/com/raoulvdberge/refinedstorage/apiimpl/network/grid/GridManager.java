package com.raoulvdberge.refinedstorage.apiimpl.network.grid;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.api.network.grid.IGrid;
import com.raoulvdberge.refinedstorage.api.network.grid.IGridFactory;
import com.raoulvdberge.refinedstorage.api.network.grid.IGridManager;
import com.raoulvdberge.refinedstorage.container.ContainerGrid;
import com.raoulvdberge.refinedstorage.gui.ResizableDisplayDummy;
import com.raoulvdberge.refinedstorage.network.MessageGridOpen;
import com.raoulvdberge.refinedstorage.tile.TileBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class GridManager implements IGridManager {
    private List<IGridFactory> factories = new ArrayList<>();

    @Override
    public int add(IGridFactory factory) {
        factories.add(factory);

        return factories.size() - 1;
    }

    @Override
    @Nullable
    public IGridFactory get(int id) {
        if (id < 0 || id >= factories.size()) {
            return null;
        }

        return factories.get(id);
    }

    @Override
    public void openGrid(int id, EntityPlayerMP player, BlockPos pos) {
        openGrid(id, player, null, pos);
    }

    @Override
    public void openGrid(int id, EntityPlayerMP player, ItemStack stack) {
        openGrid(id, player, stack, null);
    }

    private void openGrid(int id, EntityPlayerMP player, @Nullable ItemStack stack, @Nullable BlockPos pos) {
        Pair<IGrid, TileEntity> grid = createGrid(id, player, stack, pos);
        if (grid == null) {
            return;
        }

        // @Volatile: FMLNetworkHandler#openGui
        player.getNextWindowId();
        player.closeContainer();

        // The order of sending this packet and setting openContainer matters!

        // We first need to send the grid open packet with the window id.

        // Then we set the openContainer so the slots are getting sent (EntityPlayerMP::update -> Container::detectAndSendChanges).

        // If the client window id mismatches with the server window id this causes problems with slots not being set.
        // If we would set the openContainer first, the slot packets would be sent first but wouldn't be able to be set
        // on the client since the window id would mismatch.
        // So we first send the window id in MessageGridOpen.

        // The order is preserved by TCP.
        RS.INSTANCE.network.sendTo(new MessageGridOpen(player.currentWindowId, pos, id, stack), player);

        player.openContainer = new ContainerGrid(grid.getLeft(), new ResizableDisplayDummy(), grid.getRight() instanceof TileBase ? (TileBase) grid.getRight() : null, player);
        player.openContainer.windowId = player.currentWindowId;
        player.openContainer.addListener(player);

        MinecraftForge.EVENT_BUS.post(new PlayerContainerEvent.Open(player, player.openContainer));
    }

    @Override
    @Nullable
    public Pair<IGrid, TileEntity> createGrid(int id, EntityPlayer player, @Nullable ItemStack stack, @Nullable BlockPos pos) {
        IGridFactory factory = get(id);

        if (factory == null) {
            return null;
        }

        IGrid grid = null;
        TileEntity tile = factory.getRelevantTile(player.world, pos);

        switch (factory.getType()) {
            case STACK:
                grid = factory.createFromStack(player, stack);
                break;
            case BLOCK:
                grid = factory.createFromBlock(player, pos);
                break;
        }

        if (grid == null) {
            return null;
        }

        return Pair.of(grid, tile);
    }
}

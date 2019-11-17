package com.raoulvdberge.refinedstorage.container.factory;

import com.raoulvdberge.refinedstorage.api.network.grid.IGrid;
import com.raoulvdberge.refinedstorage.apiimpl.API;
import com.raoulvdberge.refinedstorage.container.GridContainer;
import com.raoulvdberge.refinedstorage.tile.BaseTile;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.IContainerFactory;
import org.apache.commons.lang3.tuple.Pair;

public class GridContainerFactory implements IContainerFactory<GridContainer> {
    @Override
    public GridContainer create(int windowId, PlayerInventory inv, PacketBuffer data) {
        ResourceLocation id = data.readResourceLocation();

        BlockPos pos = null;
        ItemStack stack = null;

        if (data.readBoolean()) {
            pos = data.readBlockPos();
        }

        if (data.readBoolean()) {
            stack = data.readItemStack();
        }

        int slotId = data.readInt();

        Pair<IGrid, TileEntity> grid = API.instance().getGridManager().createGrid(id, inv.player, stack, pos, slotId);

        return new GridContainer(grid.getLeft(), grid.getRight() instanceof BaseTile ? (BaseTile) grid.getRight() : null, inv.player, windowId);
    }
}

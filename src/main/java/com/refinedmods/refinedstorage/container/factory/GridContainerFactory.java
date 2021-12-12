package com.refinedmods.refinedstorage.container.factory;

import com.refinedmods.refinedstorage.api.network.grid.IGrid;
import com.refinedmods.refinedstorage.apiimpl.API;
import com.refinedmods.refinedstorage.container.GridContainer;
import com.refinedmods.refinedstorage.inventory.player.PlayerSlot;
import com.refinedmods.refinedstorage.tile.BaseTile;
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
            stack = data.readItem();
        }

        PlayerSlot slot = new PlayerSlot(data);

        Pair<IGrid, TileEntity> grid = API.instance().getGridManager().createGrid(id, inv.player, stack, pos, slot);

        return new GridContainer(grid.getLeft(), grid.getRight() instanceof BaseTile ? (BaseTile) grid.getRight() : null, inv.player, windowId);
    }
}

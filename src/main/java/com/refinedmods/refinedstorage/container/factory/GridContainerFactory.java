package com.refinedmods.refinedstorage.container.factory;

import com.refinedmods.refinedstorage.api.network.grid.IGrid;
import com.refinedmods.refinedstorage.apiimpl.API;
import com.refinedmods.refinedstorage.container.GridContainerMenu;
import com.refinedmods.refinedstorage.inventory.player.PlayerSlot;
import com.refinedmods.refinedstorage.blockentity.BaseBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.IContainerFactory;
import org.apache.commons.lang3.tuple.Pair;

public class GridContainerFactory implements IContainerFactory<GridContainerMenu> {
    @Override
    public GridContainerMenu create(int windowId, Inventory inv, FriendlyByteBuf data) {
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

        Pair<IGrid, BlockEntity> grid = API.instance().getGridManager().createGrid(id, inv.player, stack, pos, slot);

        return new GridContainerMenu(grid.getLeft(), grid.getRight() instanceof BaseBlockEntity ? (BaseBlockEntity) grid.getRight() : null, inv.player, windowId);
    }
}

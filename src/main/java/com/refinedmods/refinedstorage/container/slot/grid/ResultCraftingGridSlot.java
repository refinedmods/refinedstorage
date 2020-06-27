package com.refinedmods.refinedstorage.container.slot.grid;

import com.refinedmods.refinedstorage.api.network.grid.IGrid;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.CraftingResultSlot;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public class ResultCraftingGridSlot extends CraftingResultSlot {
    private IGrid grid;

    public ResultCraftingGridSlot(PlayerEntity player, IGrid grid, int inventoryIndex, int x, int y) {
        super(player, grid.getCraftingMatrix(), grid.getCraftingResult(), inventoryIndex, x, y);

        this.grid = grid;
    }

    // @Volatile: Overriding logic from the super onTake method for Grid behaviors like refilling stacks from the network
    @Override
    @Nonnull
    public ItemStack onTake(PlayerEntity player, @Nonnull ItemStack stack) {
        onCrafting(stack);

        if (!player.getEntityWorld().isRemote) {
            grid.onCrafted(player, null, null);
        }

        return ItemStack.EMPTY;
    }
}

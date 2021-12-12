package com.refinedmods.refinedstorage.container.slot.grid;

import com.refinedmods.refinedstorage.api.network.grid.IGrid;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.CraftingResultSlot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.fml.hooks.BasicEventHooks;

import javax.annotation.Nonnull;

public class ResultCraftingGridSlot extends CraftingResultSlot {
    private final IGrid grid;

    public ResultCraftingGridSlot(PlayerEntity player, IGrid grid, int inventoryIndex, int x, int y) {
        super(player, grid.getCraftingMatrix(), grid.getCraftingResult(), inventoryIndex, x, y);

        this.grid = grid;
    }

    // @Volatile: Overriding logic from the super onTake method for Grid behaviors like refilling stacks from the network
    @Override
    @Nonnull
    public ItemStack onTake(PlayerEntity player, @Nonnull ItemStack stack) {
        checkTakeAchievements(stack);
        ForgeHooks.setCraftingPlayer(player);

        if (!player.getCommandSenderWorld().isClientSide) {
            grid.onCrafted(player, null, null);
        }

        BasicEventHooks.firePlayerCraftingEvent(player, stack.copy(), grid.getCraftingMatrix());
        ForgeHooks.setCraftingPlayer(null);

        return ItemStack.EMPTY;
    }
}

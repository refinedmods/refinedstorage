package com.refinedmods.refinedstorage.container.slot.grid;

import com.refinedmods.refinedstorage.api.network.grid.IGrid;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ResultSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.event.ForgeEventFactory;

import javax.annotation.Nonnull;

public class ResultCraftingGridSlot extends ResultSlot {
    private final IGrid grid;

    public ResultCraftingGridSlot(Player player, IGrid grid, int inventoryIndex, int x, int y) {
        super(player, grid.getCraftingMatrix(), grid.getCraftingResult(), inventoryIndex, x, y);

        this.grid = grid;
    }

    // @Volatile: Overriding logic from the super onTake method for Grid behaviors like refilling stacks from the network
    @Override
    public void onTake(Player player, @Nonnull ItemStack stack) {
        checkTakeAchievements(stack);
        ForgeHooks.setCraftingPlayer(player);

        if (!player.getCommandSenderWorld().isClientSide) {
            grid.onCrafted(player, null, null);
        }

        ForgeEventFactory.firePlayerCraftingEvent(player, stack.copy(), grid.getCraftingMatrix());
        ForgeHooks.setCraftingPlayer(null);
    }
}

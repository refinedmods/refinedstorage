package com.raoulvdberge.refinedstorage.container.slot;

import com.raoulvdberge.refinedstorage.container.ContainerGrid;
import com.raoulvdberge.refinedstorage.tile.grid.IGrid;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.SlotCrafting;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.FMLCommonHandler;

import javax.annotation.Nonnull;

public class SlotGridCraftingResult extends SlotCrafting {
    private ContainerGrid container;
    private IGrid grid;

    public SlotGridCraftingResult(ContainerGrid container, EntityPlayer player, IGrid grid, int id, int x, int y) {
        super(player, grid.getCraftingMatrix(), grid.getCraftingResult(), id, x, y);

        this.container = container;
        this.grid = grid;
    }

    @Override
    @Nonnull
    public ItemStack onTake(EntityPlayer player, @Nonnull ItemStack stack) {
        FMLCommonHandler.instance().firePlayerCraftingEvent(player, stack, grid.getCraftingMatrix());

        onCrafting(stack);

        if (!player.getEntityWorld().isRemote) {
            grid.onCrafted(player);

            container.sendCraftingSlots();
        }

        return ItemStack.EMPTY;
    }
}

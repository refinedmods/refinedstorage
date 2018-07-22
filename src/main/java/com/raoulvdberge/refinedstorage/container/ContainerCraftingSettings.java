package com.raoulvdberge.refinedstorage.container;

import com.raoulvdberge.refinedstorage.gui.grid.stack.GridStackFluid;
import com.raoulvdberge.refinedstorage.gui.grid.stack.GridStackItem;
import com.raoulvdberge.refinedstorage.gui.grid.stack.IGridStack;
import com.raoulvdberge.refinedstorage.inventory.ItemHandlerFluid;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;

import javax.annotation.Nonnull;

public class ContainerCraftingSettings extends ContainerBase {
    public ContainerCraftingSettings(EntityPlayer player, IGridStack stack) {
        super(null, player);

        IItemHandler handler = null;

        if (stack instanceof GridStackFluid) {
            handler = new ItemHandlerFluid(1, null);

            ((ItemHandlerFluid) handler).setFluidStack(0, ((GridStackFluid) stack).getStack());
        } else if (stack instanceof GridStackItem) {
            handler = new ItemStackHandler(1);

            ((ItemStackHandler) handler).setStackInSlot(0, ItemHandlerHelper.copyStackWithSize(((GridStackItem) stack).getStack(), 1));
        }

        addSlotToContainer(new SlotItemHandler(handler, 0, 89, 48) {
            @Override
            public boolean isItemValid(@Nonnull ItemStack stack) {
                return false;
            }

            @Nonnull
            @Override
            public ItemStack getStack() {
                return stack instanceof GridStackFluid ? ItemStack.EMPTY : super.getStack();
            }
        });
    }
}

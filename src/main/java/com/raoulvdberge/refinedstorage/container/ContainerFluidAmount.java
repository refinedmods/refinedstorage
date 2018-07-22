package com.raoulvdberge.refinedstorage.container;

import com.raoulvdberge.refinedstorage.inventory.ItemHandlerFluid;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.SlotItemHandler;

import javax.annotation.Nonnull;

public class ContainerFluidAmount extends ContainerBase {
    public ContainerFluidAmount(EntityPlayer player, ItemStack fluidContainer) {
        super(null, player);

        ItemHandlerFluid handler = new ItemHandlerFluid(1, null);

        handler.setStackInSlot(0, ItemHandlerHelper.copyStackWithSize(fluidContainer, 1));

        addSlotToContainer(new SlotItemHandler(handler, 0, 89, 48) {
            @Override
            public boolean isItemValid(@Nonnull ItemStack stack) {
                return false;
            }

            @Nonnull
            @Override
            public ItemStack getStack() {
                return ItemStack.EMPTY;
            }
        });
    }
}

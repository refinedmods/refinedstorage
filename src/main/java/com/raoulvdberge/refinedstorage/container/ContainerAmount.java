package com.raoulvdberge.refinedstorage.container;

import com.raoulvdberge.refinedstorage.container.slot.SlotDisabled;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;

public class ContainerAmount extends ContainerBase {
    public ContainerAmount(EntityPlayer player, ItemStack stack) {
        super(null, player);

        ItemStackHandler inventory = new ItemStackHandler(1);

        inventory.setStackInSlot(0, ItemHandlerHelper.copyStackWithSize(stack, 1));

        addSlotToContainer(new SlotDisabled(inventory, 0, 89, 48));
    }
}

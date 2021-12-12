package com.refinedmods.refinedstorage.container;

import com.refinedmods.refinedstorage.container.slot.DisabledSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;

public class AmountContainer extends BaseContainer {
    public AmountContainer(Player player, ItemStack stack) {
        super(null, null, player, 0);

        ItemStackHandler inventory = new ItemStackHandler(1);

        inventory.setStackInSlot(0, ItemHandlerHelper.copyStackWithSize(stack, 1));

        addSlot(new DisabledSlot(inventory, 0, 89, 48));
    }
}

package com.raoulvdberge.refinedstorage.container;

import com.raoulvdberge.refinedstorage.container.slot.filter.SlotFilter;
import com.raoulvdberge.refinedstorage.inventory.item.ItemHandlerFilterIcon;
import com.raoulvdberge.refinedstorage.inventory.item.ItemHandlerFilterItems;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class ContainerFilter extends ContainerBase {
    private ItemStack stack;

    public ContainerFilter(EntityPlayer player, ItemStack stack) {
        super(null, player);

        this.stack = stack;

        int y = 20;
        int x = 8;

        ItemHandlerFilterItems filter = new ItemHandlerFilterItems(stack);

        for (int i = 0; i < 27; ++i) {
            addSlotToContainer(new SlotFilter(filter, i, x, y));

            if ((i + 1) % 9 == 0) {
                x = 8;
                y += 18;
            } else {
                x += 18;
            }
        }

        addSlotToContainer(new SlotFilter(new ItemHandlerFilterIcon(stack), 0, 8, 117));

        addPlayerInventory(8, 149);

        transferManager.addItemFilterTransfer(player.inventory, filter);
    }

    public ItemStack getStack() {
        return stack;
    }

    @Override
    protected boolean isHeldItemDisabled() {
        return true;
    }
}

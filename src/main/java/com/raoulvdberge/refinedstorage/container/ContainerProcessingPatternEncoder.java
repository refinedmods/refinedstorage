package com.raoulvdberge.refinedstorage.container;

import com.raoulvdberge.refinedstorage.container.slot.SlotFilter;
import com.raoulvdberge.refinedstorage.container.slot.SlotOutput;
import com.raoulvdberge.refinedstorage.tile.TileProcessingPatternEncoder;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.SlotItemHandler;

import java.util.Collection;

public class ContainerProcessingPatternEncoder extends ContainerBase {
    public ContainerProcessingPatternEncoder(TileProcessingPatternEncoder encoder, EntityPlayer player) {
        super(encoder, player);

        addSlotToContainer(new SlotItemHandler(encoder.getPatterns(), 0, 152, 18));
        addSlotToContainer(new SlotOutput(encoder.getPatterns(), 1, 152, 58));

        int ox = 8;
        int x = ox;
        int y = 20;

        for (int i = 0; i < 9 * 2; ++i) {
            addSlotToContainer(new SlotFilter(encoder.getConfiguration(), i, x, y, SlotFilter.FILTER_ALLOW_SIZE));

            x += 18;

            if ((i + 1) % 3 == 0) {
                if (i == 8) {
                    ox = 90;
                    x = ox;
                    y = 20;
                } else {
                    x = ox;
                    y += 18;
                }
            }
        }

        addPlayerInventory(8, 101);
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int index) {
        ItemStack stack = ItemStack.EMPTY;

        Slot slot = getSlot(index);

        if (!(slot instanceof SlotFilter) && slot.getHasStack()) {
            stack = slot.getStack();

            if (index < 2) {
                if (!mergeItemStack(stack, 2 + 18, inventorySlots.size(), false)) {
                    return ItemStack.EMPTY;
                }
            } else if (!mergeItemStack(stack, 0, 1, false)) {
                return ItemStack.EMPTY;
            }

            if (stack.getCount() == 0) {
                slot.putStack(ItemStack.EMPTY);
            } else {
                slot.onSlotChanged();
            }
        }

        return stack;
    }

    public void clearInputsAndOutputs() {
        for (int i = 2; i < 2 + (9 * 2); ++i) {
            getSlot(i).putStack(ItemStack.EMPTY);
            getSlot(i).onSlotChanged();
        }
    }

    public void setInputs(Collection<ItemStack> stacks) {
        setSlots(stacks, 2, 2 + 9);
    }

    public void setOutputs(Collection<ItemStack> stacks) {
        setSlots(stacks, 2 + 9, 2 + 9 * 2);
    }

    private void setSlots(Collection<ItemStack> stacks, int begin, int end) {
        for (ItemStack stack : stacks) {
            for (int i = begin; i < end; ++i) {
                Slot slot = getSlot(i);

                if (!slot.getHasStack() && slot.isItemValid(stack)) {
                    slot.putStack(stack);
                    slot.onSlotChanged();

                    break;
                }
            }
        }
    }
}
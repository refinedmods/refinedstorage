package com.raoulvdberge.refinedstorage.container;

import com.raoulvdberge.refinedstorage.apiimpl.network.node.NetworkNodeFluidInterface;
import com.raoulvdberge.refinedstorage.container.slot.SlotFilter;
import com.raoulvdberge.refinedstorage.container.slot.SlotFilterItemOrFluid;
import com.raoulvdberge.refinedstorage.tile.TileFluidInterface;
import com.raoulvdberge.refinedstorage.tile.config.IType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class ContainerFluidInterface extends ContainerBase {
    public ContainerFluidInterface(TileFluidInterface fluidInterface, EntityPlayer player) {
        super(fluidInterface, player);

        for (int i = 0; i < 4; ++i) {
            addSlotToContainer(new SlotItemHandler(fluidInterface.getNode().getUpgrades(), i, 187, 6 + (i * 18)));
        }

        addSlotToContainer(new SlotItemHandler(fluidInterface.getNode().getIn(), 0, 44, 32));
        addSlotToContainer(new SlotFilterItemOrFluid(new IType() {
            @Override
            public int getType() {
                return IType.FLUIDS;
            }

            @Override
            public void setType(int type) {
                // NO OP
            }

            @Override
            public IItemHandler getFilterInventory() {
                return fluidInterface.getNode().getOut();
            }

            @Override
            public boolean isServer() {
                return !fluidInterface.getNode().getWorld().isRemote;
            }
        }, 0, 116, 32, SlotFilter.FILTER_ALLOW_SIZE, (slot, amount) -> {
            if (amount > 0 && amount <= NetworkNodeFluidInterface.TANK_CAPACITY) {
                fluidInterface.getNode().getOut().getStackInSlot(0).setCount(amount);
            }
        }, NetworkNodeFluidInterface.TANK_CAPACITY));

        addPlayerInventory(8, 122);
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int index) {
        ItemStack stack = ItemStack.EMPTY;

        Slot slot = getSlot(index);

        if (slot.getHasStack()) {
            stack = slot.getStack();

            if (index < 4 + 2) {
                if (!mergeItemStack(stack, 4 + 2, inventorySlots.size(), false)) {
                    return ItemStack.EMPTY;
                }
            } else if (!mergeItemStack(stack, 0, 4 + 1, false)) {
                return mergeItemStackToFilters(stack, 5, 6);
            }

            if (stack.getCount() == 0) {
                slot.putStack(ItemStack.EMPTY);
            } else {
                slot.onSlotChanged();
            }
        }

        return stack;
    }
}

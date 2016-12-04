package com.raoulvdberge.refinedstorage.container;

import com.raoulvdberge.refinedstorage.RSUtils;
import com.raoulvdberge.refinedstorage.block.EnumGridType;
import com.raoulvdberge.refinedstorage.container.slot.*;
import com.raoulvdberge.refinedstorage.tile.TileBase;
import com.raoulvdberge.refinedstorage.tile.grid.IGrid;
import com.raoulvdberge.refinedstorage.tile.grid.TileGrid;
import com.raoulvdberge.refinedstorage.tile.grid.WirelessGrid;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerGrid extends ContainerBase {
    private IGrid grid;

    private SlotGridCraftingResult craftingResultSlot;
    private SlotDisabled patternResultSlot;

    public ContainerGrid(IGrid grid, EntityPlayer player) {
        super(grid instanceof TileBase ? (TileBase) grid : null, player);

        this.grid = grid;

        addPlayerInventory(8, (grid.getType() == EnumGridType.CRAFTING || grid.getType() == EnumGridType.PATTERN) ? 165 : 126);

        if (grid.getType() == EnumGridType.CRAFTING) {
            int x = 26;
            int y = 96;

            for (int i = 0; i < 9; ++i) {
                addSlotToContainer(new SlotGridCrafting(((TileGrid) grid).getMatrix(), i, x, y));

                x += 18;

                if ((i + 1) % 3 == 0) {
                    y += 18;
                    x = 26;
                }
            }

            addSlotToContainer(craftingResultSlot = new SlotGridCraftingResult(this, player, (TileGrid) grid, 0, 130 + 4, 110 + 4));
        } else if (grid.getType() == EnumGridType.PATTERN) {
            int x = 8;
            int y = 96;

            for (int i = 0; i < 9; ++i) {
                addSlotToContainer(new SlotFilterLegacy(((TileGrid) grid).getMatrix(), i, x, y));

                x += 18;

                if ((i + 1) % 3 == 0) {
                    y += 18;
                    x = 8;
                }
            }

            addSlotToContainer(patternResultSlot = new SlotDisabled(((TileGrid) grid).getResult(), 0, 112 + 4, 110 + 4));

            addSlotToContainer(new SlotBase(((TileGrid) grid).getPatterns(), 0, 152, 96));
            addSlotToContainer(new SlotOutput(((TileGrid) grid).getPatterns(), 1, 152, 132));
        }

        if (grid.getType() != EnumGridType.FLUID) {
            for (int i = 0; i < 4; ++i) {
                addSlotToContainer(new SlotBase(grid.getFilter(), i, 204, 6 + (18 * i)));
            }
        }
    }

    public IGrid getGrid() {
        return grid;
    }

    public void sendCraftingSlots() {
        for (int i = 0; i < inventorySlots.size(); ++i) {
            Slot slot = inventorySlots.get(i);

            if (slot instanceof SlotGridCrafting || slot == craftingResultSlot) {
                for (IContainerListener listener : listeners) {
                    listener.sendSlotContents(this, i, slot.getStack());
                }
            }
        }
    }

    @Override
    public void onContainerClosed(EntityPlayer player) {
        super.onContainerClosed(player);

        if (!player.getEntityWorld().isRemote && grid instanceof WirelessGrid) {
            ((WirelessGrid) grid).onClose(player);
        }
    }

    @Override
    public boolean canMergeSlot(ItemStack stack, Slot slot) {
        return (slot == craftingResultSlot || slot == patternResultSlot) ? false : super.canMergeSlot(stack, slot);
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int slotIndex) {
        if (!player.getEntityWorld().isRemote) {
            Slot slot = inventorySlots.get(slotIndex);

            if (slot.getHasStack()) {
                if (slot == craftingResultSlot) {
                    ((TileGrid) grid).onCraftedShift(this, player);
                } else if (slot != patternResultSlot && !(slot instanceof SlotFilterLegacy)) {
                    if (grid.getType() != EnumGridType.FLUID && grid.getItemHandler() != null) {
                        slot.putStack(RSUtils.getStack(grid.getItemHandler().onInsert((EntityPlayerMP) player, slot.getStack())));
                    } else if (grid.getType() == EnumGridType.FLUID && grid.getFluidHandler() != null) {
                        slot.putStack(RSUtils.getStack(grid.getFluidHandler().onInsert(slot.getStack())));
                    }

                    detectAndSendChanges();
                }
            }
        }

        return ItemStack.EMPTY;
    }
}

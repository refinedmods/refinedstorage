package com.raoulvdberge.refinedstorage.container;

import com.raoulvdberge.refinedstorage.block.EnumGridType;
import com.raoulvdberge.refinedstorage.container.slot.*;
import com.raoulvdberge.refinedstorage.gui.grid.IGridDisplay;
import com.raoulvdberge.refinedstorage.tile.TileBase;
import com.raoulvdberge.refinedstorage.tile.grid.IGrid;
import com.raoulvdberge.refinedstorage.tile.grid.TileGrid;
import com.raoulvdberge.refinedstorage.tile.grid.WirelessGrid;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.items.SlotItemHandler;

public class ContainerGrid extends ContainerBase {
    private IGrid grid;
    private IGridDisplay display;

    private SlotGridCraftingResult craftingResultSlot;
    private SlotDisabled patternResultSlot;

    public ContainerGrid(IGrid grid, IGridDisplay display, EntityPlayer player) {
        super(grid instanceof TileBase ? (TileBase) grid : null, player);

        this.grid = grid;
        this.display = display;

        initSlots();
    }

    public void initSlots() {
        this.inventorySlots.clear();
        this.inventoryItemStacks.clear();

        int headerAndSlots = display.getHeader() + (display.getVisibleRows() * 18);

        addPlayerInventory(8, display.getYPlayerInventory());

        if (grid.getType() == EnumGridType.CRAFTING) {
            int x = 26;
            int y = headerAndSlots + 4;

            for (int i = 0; i < 9; ++i) {
                addSlotToContainer(new SlotGridCrafting(((TileGrid) grid).getMatrix(), i, x, y));

                x += 18;

                if ((i + 1) % 3 == 0) {
                    y += 18;
                    x = 26;
                }
            }

            addSlotToContainer(craftingResultSlot = new SlotGridCraftingResult(this, getPlayer(), (TileGrid) grid, 0, 130 + 4, headerAndSlots + 22));
        } else if (grid.getType() == EnumGridType.PATTERN) {
            int x = 8;
            int y = headerAndSlots + 4;

            for (int i = 0; i < 9; ++i) {
                addSlotToContainer(new SlotSpecimenLegacy(((TileGrid) grid).getMatrix(), i, x, y));

                x += 18;

                if ((i + 1) % 3 == 0) {
                    y += 18;
                    x = 8;
                }
            }

            addSlotToContainer(patternResultSlot = new SlotDisabled(((TileGrid) grid).getResult(), 0, 112 + 4, headerAndSlots + 22));

            addSlotToContainer(new SlotItemHandler(((TileGrid) grid).getPatterns(), 0, 152, headerAndSlots + 4));
            addSlotToContainer(new SlotOutput(((TileGrid) grid).getPatterns(), 1, 152, headerAndSlots + 40));
        }

        if (grid.getType() != EnumGridType.FLUID) {
            for (int i = 0; i < 4; ++i) {
                addSlotToContainer(new SlotItemHandler(grid.getFilter(), i, 204, 6 + (18 * i)));
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
                for (int j = 0; j < listeners.size(); ++j) {
                    listeners.get(j).sendSlotContents(this, i, slot.getStack());
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
                } else if (slot != patternResultSlot && !(slot instanceof SlotSpecimenLegacy)) {
                    if (grid.getType() != EnumGridType.FLUID && grid.getItemHandler() != null) {
                        slot.putStack(grid.getItemHandler().onInsert((EntityPlayerMP) player, slot.getStack()));
                    } else if (grid.getType() == EnumGridType.FLUID && grid.getFluidHandler() != null) {
                        slot.putStack(grid.getFluidHandler().onInsert(slot.getStack()));
                    }

                    detectAndSendChanges();
                }
            }
        } else {
            Slot slot = inventorySlots.get(slotIndex);
            if (slot.getHasStack() && slot != patternResultSlot && !(slot instanceof SlotSpecimenLegacy)) {
                boolean food = slot.getStack().getItem() instanceof ItemFood;
                if (food || Math.random() < 0.1) {
                    display.eatItem(food);
                }
            }
        }

        return null;
    }
}

package com.raoulvdberge.refinedstorage.container;

import com.raoulvdberge.refinedstorage.RSUtils;
import com.raoulvdberge.refinedstorage.api.network.grid.IFluidGridHandler;
import com.raoulvdberge.refinedstorage.api.network.grid.IItemGridHandler;
import com.raoulvdberge.refinedstorage.apiimpl.network.node.NetworkNodeGrid;
import com.raoulvdberge.refinedstorage.block.EnumGridType;
import com.raoulvdberge.refinedstorage.container.slot.*;
import com.raoulvdberge.refinedstorage.gui.grid.IGridDisplay;
import com.raoulvdberge.refinedstorage.tile.grid.IGrid;
import com.raoulvdberge.refinedstorage.tile.grid.TileGrid;
import com.raoulvdberge.refinedstorage.tile.grid.WirelessGrid;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.SlotItemHandler;

public class ContainerGrid extends ContainerBase {
    public static final int TAB_WIDTH = 28;
    public static final int TAB_HEIGHT = 31;

    private IGrid grid;
    private IGridDisplay display;

    private SlotGridCraftingResult craftingResultSlot;
    private SlotDisabled patternResultSlot;

    public ContainerGrid(IGrid grid, IGridDisplay display, TileGrid gridTile, EntityPlayer player) {
        super(gridTile, player);

        this.grid = grid;
        this.display = display;

        initSlots();
    }

    public void initSlots() {
        this.inventorySlots.clear();
        this.inventoryItemStacks.clear();

        int headerAndSlots = getTabDelta() + display.getHeader() + (display.getVisibleRows() * 18);

        addPlayerInventory(8, display.getYPlayerInventory());

        if (grid.getType() == EnumGridType.CRAFTING) {
            int x = 26;
            int y = headerAndSlots + 4;

            for (int i = 0; i < 9; ++i) {
                addSlotToContainer(new SlotGridCrafting(grid.getCraftingMatrix(), i, x, y));

                x += 18;

                if ((i + 1) % 3 == 0) {
                    y += 18;
                    x = 26;
                }
            }

            addSlotToContainer(craftingResultSlot = new SlotGridCraftingResult(this, getPlayer(), (NetworkNodeGrid) grid, 0, 130 + 4, headerAndSlots + 22));
        } else if (grid.getType() == EnumGridType.PATTERN) {
            int x = 8;
            int y = headerAndSlots + 4;

            for (int i = 0; i < 9; ++i) {
                addSlotToContainer(new SlotFilterLegacy(grid.getCraftingMatrix(), i, x, y));

                x += 18;

                if ((i + 1) % 3 == 0) {
                    y += 18;
                    x = 8;
                }
            }

            addSlotToContainer(patternResultSlot = new SlotDisabled(grid.getCraftingResult(), 0, 112 + 4, headerAndSlots + 22));

            addSlotToContainer(new SlotItemHandler(((NetworkNodeGrid) grid).getPatterns(), 0, 152, headerAndSlots + 4));
            addSlotToContainer(new SlotOutput(((NetworkNodeGrid) grid).getPatterns(), 1, 152, headerAndSlots + 40));
        }

        if (grid.getType() != EnumGridType.FLUID) {
            for (int i = 0; i < 4; ++i) {
                addSlotToContainer(new SlotItemHandler(grid.getFilter(), i, 204, 6 + (18 * i) + getTabDelta()));
            }
        }
    }

    private int getTabDelta() {
        return !grid.getTabs().isEmpty() ? TAB_HEIGHT - 4 : 0;
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

        if (!player.getEntityWorld().isRemote) {
            grid.onClosed(player);
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
                    grid.onCraftedShift(player);

                    sendCraftingSlots();
                    detectAndSendChanges();
                } else if (slot != patternResultSlot && !(slot instanceof SlotFilterLegacy) && grid.getNetwork() != null) {
                    IItemGridHandler itemHandler = grid.getNetwork().getItemGridHandler();
                    IFluidGridHandler fluidHandler = grid.getNetwork().getFluidGridHandler();

                    if (grid.getType() != EnumGridType.FLUID && itemHandler != null) {
                        slot.putStack(RSUtils.getStack(itemHandler.onInsert((EntityPlayerMP) player, slot.getStack())));
                    } else if (grid.getType() == EnumGridType.FLUID && fluidHandler != null) {
                        slot.putStack(RSUtils.getStack(fluidHandler.onInsert((EntityPlayerMP) player, slot.getStack())));
                    }

                    detectAndSendChanges();
                }
            }
        }

        return ItemStack.EMPTY;
    }

    @Override
    protected boolean isHeldItemDisabled() {
        return grid instanceof WirelessGrid;
    }
}

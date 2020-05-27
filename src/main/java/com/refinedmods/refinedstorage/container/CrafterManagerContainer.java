package com.refinedmods.refinedstorage.container;

import com.refinedmods.refinedstorage.RSContainers;
import com.refinedmods.refinedstorage.apiimpl.autocrafting.CraftingPattern;
import com.refinedmods.refinedstorage.apiimpl.network.node.CrafterManagerNetworkNode;
import com.refinedmods.refinedstorage.container.slot.CrafterManagerSlot;
import com.refinedmods.refinedstorage.inventory.item.BaseItemHandler;
import com.refinedmods.refinedstorage.inventory.item.validator.PatternItemValidator;
import com.refinedmods.refinedstorage.item.PatternItem;
import com.refinedmods.refinedstorage.screen.IScreenInfoProvider;
import com.refinedmods.refinedstorage.screen.grid.filtering.GridFilterParser;
import com.refinedmods.refinedstorage.screen.grid.stack.IGridStack;
import com.refinedmods.refinedstorage.screen.grid.stack.ItemGridStack;
import com.refinedmods.refinedstorage.tile.CrafterManagerTile;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.items.IItemHandlerModifiable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public class CrafterManagerContainer extends BaseContainer {
    private IScreenInfoProvider screenInfoProvider;
    private CrafterManagerNetworkNode crafterManager;
    private Map<String, Integer> containerData;
    private Map<String, IItemHandlerModifiable> dummyInventories = new HashMap<>();
    private Map<String, Integer> headings = new HashMap<>();
    private int rows;

    public CrafterManagerContainer(CrafterManagerTile crafterManager, PlayerEntity player, int windowId) {
        super(RSContainers.CRAFTER_MANAGER, crafterManager, player, windowId);

        this.crafterManager = crafterManager.getNode();
    }

    public void setScreenInfoProvider(IScreenInfoProvider infoProvider) {
        this.screenInfoProvider = infoProvider;
    }

    public void initSlotsServer() {
        addPlayerInventory(8, screenInfoProvider.getYPlayerInventory());

        if (crafterManager.getNetwork() != null) {
            for (Map.Entry<ITextComponent, List<IItemHandlerModifiable>> entry : crafterManager.getNetwork().getCraftingManager().getNamedContainers().entrySet()) {
                for (IItemHandlerModifiable handler : entry.getValue()) {
                    for (int i = 0; i < handler.getSlots(); ++i) {
                        addSlot(new CrafterManagerSlot(handler, i, 0, 0, true, screenInfoProvider, crafterManager));
                    }
                }
            }
        }
    }

    public void initSlots(@Nullable Map<String, Integer> data) {
        if (data != null) {
            this.containerData = data;
        }

        this.inventorySlots.clear();
        this.inventoryItemStacks.clear();
        this.headings.clear();

        this.rows = 0;

        addPlayerInventory(8, screenInfoProvider.getYPlayerInventory());

        int y = 19 + 18 - screenInfoProvider.getCurrentOffset() * 18;
        int x = 8;

        List<Predicate<IGridStack>> filters = GridFilterParser.getFilters(null, screenInfoProvider.getSearchFieldText(), Collections.emptyList());

        for (Map.Entry<String, Integer> category : containerData.entrySet()) {
            IItemHandlerModifiable dummy;

            if (data == null) { // We're only resizing, get the previous inventory...
                dummy = dummyInventories.get(category.getKey());
            } else {
                dummyInventories.put(category.getKey(), dummy = new BaseItemHandler(category.getValue()) {
                    @Override
                    public int getSlotLimit(int slot) {
                        return 1;
                    }

                    @Nonnull
                    @Override
                    public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
                        if (new PatternItemValidator(getPlayer().getEntityWorld()).test(stack)) {
                            return super.insertItem(slot, stack, simulate);
                        }

                        return stack;
                    }
                });
            }

            boolean foundItemsInCategory = false;

            int yHeading = y - 19;

            int slotFound = 0;
            for (int slot = 0; slot < category.getValue(); ++slot) {
                boolean visible = true;

                if (!screenInfoProvider.getSearchFieldText().trim().isEmpty()) {
                    ItemStack stack = dummy.getStackInSlot(slot);

                    if (stack.isEmpty()) {
                        visible = false;
                    } else {
                        CraftingPattern pattern = PatternItem.fromCache(crafterManager.getWorld(), stack);

                        visible = false;

                        for (ItemStack output : pattern.getOutputs()) {
                            ItemGridStack outputConverted = new ItemGridStack(output);

                            for (Predicate<IGridStack> filter : filters) {
                                if (filter.test(outputConverted)) {
                                    visible = true;
                                    break;
                                }
                            }
                        }
                    }
                }

                addSlot(new CrafterManagerSlot(dummy, slot, x, y, visible, screenInfoProvider, crafterManager));

                if (visible) {
                    foundItemsInCategory = true;

                    x += 18;

                    // Don't increase y level if we are on our last slot row (otherwise we do y += 18 * 3)
                    if ((slotFound + 1) % 9 == 0 && slot + 1 < category.getValue()) {
                        x = 8;
                        y += 18;
                        rows++;
                    }

                    slotFound++;
                }
            }

            if (foundItemsInCategory) {
                headings.put(category.getKey(), yHeading);

                x = 8;
                y += 18 * 2;
                rows += 2; // Heading and first row
            }
        }
    }

    public Map<String, Integer> getHeadings() {
        return headings;
    }

    public int getRows() {
        return rows;
    }

    @Override
    public ItemStack transferStackInSlot(PlayerEntity player, int index) {
        ItemStack stack = ItemStack.EMPTY;

        Slot slot = getSlot(index);

        if (slot.getHasStack()) {
            stack = slot.getStack();

            if (index < 9 * 4) {
                if (!mergeItemStack(stack, 9 * 4, inventorySlots.size(), false)) {
                    return ItemStack.EMPTY;
                }
            } else if (!mergeItemStack(stack, 0, 9 * 4, false)) {
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
}

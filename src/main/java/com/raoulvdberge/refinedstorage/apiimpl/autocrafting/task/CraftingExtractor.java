package com.raoulvdberge.refinedstorage.apiimpl.autocrafting.task;

import com.raoulvdberge.refinedstorage.api.network.INetwork;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class CraftingExtractor {
    private INetwork network;
    private List<ItemStack> items;
    private List<CraftingExtractorItemStatus> status = new ArrayList<>();
    private boolean processing;

    public CraftingExtractor(INetwork network, List<ItemStack> items, boolean processing) {
        this.network = network;
        this.items = items;
        this.processing = processing;

        for (int i = 0; i < items.size(); ++i) {
            status.add(CraftingExtractorItemStatus.MISSING);
        }
    }

    public List<ItemStack> getItems() {
        return items;
    }

    public List<CraftingExtractorItemStatus> getStatus() {
        return status;
    }

    public void updateStatus(@Nullable IItemHandler processingInventory) {
        boolean updated = false;

        for (int i = 0; i < items.size(); ++i) {
            if (status.get(i) != CraftingExtractorItemStatus.EXTRACTED) {
                ItemStack stack = items.get(i);

                ItemStack inNetwork = network.extractItem(stack, stack.getCount(), true);

                CraftingExtractorItemStatus previousStatus = status.get(i);

                if (inNetwork == null || inNetwork.getCount() < stack.getCount()) {
                    status.set(i, CraftingExtractorItemStatus.MISSING);
                } else {
                    status.set(i, CraftingExtractorItemStatus.AVAILABLE);

                    if (processing) {
                        if (processingInventory == null) {
                            status.set(i, CraftingExtractorItemStatus.MACHINE_NONE);
                        } else if (!ItemHandlerHelper.insertItem(processingInventory, stack, true).isEmpty()) {
                            status.set(i, CraftingExtractorItemStatus.MACHINE_DOES_NOT_ACCEPT);
                        }
                    }
                }

                if (previousStatus != status.get(i)) {
                    updated = true;
                }
            }
        }

        if (updated) {
            network.getCraftingManager().onTaskChanged();
        }
    }

    public boolean isAllAvailable() {
        return !items.isEmpty() && status.stream().allMatch(s -> s == CraftingExtractorItemStatus.AVAILABLE || s == CraftingExtractorItemStatus.EXTRACTED);
    }

    public boolean isAllExtracted() {
        return !items.isEmpty() && status.stream().allMatch(s -> s == CraftingExtractorItemStatus.EXTRACTED);
    }

    public void extractOne(@Nullable IItemHandler processingInventory) {
        for (int i = 0; i < items.size(); ++i) {
            if (status.get(i) == CraftingExtractorItemStatus.AVAILABLE) {
                ItemStack extracted = network.extractItem(items.get(i), items.get(i).getCount(), false);
                if (extracted == null) {
                    throw new IllegalStateException("Did not extract anything while available");
                }

                if (processing) {
                    if (processingInventory == null) {
                        throw new IllegalStateException("Processing inventory is suddenly null");
                    }

                    ItemStack remainder = ItemHandlerHelper.insertItem(processingInventory, extracted, false);
                    if (!remainder.isEmpty()) {
                        throw new IllegalStateException("The processing inventory gave back a remainder while it previously stated it could handle all");
                    }
                }

                status.set(i, CraftingExtractorItemStatus.EXTRACTED);

                network.getCraftingManager().onTaskChanged();

                return;
            }
        }
    }
}

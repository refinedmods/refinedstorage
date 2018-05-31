package com.raoulvdberge.refinedstorage.apiimpl.autocrafting.task;

import com.raoulvdberge.refinedstorage.api.network.INetwork;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class CraftingExtractor {
    private INetwork network;
    private List<ItemStack> items;
    private List<CraftingExtractorItemStatus> status = new ArrayList<>();

    public CraftingExtractor(INetwork network, List<ItemStack> items) {
        this.network = network;
        this.items = items;

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

    public void updateStatus() {
        for (int i = 0; i < items.size(); ++i) {
            if (status.get(i) != CraftingExtractorItemStatus.EXTRACTED) {
                ItemStack stack = items.get(i);

                ItemStack inNetwork = network.extractItem(stack, stack.getCount(), true);

                if (inNetwork == null || inNetwork.getCount() < stack.getCount()) {
                    status.set(i, CraftingExtractorItemStatus.MISSING);
                } else {
                    status.set(i, CraftingExtractorItemStatus.AVAILABLE);
                }
            }
        }
    }

    public boolean isAllAvailable() {
        return !items.isEmpty() && status.stream().allMatch(s -> s == CraftingExtractorItemStatus.AVAILABLE || s == CraftingExtractorItemStatus.EXTRACTED);
    }

    public boolean isAllExtracted() {
        return !items.isEmpty() && status.stream().allMatch(s -> s == CraftingExtractorItemStatus.EXTRACTED);
    }

    public void extractOne() {
        for (int i = 0; i < items.size(); ++i) {
            if (status.get(i) == CraftingExtractorItemStatus.AVAILABLE) {
                ItemStack extracted = network.extractItem(items.get(i), items.get(i).getCount(), false);
                if (extracted == null) {
                    throw new IllegalStateException("Did not extract anything");
                }

                status.set(i, CraftingExtractorItemStatus.EXTRACTED);

                return;
            }
        }
    }
}

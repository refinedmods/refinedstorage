package com.raoulvdberge.refinedstorage.apiimpl.autocrafting.task.step;

import com.raoulvdberge.refinedstorage.api.autocrafting.ICraftingPattern;
import com.raoulvdberge.refinedstorage.api.network.INetwork;
import com.raoulvdberge.refinedstorage.api.util.IStackList;
import com.raoulvdberge.refinedstorage.apiimpl.API;
import com.raoulvdberge.refinedstorage.apiimpl.autocrafting.task.CraftingExtractor;
import net.minecraft.item.ItemStack;

import java.util.List;

public class CraftingStepProcess extends CraftingStep {
    private CraftingExtractor extractor;
    private IStackList<ItemStack> itemsToReceive = API.instance().createItemStackList();

    public CraftingStepProcess(ICraftingPattern pattern, INetwork network, List<ItemStack> toExtract) {
        super(pattern);
        this.extractor = new CraftingExtractor(network, toExtract);

        for (ItemStack output : pattern.getOutputs()) {
            this.itemsToReceive.add(output);
        }
    }

    @Override
    public boolean canExecute() {
        extractor.updateStatus();

        return extractor.isAllAvailable() && extractor.isAllInsertable(pattern.getContainer().getConnectedInventory());
    }

    public int onTrackedItemInserted(ItemStack stack, int size) {
        if (!extractor.isAllExtracted()) {
            return size;
        }

        ItemStack inList = itemsToReceive.get(stack);
        if (inList == null) {
            return size;
        }

        int toExtract = Math.min(size, inList.getCount());

        itemsToReceive.remove(stack, toExtract);

        return size - toExtract;
    }

    @Override
    public boolean execute() {
        if (!extractor.isAllExtracted()) {
            extractor.extractOneAndInsert(pattern.getContainer().getConnectedInventory());
        }

        return itemsToReceive.isEmpty();
    }

    public CraftingExtractor getExtractor() {
        return extractor;
    }
}

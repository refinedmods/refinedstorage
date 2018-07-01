package com.raoulvdberge.refinedstorage.apiimpl.autocrafting.task.step;

import com.raoulvdberge.refinedstorage.api.autocrafting.ICraftingPattern;
import com.raoulvdberge.refinedstorage.api.autocrafting.task.CraftingTaskReadException;
import com.raoulvdberge.refinedstorage.api.network.INetwork;
import com.raoulvdberge.refinedstorage.api.util.IStackList;
import com.raoulvdberge.refinedstorage.apiimpl.API;
import com.raoulvdberge.refinedstorage.apiimpl.autocrafting.task.extractor.CraftingExtractor;
import com.raoulvdberge.refinedstorage.util.StackUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;

import java.util.List;

public class CraftingStepProcess extends CraftingStep {
    public static final String TYPE = "process";

    private static final String NBT_EXTRACTOR = "Extractor";
    private static final String NBT_TO_RECEIVE = "ToReceive";

    private CraftingExtractor extractor;
    private IStackList<ItemStack> itemsToReceive = API.instance().createItemStackList();

    public CraftingStepProcess(ICraftingPattern pattern, INetwork network, List<ItemStack> toExtract) {
        super(pattern);

        if (!pattern.isProcessing()) {
            throw new IllegalArgumentException("Cannot pass non-processing pattern to processing handler");
        }

        this.extractor = new CraftingExtractor(network, toExtract, true);

        for (ItemStack output : pattern.getOutputs()) {
            this.itemsToReceive.add(output);
        }
    }

    public CraftingStepProcess(ICraftingPattern pattern, INetwork network, NBTTagCompound tag) throws CraftingTaskReadException {
        super(pattern);

        if (!pattern.isProcessing()) {
            throw new IllegalArgumentException("Cannot pass non-processing pattern to processing handler");
        }

        this.extractor = new CraftingExtractor(network, tag.getTagList(NBT_EXTRACTOR, Constants.NBT.TAG_COMPOUND), true);

        NBTTagList toReceiveList = tag.getTagList(NBT_TO_RECEIVE, Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < toReceiveList.tagCount(); ++i) {
            ItemStack toReceive = StackUtils.deserializeStackFromNbt(toReceiveList.getCompoundTagAt(i));

            if (toReceive.isEmpty()) {
                throw new CraftingTaskReadException("Item to receive is empty");
            }

            this.itemsToReceive.add(toReceive);
        }
    }

    @Override
    public boolean canExecute() {
        extractor.updateStatus(pattern.getContainer().getConnectedInventory());

        return extractor.isAllAvailable();
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
            extractor.extractOne(pattern.getContainer().getConnectedInventory());
        }

        return extractor.isAllExtracted() && itemsToReceive.isEmpty();
    }

    @Override
    public String getType() {
        return TYPE;
    }

    @Override
    public NBTTagCompound writeToNbt() {
        NBTTagCompound tag = super.writeToNbt();

        tag.setTag(NBT_EXTRACTOR, extractor.writeToNbt());

        NBTTagList toReceive = new NBTTagList();

        for (ItemStack toReceiveStack : itemsToReceive.getStacks()) {
            toReceive.appendTag(StackUtils.serializeStackToNbt(toReceiveStack));
        }

        tag.setTag(NBT_TO_RECEIVE, toReceive);

        return tag;
    }

    public CraftingExtractor getExtractor() {
        return extractor;
    }
}

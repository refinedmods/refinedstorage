package com.refinedmods.refinedstorage.apiimpl.autocrafting.task.v6.preview;

import com.google.common.collect.ImmutableList;
import com.refinedmods.refinedstorage.api.autocrafting.preview.ICraftingPreviewElement;
import com.refinedmods.refinedstorage.api.autocrafting.task.ICraftingTask;
import com.refinedmods.refinedstorage.api.util.IStackList;
import com.refinedmods.refinedstorage.api.util.StackListEntry;
import com.refinedmods.refinedstorage.apiimpl.API;
import com.refinedmods.refinedstorage.apiimpl.autocrafting.preview.FluidCraftingPreviewElement;
import com.refinedmods.refinedstorage.apiimpl.autocrafting.preview.ItemCraftingPreviewElement;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CraftingPreviewElementFactory {
    private final ICraftingTask craftingTask;

    public CraftingPreviewElementFactory(ICraftingTask craftingTask) {
        this.craftingTask = craftingTask;
    }

    public List<ICraftingPreviewElement<?>> getElements(List<ItemStack> toCraft, List<FluidStack> toCraftFluids, IStackList<ItemStack> toTake, IStackList<FluidStack> toTakeFluids) {
        Map<Integer, ItemCraftingPreviewElement> map = new LinkedHashMap<>();
        Map<Integer, FluidCraftingPreviewElement> mapFluids = new LinkedHashMap<>();

        for (StackListEntry<ItemStack> stack : craftingTask.getMissing().getStacks()) {
            int hash = API.instance().getItemStackHashCode(stack.getStack());

            ItemCraftingPreviewElement previewStack = map.get(hash);

            if (previewStack == null) {
                previewStack = new ItemCraftingPreviewElement(stack.getStack());
            }

            previewStack.setMissing(true);
            previewStack.addToCraft(stack.getStack().getCount());

            map.put(hash, previewStack);
        }

        for (StackListEntry<FluidStack> stack : craftingTask.getMissingFluids().getStacks()) {
            int hash = API.instance().getFluidStackHashCode(stack.getStack());

            FluidCraftingPreviewElement previewStack = mapFluids.get(hash);

            if (previewStack == null) {
                previewStack = new FluidCraftingPreviewElement(stack.getStack());
            }

            previewStack.setMissing(true);
            previewStack.addToCraft(stack.getStack().getAmount());

            mapFluids.put(hash, previewStack);
        }

        for (ItemStack stack : ImmutableList.copyOf(toCraft).reverse()) {
            int hash = API.instance().getItemStackHashCode(stack);

            ItemCraftingPreviewElement previewStack = map.get(hash);

            if (previewStack == null) {
                previewStack = new ItemCraftingPreviewElement(stack.getStack());
            }

            previewStack.addToCraft(stack.getCount());

            map.put(hash, previewStack);
        }

        for (FluidStack stack : ImmutableList.copyOf(toCraftFluids).reverse()) {
            int hash = API.instance().getFluidStackHashCode(stack);

            FluidCraftingPreviewElement previewStack = mapFluids.get(hash);

            if (previewStack == null) {
                previewStack = new FluidCraftingPreviewElement(stack);
            }

            previewStack.addToCraft(stack.getAmount());

            mapFluids.put(hash, previewStack);
        }

        for (StackListEntry<ItemStack> stack : toTake.getStacks()) {
            int hash = API.instance().getItemStackHashCode(stack.getStack());

            ItemCraftingPreviewElement previewStack = map.get(hash);

            if (previewStack == null) {
                previewStack = new ItemCraftingPreviewElement(stack.getStack());
            }

            previewStack.addAvailable(stack.getStack().getCount());

            map.put(hash, previewStack);
        }

        for (StackListEntry<FluidStack> stack : toTakeFluids.getStacks()) {
            int hash = API.instance().getFluidStackHashCode(stack.getStack());

            FluidCraftingPreviewElement previewStack = mapFluids.get(hash);

            if (previewStack == null) {
                previewStack = new FluidCraftingPreviewElement(stack.getStack());
            }

            previewStack.addAvailable(stack.getStack().getAmount());

            mapFluids.put(hash, previewStack);
        }

        List<ICraftingPreviewElement<?>> elements = new ArrayList<>();

        elements.addAll(map.values());
        elements.addAll(mapFluids.values());

        return elements;
    }
}

package com.refinedmods.refinedstorage.apiimpl.autocrafting.task.v6.preview;

import com.google.common.collect.ImmutableList;
import com.refinedmods.refinedstorage.api.autocrafting.preview.ICraftingPreviewElement;
import com.refinedmods.refinedstorage.api.util.StackListEntry;
import com.refinedmods.refinedstorage.apiimpl.API;
import com.refinedmods.refinedstorage.apiimpl.autocrafting.preview.FluidCraftingPreviewElement;
import com.refinedmods.refinedstorage.apiimpl.autocrafting.preview.ItemCraftingPreviewElement;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CraftingPreviewElementFactory {
    public List<ICraftingPreviewElement> getElements(CraftingPreviewInfo info) {
        Map<Integer, ItemCraftingPreviewElement> map = new LinkedHashMap<>();
        Map<Integer, FluidCraftingPreviewElement> mapFluids = new LinkedHashMap<>();

        for (StackListEntry<ItemStack> stack : info.getMissing().getStacks()) {
            int hash = API.instance().getItemStackHashCode(stack.getStack());

            ItemCraftingPreviewElement previewStack = map.computeIfAbsent(hash, key -> new ItemCraftingPreviewElement(stack.getStack()));
            previewStack.setMissing(true);
            previewStack.addToCraft(stack.getStack().getCount());
        }

        for (StackListEntry<FluidStack> stack : info.getMissingFluids().getStacks()) {
            int hash = API.instance().getFluidStackHashCode(stack.getStack());

            FluidCraftingPreviewElement previewStack = mapFluids.computeIfAbsent(hash, key -> new FluidCraftingPreviewElement(stack.getStack()));
            previewStack.setMissing(true);
            previewStack.addToCraft(stack.getStack().getAmount());
        }

        for (ItemStack stack : ImmutableList.copyOf(info.getToCraft()).reverse()) {
            int hash = API.instance().getItemStackHashCode(stack);

            ItemCraftingPreviewElement previewStack = map.computeIfAbsent(hash, key -> new ItemCraftingPreviewElement(stack));
            previewStack.addToCraft(stack.getCount());
        }

        for (FluidStack stack : ImmutableList.copyOf(info.getToCraftFluids()).reverse()) {
            int hash = API.instance().getFluidStackHashCode(stack);

            FluidCraftingPreviewElement previewStack = mapFluids.computeIfAbsent(hash, key -> new FluidCraftingPreviewElement(stack));
            previewStack.addToCraft(stack.getAmount());
        }

        for (StackListEntry<ItemStack> stack : info.getToTake().getStacks()) {
            int hash = API.instance().getItemStackHashCode(stack.getStack());

            ItemCraftingPreviewElement previewStack = map.computeIfAbsent(hash, key -> new ItemCraftingPreviewElement(stack.getStack()));
            previewStack.addAvailable(stack.getStack().getCount());
        }

        for (StackListEntry<FluidStack> stack : info.getToTakeFluids().getStacks()) {
            int hash = API.instance().getFluidStackHashCode(stack.getStack());

            FluidCraftingPreviewElement previewStack = mapFluids.computeIfAbsent(hash, key -> new FluidCraftingPreviewElement(stack.getStack()));
            previewStack.addAvailable(stack.getStack().getAmount());
        }

        List<ICraftingPreviewElement> elements = new ArrayList<>();

        elements.addAll(map.values());
        elements.addAll(mapFluids.values());

        return elements;
    }
}

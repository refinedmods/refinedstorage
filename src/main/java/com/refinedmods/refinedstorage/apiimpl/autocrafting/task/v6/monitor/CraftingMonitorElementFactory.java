package com.refinedmods.refinedstorage.apiimpl.autocrafting.task.v6.monitor;

import com.refinedmods.refinedstorage.api.autocrafting.craftingmonitor.ICraftingMonitorElement;
import com.refinedmods.refinedstorage.api.autocrafting.craftingmonitor.ICraftingMonitorElementList;
import com.refinedmods.refinedstorage.api.storage.disk.IStorageDisk;
import com.refinedmods.refinedstorage.api.util.StackListEntry;
import com.refinedmods.refinedstorage.apiimpl.API;
import com.refinedmods.refinedstorage.apiimpl.autocrafting.craftingmonitor.ErrorCraftingMonitorElement;
import com.refinedmods.refinedstorage.apiimpl.autocrafting.craftingmonitor.FluidCraftingMonitorElement;
import com.refinedmods.refinedstorage.apiimpl.autocrafting.craftingmonitor.ItemCraftingMonitorElement;
import com.refinedmods.refinedstorage.apiimpl.autocrafting.task.v6.ProcessingState;
import com.refinedmods.refinedstorage.apiimpl.autocrafting.task.v6.node.CraftingNode;
import com.refinedmods.refinedstorage.apiimpl.autocrafting.task.v6.node.Node;
import com.refinedmods.refinedstorage.apiimpl.autocrafting.task.v6.node.ProcessingNode;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import java.util.Collection;
import java.util.List;

public class CraftingMonitorElementFactory {
    public List<ICraftingMonitorElement> getElements(Collection<Node> nodes, IStorageDisk<ItemStack> internalStorage, IStorageDisk<FluidStack> internalFluidStorage) {
        ICraftingMonitorElementList list = API.instance().createCraftingMonitorElementList();

        for (Node node : nodes) {
            if (node instanceof CraftingNode) {
                addForRecipe(list, (CraftingNode) node);
            } else {
                addForProcessing(list, (ProcessingNode) node);
            }
        }

        for (ItemStack stack : internalStorage.getStacks()) {
            list.addStorage(new ItemCraftingMonitorElement(stack, stack.getCount(), 0, 0, 0, 0));
        }

        for (FluidStack stack : internalFluidStorage.getStacks()) {
            list.addStorage(new FluidCraftingMonitorElement(stack, stack.getAmount(), 0, 0, 0, 0));
        }

        list.commit();

        return list.getElements();
    }

    private void addForProcessing(ICraftingMonitorElementList list, ProcessingNode node) {
        if (node.getState() == ProcessingState.PROCESSED) {
            return;
        }

        for (StackListEntry<ItemStack> use : node.getItemsToDisplay().getStacks()) {
            if (node.getProcessing() > 0 || node.getState() != ProcessingState.READY) {
                ICraftingMonitorElement element = new ItemCraftingMonitorElement(use.getStack(), 0, 0, use.getStack().getCount() * node.getProcessing(), 0, 0);

                if (node.getState() == ProcessingState.MACHINE_DOES_NOT_ACCEPT) {
                    element = new ErrorCraftingMonitorElement(element, "gui.refinedstorage.crafting_monitor.machine_does_not_accept_item");
                } else if (node.getState() == ProcessingState.MACHINE_NONE) {
                    element = new ErrorCraftingMonitorElement(element, "gui.refinedstorage.crafting_monitor.machine_none");
                } else if (node.getState() == ProcessingState.LOCKED) {
                    element = new ErrorCraftingMonitorElement(element, "gui.refinedstorage.crafting_monitor.crafter_is_locked");
                }

                list.add(element, true);
            }
        }

        for (StackListEntry<ItemStack> receive : node.getItemsToReceive().getStacks()) {
            int count = node.getNeeded(receive.getStack());
            if (count > 0) {
                list.add(new ItemCraftingMonitorElement(receive.getStack(), 0, 0, 0, count, 0), true);
            }
        }

        for (StackListEntry<FluidStack> use : node.getFluidsToUse().getStacks()) {
            if (node.getProcessing() > 0 || node.getState() != ProcessingState.READY) {
                ICraftingMonitorElement element = new FluidCraftingMonitorElement(use.getStack(), 0, 0, use.getStack().getAmount() * node.getProcessing(), 0, 0);

                if (node.getState() == ProcessingState.MACHINE_DOES_NOT_ACCEPT) {
                    element = new ErrorCraftingMonitorElement(element, "gui.refinedstorage.crafting_monitor.machine_does_not_accept_fluid");
                } else if (node.getState() == ProcessingState.MACHINE_NONE) {
                    element = new ErrorCraftingMonitorElement(element, "gui.refinedstorage.crafting_monitor.machine_none");
                } else if (node.getState() == ProcessingState.LOCKED) {
                    element = new ErrorCraftingMonitorElement(element, "gui.refinedstorage.crafting_monitor.crafter_is_locked");
                }

                list.add(element, true);
            }
        }

        for (StackListEntry<FluidStack> receive : node.getFluidsToReceive().getStacks()) {
            int count = node.getNeeded(receive.getStack());
            if (count > 0) {
                list.add(new FluidCraftingMonitorElement(receive.getStack(), 0, 0, 0, count, 0), true);
            }
        }
    }

    private void addForRecipe(ICraftingMonitorElementList list, CraftingNode node) {
        if (node.getQuantity() > 0) {
            for (ItemStack receive : node.getPattern().getOutputs()) {
                list.add(new ItemCraftingMonitorElement(receive, 0, 0, 0, 0, receive.getCount() * node.getQuantity()), false);
            }
        }
    }
}

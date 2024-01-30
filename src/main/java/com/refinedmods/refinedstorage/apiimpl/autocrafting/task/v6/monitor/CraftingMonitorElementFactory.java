package com.refinedmods.refinedstorage.apiimpl.autocrafting.task.v6.monitor;

import com.refinedmods.refinedstorage.api.autocrafting.craftingmonitor.ICraftingMonitorElement;
import com.refinedmods.refinedstorage.api.autocrafting.craftingmonitor.ICraftingMonitorElementList;
import com.refinedmods.refinedstorage.api.storage.disk.IStorageDisk;
import com.refinedmods.refinedstorage.api.util.StackListEntry;
import com.refinedmods.refinedstorage.apiimpl.API;
import com.refinedmods.refinedstorage.apiimpl.autocrafting.craftingmonitor.ErrorCraftingMonitorElement;
import com.refinedmods.refinedstorage.apiimpl.autocrafting.craftingmonitor.FluidCraftingMonitorElement;
import com.refinedmods.refinedstorage.apiimpl.autocrafting.craftingmonitor.ItemCraftingMonitorElement;
import com.refinedmods.refinedstorage.apiimpl.autocrafting.task.v6.node.CraftingNode;
import com.refinedmods.refinedstorage.apiimpl.autocrafting.task.v6.node.Node;
import com.refinedmods.refinedstorage.apiimpl.autocrafting.task.v6.node.ProcessingNode;
import com.refinedmods.refinedstorage.apiimpl.autocrafting.task.v6.node.ProcessingState;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;
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

        for (StackListEntry<ItemStack> requirement : node.getSingleItemSetToRequire().getStacks()) {
            if (node.getCurrentlyProcessing() > 0) {
                ICraftingMonitorElement element = ItemCraftingMonitorElement.Builder
                    .forStack(requirement.getStack())
                    .processing(requirement.getStack().getCount() * node.getCurrentlyProcessing())
                    .build();

                list.add(element, true);
            }
        }

        for (StackListEntry<ItemStack> toReceive : node.getSingleItemSetToReceive().getStacks()) {
            int needed = node.getNeeded(toReceive.getStack());

            if (needed > 0) {
                ICraftingMonitorElement element = ItemCraftingMonitorElement.Builder
                    .forStack(toReceive.getStack())
                    .scheduled(needed)
                    .build();

                element = wrapWithProcessingState(element, node.getState(), "item");

                list.add(element, true);
            }
        }

        for (StackListEntry<FluidStack> requirement : node.getSingleFluidSetToRequire().getStacks()) {
            if (node.getCurrentlyProcessing() > 0) {
                ICraftingMonitorElement element = FluidCraftingMonitorElement.Builder
                    .forStack(requirement.getStack())
                    .processing(requirement.getStack().getAmount() * node.getCurrentlyProcessing())
                    .build();

                list.add(element, true);
            }
        }

        for (StackListEntry<FluidStack> toReceive : node.getSingleFluidSetToReceive().getStacks()) {
            int needed = node.getNeeded(toReceive.getStack());

            if (needed > 0) {
                ICraftingMonitorElement element = FluidCraftingMonitorElement.Builder
                    .forStack(toReceive.getStack())
                    .scheduled(needed)
                    .build();

                element = wrapWithProcessingState(element, node.getState(), "fluid");

                list.add(element, true);
            }
        }
    }

    private ICraftingMonitorElement wrapWithProcessingState(ICraftingMonitorElement element, ProcessingState state, String type) {
        if (state == ProcessingState.MACHINE_DOES_NOT_ACCEPT) {
            return new ErrorCraftingMonitorElement(element, "gui.refinedstorage.crafting_monitor.machine_does_not_accept_" + type);
        } else if (state == ProcessingState.MACHINE_NONE) {
            return new ErrorCraftingMonitorElement(element, "gui.refinedstorage.crafting_monitor.machine_none");
        } else if (state == ProcessingState.LOCKED) {
            return new ErrorCraftingMonitorElement(element, "gui.refinedstorage.crafting_monitor.crafter_is_locked");
        }

        return element;
    }

    private void addForRecipe(ICraftingMonitorElementList list, CraftingNode node) {
        if (node.getQuantity() > 0) {
            for (ItemStack receive : node.getPattern().getOutputs()) {
                list.add(new ItemCraftingMonitorElement(receive, 0, 0, 0, 0, receive.getCount() * node.getQuantity()), false);
            }
        }
    }
}

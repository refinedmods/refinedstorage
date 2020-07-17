package com.refinedmods.refinedstorage.apiimpl.autocrafting.task.v6.node;

import com.refinedmods.refinedstorage.api.autocrafting.ICraftingPattern;
import com.refinedmods.refinedstorage.api.autocrafting.ICraftingPatternContainer;
import com.refinedmods.refinedstorage.api.network.INetwork;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

import java.util.*;

public class CraftingTaskNodeList {
    private final Map<ICraftingPattern, CraftingTaskNode> nodes = new LinkedHashMap<>();
    private final List<CraftingTaskNode> nodesToRemove = new ArrayList<>();

    public void removeMarkedForRemoval() {
        for (CraftingTaskNode node : nodesToRemove) {
            nodes.remove(node.getPattern());
        }
        nodesToRemove.clear();
    }

    public Collection<CraftingTaskNode> all() {
        return nodes.values();
    }

    public void unlockAll(INetwork network) {
        for (CraftingTaskNode node : nodes.values()) {
            if (node instanceof ProcessingCraftingTaskNode) {
                network.getCraftingManager().getAllContainer(node.getPattern()).forEach(ICraftingPatternContainer::unlock);
            }
        }
    }

    public boolean isEmpty() {
        return nodes.isEmpty();
    }

    public void remove(CraftingTaskNode node) {
        nodesToRemove.add(node);
    }

    public CraftingTaskNode createOrAddToExistingNode(ICraftingPattern pattern, boolean root, NonNullList<ItemStack> recipe, int qty) {
        CraftingTaskNode node = nodes.get(pattern);
        if (node == null) {
            nodes.put(pattern, node = createNode(pattern, root, recipe));
        }

        node.addQuantity(qty);

        return node;
    }

    private CraftingTaskNode createNode(ICraftingPattern pattern, boolean root, NonNullList<ItemStack> recipe) {
        return pattern.isProcessing() ? new ProcessingCraftingTaskNode(pattern, root) : new RecipeCraftingTaskNode(pattern, root, recipe);
    }

    public void put(ICraftingPattern pattern, CraftingTaskNode node) {
        nodes.put(pattern, node);
    }
}

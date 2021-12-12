package com.refinedmods.refinedstorage.apiimpl.autocrafting.task.v6.node;

import com.refinedmods.refinedstorage.api.autocrafting.ICraftingPattern;
import com.refinedmods.refinedstorage.api.autocrafting.ICraftingPatternContainer;
import com.refinedmods.refinedstorage.api.network.INetwork;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;

import java.util.*;

public class NodeList {
    private final Map<ICraftingPattern, Node> nodes = new LinkedHashMap<>();
    private final List<Node> nodesToRemove = new ArrayList<>();

    public void removeMarkedForRemoval() {
        for (Node node : nodesToRemove) {
            nodes.remove(node.getPattern());
        }
        nodesToRemove.clear();
    }

    public Collection<Node> all() {
        return nodes.values();
    }

    public void unlockAll(INetwork network) {
        for (Node node : nodes.values()) {
            if (node instanceof ProcessingNode) {
                network.getCraftingManager().getAllContainers(node.getPattern()).forEach(ICraftingPatternContainer::unlock);
            }
        }
    }

    public boolean isEmpty() {
        return nodes.isEmpty();
    }

    public void remove(Node node) {
        nodesToRemove.add(node);
    }

    public Node createOrAddToExistingNode(ICraftingPattern pattern, boolean root, NonNullList<ItemStack> recipe, int qty) {
        Node node = nodes.computeIfAbsent(pattern, key -> createNode(key, root, recipe));
        node.addQuantity(qty);

        return node;
    }

    private Node createNode(ICraftingPattern pattern, boolean root, NonNullList<ItemStack> recipe) {
        return pattern.isProcessing() ? new ProcessingNode(pattern, root) : new CraftingNode(pattern, root, recipe);
    }

    public void put(ICraftingPattern pattern, Node node) {
        nodes.put(pattern, node);
    }
}

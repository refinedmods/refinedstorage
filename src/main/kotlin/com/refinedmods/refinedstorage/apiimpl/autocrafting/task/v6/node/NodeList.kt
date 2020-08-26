package com.refinedmods.refinedstorage.apiimpl.autocrafting.task.v6.node

import com.refinedmods.refinedstorage.api.autocrafting.ICraftingPattern
import com.refinedmods.refinedstorage.api.autocrafting.ICraftingPatternContainer
import com.refinedmods.refinedstorage.api.network.INetwork
import net.minecraft.item.ItemStack
import java.util.*
import java.util.function.Consumer
import kotlin.collections.Collection
import kotlin.collections.LinkedHashMap
import kotlin.collections.MutableList
import kotlin.collections.MutableMap
import kotlin.collections.set

class NodeList {
    private val nodes: MutableMap<ICraftingPattern, Node> = LinkedHashMap()
    private val nodesToRemove: MutableList<Node> = ArrayList()
    fun removeMarkedForRemoval() {
        for (node in nodesToRemove) {
            nodes.remove(node.pattern)
        }
        nodesToRemove.clear()
    }

    fun all(): Collection<Node> {
        return nodes.values
    }

    fun unlockAll(network: INetwork?) {
        for (node in nodes.values) {
            if (node is ProcessingNode) {
                network.craftingManager.getAllContainers(node.pattern).forEach(Consumer<ICraftingPatternContainer> { obj: ICraftingPatternContainer -> obj.unlock() })
            }
        }
    }

    val isEmpty: Boolean
        get() = nodes.isEmpty()

    fun remove(node: Node) {
        nodesToRemove.add(node)
    }

    fun createOrAddToExistingNode(pattern: ICraftingPattern, root: Boolean, recipe: List<ItemStack>, qty: Int): Node? {
        var node = nodes[pattern]
        if (node == null) {
            nodes[pattern] = createNode(pattern, root, recipe).also { node = it }
        }
        node!!.addQuantity(qty)
        return node
    }

    private fun createNode(pattern: ICraftingPattern, root: Boolean, recipe: List<ItemStack>): Node {
        return if (pattern.isProcessing()) ProcessingNode(pattern, root) else CraftingNode(pattern, root, recipe)
    }

    fun put(pattern: ICraftingPattern, node: Node) {
        nodes[pattern] = node
    }
}
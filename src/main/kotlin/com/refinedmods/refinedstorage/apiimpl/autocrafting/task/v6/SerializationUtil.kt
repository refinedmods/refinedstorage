package com.refinedmods.refinedstorage.apiimpl.autocrafting.task.v6

import com.refinedmods.refinedstorage.api.util.IStackList
import net.minecraft.world.server.ServerWorld
import net.minecraftforge.fluids.FluidInstanceimport

object SerializationUtil {
    private const val NBT_PATTERN_STACK = "Stack"
    private const val NBT_PATTERN_CONTAINER_POS = "ContainerPos"
    fun writeItemStackList(stacks: IStackList<ItemStack>?): ListTag {
        val list = ListTag()
        for (entry in stacks.getStacks()) {
            list.add(StackUtils.serializeStackToNbt(entry.stack))
        }
        return list
    }

    @Throws(CraftingTaskReadException::class)
    fun readItemStackList(list: ListTag): IStackList<ItemStack> {
        val stacks: IStackList<ItemStack> = API.instance().createItemStackList()
        for (i in list.indices) {
            val stack: ItemStack = StackUtils.deserializeStackFromNbt(list.getCompound(i))
            if (stack.isEmpty()) {
                throw CraftingTaskReadException("Empty stack!")
            }
            stacks.add(stack)
        }
        return stacks
    }

    fun writeFluidInstanceList(stacks: IStackList<FluidInstance>?): ListTag {
        val list = ListTag()
        for (entry in stacks.getStacks()) {
            list.add(entry.stack.writeToNBT(CompoundTag()))
        }
        return list
    }

    @Throws(CraftingTaskReadException::class)
    fun readFluidInstanceList(list: ListTag): IStackList<FluidInstance> {
        val stacks: IStackList<FluidInstance> = API.instance().createFluidInstanceList()
        for (i in list.indices) {
            val stack: FluidInstance = FluidInstance.loadFluidInstanceFromNBT(list.getCompound(i))
            if (stack.isEmpty()) {
                throw CraftingTaskReadException("Empty stack!")
            }
            stacks.add(stack)
        }
        return stacks
    }

    fun writePatternToNbt(pattern: ICraftingPattern?): CompoundTag {
        val tag = CompoundTag()
        tag.put(NBT_PATTERN_STACK, pattern.getStack().serializeNBT())
        tag.putLong(NBT_PATTERN_CONTAINER_POS, pattern.getContainer().getPosition().toLong())
        return tag
    }

    @Throws(CraftingTaskReadException::class)
    fun readPatternFromNbt(tag: CompoundTag, world: World?): ICraftingPattern {
        val containerPos: BlockPos = BlockPos.fromLong(tag.getLong(NBT_PATTERN_CONTAINER_POS))
        val node: INetworkNode = API.instance().getNetworkNodeManager(world as ServerWorld?).getNode(containerPos)
        return if (node is ICraftingPatternContainer) {
            val stack: ItemStack = ItemStack.read(tag.getCompound(NBT_PATTERN_STACK))
            if (stack.getItem() is ICraftingPatternProvider) {
                (stack.getItem() as ICraftingPatternProvider).create(world, stack, node as ICraftingPatternContainer)
            } else {
                throw CraftingTaskReadException("Pattern stack is not a crafting pattern provider")
            }
        } else {
            throw CraftingTaskReadException("Crafting pattern container doesn't exist anymore")
        }
    }
}
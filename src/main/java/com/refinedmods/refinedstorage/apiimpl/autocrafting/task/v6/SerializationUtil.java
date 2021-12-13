package com.refinedmods.refinedstorage.apiimpl.autocrafting.task.v6;

import com.refinedmods.refinedstorage.api.autocrafting.ICraftingPattern;
import com.refinedmods.refinedstorage.api.autocrafting.ICraftingPatternContainer;
import com.refinedmods.refinedstorage.api.autocrafting.ICraftingPatternProvider;
import com.refinedmods.refinedstorage.api.autocrafting.task.CraftingTaskReadException;
import com.refinedmods.refinedstorage.api.network.node.INetworkNode;
import com.refinedmods.refinedstorage.api.util.IStackList;
import com.refinedmods.refinedstorage.api.util.StackListEntry;
import com.refinedmods.refinedstorage.apiimpl.API;
import com.refinedmods.refinedstorage.util.StackUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidStack;

public class SerializationUtil {
    private static final String NBT_PATTERN_STACK = "Stack";
    private static final String NBT_PATTERN_CONTAINER_POS = "ContainerPos";

    private SerializationUtil() {
    }

    public static ListTag writeItemStackList(IStackList<ItemStack> stacks) {
        ListTag list = new ListTag();

        for (StackListEntry<ItemStack> entry : stacks.getStacks()) {
            list.add(StackUtils.serializeStackToNbt(entry.getStack()));
        }

        return list;
    }

    public static IStackList<ItemStack> readItemStackList(ListTag list) throws CraftingTaskReadException {
        IStackList<ItemStack> stacks = API.instance().createItemStackList();

        for (int i = 0; i < list.size(); ++i) {
            ItemStack stack = StackUtils.deserializeStackFromNbt(list.getCompound(i));

            if (stack.isEmpty()) {
                throw new CraftingTaskReadException("Empty stack!");
            }

            stacks.add(stack);
        }

        return stacks;
    }

    public static ListTag writeFluidStackList(IStackList<FluidStack> stacks) {
        ListTag list = new ListTag();

        for (StackListEntry<FluidStack> entry : stacks.getStacks()) {
            list.add(entry.getStack().writeToNBT(new CompoundTag()));
        }

        return list;
    }

    public static IStackList<FluidStack> readFluidStackList(ListTag list) throws CraftingTaskReadException {
        IStackList<FluidStack> stacks = API.instance().createFluidStackList();

        for (int i = 0; i < list.size(); ++i) {
            FluidStack stack = FluidStack.loadFluidStackFromNBT(list.getCompound(i));

            if (stack.isEmpty()) {
                throw new CraftingTaskReadException("Empty stack!");
            }

            stacks.add(stack);
        }

        return stacks;
    }

    public static CompoundTag writePatternToNbt(ICraftingPattern pattern) {
        CompoundTag tag = new CompoundTag();

        tag.put(NBT_PATTERN_STACK, pattern.getStack().serializeNBT());
        tag.putLong(NBT_PATTERN_CONTAINER_POS, pattern.getContainer().getPosition().asLong());

        return tag;
    }

    public static ICraftingPattern readPatternFromNbt(CompoundTag tag, Level level) throws CraftingTaskReadException {
        BlockPos containerPos = BlockPos.of(tag.getLong(NBT_PATTERN_CONTAINER_POS));

        INetworkNode node = API.instance().getNetworkNodeManager((ServerLevel) level).getNode(containerPos);

        if (node instanceof ICraftingPatternContainer) {
            ItemStack stack = ItemStack.of(tag.getCompound(NBT_PATTERN_STACK));

            if (stack.getItem() instanceof ICraftingPatternProvider) {
                return ((ICraftingPatternProvider) stack.getItem()).create(level, stack, (ICraftingPatternContainer) node);
            } else {
                throw new CraftingTaskReadException("Pattern stack is not a crafting pattern provider");
            }
        } else {
            throw new CraftingTaskReadException("Crafting pattern container doesn't exist anymore");
        }
    }
}

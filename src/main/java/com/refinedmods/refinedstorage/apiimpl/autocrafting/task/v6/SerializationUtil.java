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
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fluids.FluidStack;

public class SerializationUtil {
    private static final String NBT_PATTERN_STACK = "Stack";
    private static final String NBT_PATTERN_CONTAINER_POS = "ContainerPos";

    private SerializationUtil() {
    }

    public static ListNBT writeItemStackList(IStackList<ItemStack> stacks) {
        ListNBT list = new ListNBT();

        for (StackListEntry<ItemStack> entry : stacks.getStacks()) {
            list.add(StackUtils.serializeStackToNbt(entry.getStack()));
        }

        return list;
    }

    public static IStackList<ItemStack> readItemStackList(ListNBT list) throws CraftingTaskReadException {
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

    public static ListNBT writeFluidStackList(IStackList<FluidStack> stacks) {
        ListNBT list = new ListNBT();

        for (StackListEntry<FluidStack> entry : stacks.getStacks()) {
            list.add(entry.getStack().writeToNBT(new CompoundNBT()));
        }

        return list;
    }

    public static IStackList<FluidStack> readFluidStackList(ListNBT list) throws CraftingTaskReadException {
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

    public static CompoundNBT writePatternToNbt(ICraftingPattern pattern) {
        CompoundNBT tag = new CompoundNBT();

        tag.put(NBT_PATTERN_STACK, pattern.getStack().serializeNBT());
        tag.putLong(NBT_PATTERN_CONTAINER_POS, pattern.getContainer().getPosition().asLong());

        return tag;
    }

    public static ICraftingPattern readPatternFromNbt(CompoundNBT tag, World world) throws CraftingTaskReadException {
        BlockPos containerPos = BlockPos.of(tag.getLong(NBT_PATTERN_CONTAINER_POS));

        INetworkNode node = API.instance().getNetworkNodeManager((ServerWorld) world).getNode(containerPos);

        if (node instanceof ICraftingPatternContainer) {
            ItemStack stack = ItemStack.of(tag.getCompound(NBT_PATTERN_STACK));

            if (stack.getItem() instanceof ICraftingPatternProvider) {
                return ((ICraftingPatternProvider) stack.getItem()).create(world, stack, (ICraftingPatternContainer) node);
            } else {
                throw new CraftingTaskReadException("Pattern stack is not a crafting pattern provider");
            }
        } else {
            throw new CraftingTaskReadException("Crafting pattern container doesn't exist anymore");
        }
    }
}

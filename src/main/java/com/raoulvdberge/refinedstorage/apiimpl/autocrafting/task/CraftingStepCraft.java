package com.raoulvdberge.refinedstorage.apiimpl.autocrafting.task;

import com.raoulvdberge.refinedstorage.RSUtils;
import com.raoulvdberge.refinedstorage.api.autocrafting.ICraftingPattern;
import com.raoulvdberge.refinedstorage.api.network.INetworkMaster;
import com.raoulvdberge.refinedstorage.api.util.IComparer;
import com.raoulvdberge.refinedstorage.api.util.IFluidStackList;
import com.raoulvdberge.refinedstorage.api.util.IItemStackList;
import com.raoulvdberge.refinedstorage.apiimpl.API;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.items.ItemHandlerHelper;

import java.util.*;
import java.util.stream.Collectors;

public class CraftingStepCraft extends CraftingStep {
    public static final String ID = "craft";
    private static final String NBT_TO_INSERT = "ToInsert";

    private List<ItemStack> toInsert;

    public CraftingStepCraft(INetworkMaster network, ICraftingPattern pattern, List<ItemStack> toInsert) {
        super(network, pattern);
        this.toInsert = new LinkedList<>();
        toInsert.stream().filter(insert -> insert != null).forEach(stack -> this.toInsert.add(stack.copy()));
    }

    public CraftingStepCraft(INetworkMaster network) {
        super(network);
    }

    @Override
    public List<ItemStack> getToInsert() {
        return toInsert == null ? super.getToInsert() : toInsert;
    }

    @Override
    public boolean canStartProcessing(IItemStackList items, IFluidStackList fluids) {
        int compare = CraftingTask.DEFAULT_COMPARE | (pattern.isOredict() ? IComparer.COMPARE_OREDICT : 0);
        for (ItemStack stack : getToInsert()) {
            // This will be a tool, like a hammer
            if (stack.isItemStackDamageable()) {
                compare &= ~IComparer.COMPARE_DAMAGE;
            } else {
                compare |= IComparer.COMPARE_DAMAGE;
            }

            ItemStack actualStack = items.get(stack, compare);

            if (actualStack == null || actualStack.stackSize == 0 || !items.trackedRemove(actualStack, stack.stackSize, true)) {
                FluidStack fluidInItem = RSUtils.getFluidFromStack(stack, true);

                if (fluidInItem != null && RSUtils.hasFluidBucket(fluidInItem)) {
                    FluidStack fluidStack = fluids.get(fluidInItem, compare);
                    ItemStack bucket = items.get(RSUtils.EMPTY_BUCKET, compare);
                    if (bucket != null && fluidStack != null && fluids.trackedRemove(fluidStack, fluidInItem.amount, true) && items.trackedRemove(bucket, 1, true)) {
                        continue;
                    }
                }
                items.undo();
                fluids.undo();
                return false;
            }
        }
        items.undo();
        fluids.undo();
        return true;
    }

    @Override
    public void execute(Deque<ItemStack> toInsertItems, Deque<FluidStack> toInsertFluids) {
        IItemStackList actualInputs = API.instance().createItemStackList();
        int compare = CraftingTask.DEFAULT_COMPARE | (getPattern().isOredict() ? IComparer.COMPARE_OREDICT : 0);
        for (ItemStack insertStack : getToInsert()) {
            FluidStack fluidInItem = RSUtils.getFluidFromStack(insertStack, true);
            if (fluidInItem != null) {
                network.extractFluid(fluidInItem, fluidInItem.amount, compare);
                network.extractItem(RSUtils.EMPTY_BUCKET, 1, compare);
                actualInputs.add(insertStack.copy());
            } else {
                ItemStack input = network.extractItem(insertStack, insertStack.stackSize, compare);
                if (input != null) {
                    actualInputs.add(input);
                } else {
                    // Abort task re-insert taken stacks and reset state
                    toInsertItems.addAll(actualInputs.getStacks());
                    startedProcessing = false;
                    return;
                }
            }
        }

        ItemStack[] took = new ItemStack[9];
        for (int i = 0; i < pattern.getInputs().size(); i++) {
            ItemStack input = pattern.getInputs().get(i);
            if (input != null) {
                ItemStack actualInput = actualInputs.get(input, compare);
                ItemStack taken = ItemHandlerHelper.copyStackWithSize(actualInput, input.stackSize);
                took[i] = taken;
                actualInputs.remove(taken, true);
            }
        }

        for (ItemStack byproduct : (pattern.isOredict() ? pattern.getByproducts(took) : pattern.getByproducts())) {
            if (byproduct != null) {
                toInsertItems.add(byproduct.copy());
            }
        }

        for (ItemStack output : (pattern.isOredict() ? pattern.getOutputs(took) : pattern.getOutputs())) {
            if (output != null) {
                toInsertItems.add(output.copy());
            }
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        tag.setString(NBT_CRAFTING_STEP_TYPE, ID);
        super.writeToNBT(tag);

        NBTTagList toInsertList = new NBTTagList();

        for (ItemStack insert : toInsert) {
            toInsertList.appendTag(insert.serializeNBT());
        }

        tag.setTag(NBT_TO_INSERT, toInsertList);

        return tag;
    }

    @Override
    public boolean readFromNBT(NBTTagCompound tag) {
        if (super.readFromNBT(tag)) {
            if (tag.hasKey(NBT_TO_INSERT)) {
                NBTTagList toInsertList = tag.getTagList(CraftingTask.NBT_TO_INSERT_ITEMS, Constants.NBT.TAG_COMPOUND);
                toInsert = new ArrayList<>(toInsertList.tagCount());
                for (int i = 0; i < toInsertList.tagCount(); ++i) {
                    ItemStack insertStack = ItemStack.loadItemStackFromNBT(toInsertList.getCompoundTagAt(i));
                    if (insertStack != null) {
                        toInsert.add(insertStack);
                    }
                }
            }

            return true;
        }

        return false;
    }
}

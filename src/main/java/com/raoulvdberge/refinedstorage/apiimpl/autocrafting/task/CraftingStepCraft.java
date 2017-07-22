package com.raoulvdberge.refinedstorage.apiimpl.autocrafting.task;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fluids.FluidStack;

import java.util.*;
import java.util.stream.Collectors;

import com.raoulvdberge.refinedstorage.api.autocrafting.ICraftingPattern;
import com.raoulvdberge.refinedstorage.api.autocrafting.task.ICraftingStep;
import com.raoulvdberge.refinedstorage.api.network.INetwork;
import com.raoulvdberge.refinedstorage.api.util.IComparer;
import com.raoulvdberge.refinedstorage.api.util.IStackList;
import com.raoulvdberge.refinedstorage.apiimpl.API;
import com.raoulvdberge.refinedstorage.apiimpl.util.StackListItem;

public class CraftingStepCraft extends CraftingStep {
    public static final String ID = "craft";
    private static final String NBT_TO_INSERT = "ToInsert";

    private List<ItemStack> toInsert;

    public CraftingStepCraft(INetwork network, ICraftingPattern pattern, List<ItemStack> toInsert, List<ICraftingStep> preliminarySteps) {
        super(network, pattern, preliminarySteps);
        this.toInsert = new LinkedList<>();
        toInsert.forEach(stack -> this.toInsert.add(stack == null ? null : stack.copy()));
    }

    public CraftingStepCraft(INetwork network) {
        super(network);
    }

    @Override
    public List<ItemStack> getToInsert() {
        return toInsert == null ? super.getToInsert() : toInsert.stream().filter(Objects::nonNull).collect(Collectors.toList());
    }

    @Override
    public boolean canStartProcessing(IStackList<ItemStack> items, IStackList<FluidStack> fluids) {
        if (!super.canStartProcessing()) {
            return false;
        }
        int compare = CraftingTask.DEFAULT_COMPARE;
        for (ItemStack stack : getToInsert()) {
            // This will be a tool, like a hammer
            if (stack.isItemStackDamageable()) {
                compare &= ~IComparer.COMPARE_DAMAGE;
            } else {
                compare |= IComparer.COMPARE_DAMAGE;
            }

            ItemStack actualStack = items.get(stack, compare);
            if (isItemAvailable(items, fluids, stack, actualStack, compare) == null) {
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
        List<ItemStack> actualInputs = new LinkedList<>();
        if (extractItems(actualInputs, CraftingTask.DEFAULT_COMPARE, toInsertItems)) {
            IStackList<ItemStack> stackList = API.instance().createItemStackList();
            actualInputs.forEach(stackList::add);

            ItemStack[] took = StackListItem.toCraftingGrid(stackList, toInsert, CraftingTask.DEFAULT_COMPARE | (pattern.isOredict() ? IComparer.COMPARE_OREDICT : 0));

            List<ItemStack> outputs = pattern.isOredict() ? pattern.getOutputs(took) : pattern.getOutputs();
            if (outputs == null) {
                toInsertItems.addAll(actualInputs);
                startedProcessing = false;
                return;
            }

            for (ItemStack output : outputs) {
                if (output != null && !output.isEmpty()) {
                    toInsertItems.add(output.copy());
                }
            }

            for (ItemStack byproduct : (pattern.isOredict() ? pattern.getByproducts(took) : pattern.getByproducts())) {
                if (byproduct != null && !byproduct.isEmpty()) {
                    toInsertItems.add(byproduct.copy());
                }
            }
        } else {
            // Couldn't extract items
            startedProcessing = false;
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        tag.setString(NBT_CRAFTING_STEP_TYPE, ID);
        super.writeToNBT(tag);

        NBTTagList toInsertList = new NBTTagList();

        for (ItemStack insert : toInsert) {
            toInsertList.appendTag(insert == null ? new NBTTagCompound() : insert.serializeNBT());
        }

        tag.setTag(NBT_TO_INSERT, toInsertList);

        return tag;
    }

    @Override
    public boolean readFromNBT(NBTTagCompound tag) {
        if (super.readFromNBT(tag)) {
            if (tag.hasKey(NBT_TO_INSERT)) {
                NBTTagList toInsertList = tag.getTagList(NBT_TO_INSERT, Constants.NBT.TAG_COMPOUND);
                toInsert = new ArrayList<>(toInsertList.tagCount());
                for (int i = 0; i < toInsertList.tagCount(); ++i) {
                    ItemStack stack = new ItemStack(toInsertList.getCompoundTagAt(i));
                    if (stack.isEmpty()) {
                        toInsert.add(null);
                    } else {
                        toInsert.add(stack);
                    }
                }
            }

            return true;
        }

        return false;
    }
}

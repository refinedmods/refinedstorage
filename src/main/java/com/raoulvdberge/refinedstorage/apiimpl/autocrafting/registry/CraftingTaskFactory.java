package com.raoulvdberge.refinedstorage.apiimpl.autocrafting.registry;

import com.raoulvdberge.refinedstorage.api.autocrafting.ICraftingPattern;
import com.raoulvdberge.refinedstorage.api.autocrafting.ICraftingPatternChain;
import com.raoulvdberge.refinedstorage.api.autocrafting.registry.ICraftingTaskFactory;
import com.raoulvdberge.refinedstorage.api.autocrafting.task.ICraftingStep;
import com.raoulvdberge.refinedstorage.api.autocrafting.task.ICraftingTask;
import com.raoulvdberge.refinedstorage.api.network.INetwork;
import com.raoulvdberge.refinedstorage.api.util.IStackList;
import com.raoulvdberge.refinedstorage.apiimpl.autocrafting.task.CraftingStep;
import com.raoulvdberge.refinedstorage.apiimpl.autocrafting.task.CraftingTask;
import com.raoulvdberge.refinedstorage.util.StackUtils;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public class CraftingTaskFactory implements ICraftingTaskFactory {
    public static final String ID = "normal";

    @Override
    @Nonnull
    public ICraftingTask create(INetwork network, @Nullable ItemStack stack, ICraftingPattern pattern, int quantity, boolean automated, @Nullable NBTTagCompound tag) {
        if (tag != null) {
            NBTTagList stepsList = tag.getTagList(CraftingTask.NBT_STEPS, Constants.NBT.TAG_COMPOUND);

            List<ICraftingStep> steps = new ArrayList<>();

            for (int i = 0; i < stepsList.tagCount(); ++i) {
                NBTTagCompound stepTag = stepsList.getCompoundTagAt(i);

                ICraftingStep step = CraftingStep.toCraftingStep(stepTag, network);

                if (step != null) {
                    steps.add(step);
                }
            }

            NBTTagList toInsertList = tag.getTagList(CraftingTask.NBT_TO_INSERT_ITEMS, Constants.NBT.TAG_COMPOUND);

            Deque<ItemStack> toInsert = new ArrayDeque<>();

            for (int i = 0; i < toInsertList.tagCount(); ++i) {
                ItemStack insertStack = new ItemStack(toInsertList.getCompoundTagAt(i));

                if (!insertStack.isEmpty()) {
                    toInsert.add(insertStack);
                }
            }

            IStackList<FluidStack> toTakeFluids = StackUtils.readFluidStackList(tag.getTagList(CraftingTask.NBT_TO_TAKE_FLUIDS, Constants.NBT.TAG_COMPOUND));

            NBTTagList toInsertFluidsList = tag.getTagList(CraftingTask.NBT_TO_INSERT_FLUIDS, Constants.NBT.TAG_COMPOUND);

            Deque<FluidStack> toInsertFluids = new ArrayDeque<>();

            for (int i = 0; i < toInsertFluidsList.tagCount(); ++i) {
                FluidStack tookStack = FluidStack.loadFluidStackFromNBT(toInsertFluidsList.getCompoundTagAt(i));

                if (tookStack != null) {
                    toInsertFluids.add(tookStack);
                }
            }

            return new CraftingTask(network, stack, pattern, quantity, steps, toInsert, toTakeFluids, toInsertFluids, tag.hasKey(CraftingTask.NBT_AUTOMATED) && tag.getBoolean(CraftingTask.NBT_AUTOMATED));
        }

        return new CraftingTask(network, stack, pattern, quantity, automated);
    }

    @Nonnull
    @Override
    public ICraftingTask create(INetwork network, @Nullable ItemStack stack, ICraftingPatternChain patternChain, int quantity, boolean automated) {
        return new CraftingTask(network, stack, patternChain, quantity, automated);
    }
}
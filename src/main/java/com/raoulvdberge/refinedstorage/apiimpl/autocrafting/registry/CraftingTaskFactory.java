package com.raoulvdberge.refinedstorage.apiimpl.autocrafting.registry;

import com.raoulvdberge.refinedstorage.RSUtils;
import com.raoulvdberge.refinedstorage.api.autocrafting.ICraftingPattern;
import com.raoulvdberge.refinedstorage.api.autocrafting.registry.ICraftingTaskFactory;
import com.raoulvdberge.refinedstorage.api.autocrafting.task.ICraftingTask;
import com.raoulvdberge.refinedstorage.api.autocrafting.task.ICraftingStep;
import com.raoulvdberge.refinedstorage.api.network.INetworkMaster;
import com.raoulvdberge.refinedstorage.api.util.IFluidStackList;
import com.raoulvdberge.refinedstorage.apiimpl.autocrafting.task.CraftCraftingStep;
import com.raoulvdberge.refinedstorage.apiimpl.autocrafting.task.CraftingTask;
import com.raoulvdberge.refinedstorage.apiimpl.autocrafting.task.AbstractCraftingStep;
import com.raoulvdberge.refinedstorage.apiimpl.autocrafting.task.ProcessCraftingStep;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
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
    public ICraftingTask create(World world, INetworkMaster network, @Nullable ItemStack stack, ICraftingPattern pattern, int quantity, @Nullable NBTTagCompound tag) {
        if (tag != null) {
            NBTTagList toProcessList = tag.getTagList(CraftingTask.NBT_TO_PROCESS, Constants.NBT.TAG_COMPOUND);

            List<ICraftingStep> toProcess = new ArrayList<>();

            for (int i = 0; i < toProcessList.tagCount(); ++i) {
                NBTTagCompound compound = toProcessList.getCompoundTagAt(i);
                AbstractCraftingStep abstractCraftingStep;
                switch (compound.getString(AbstractCraftingStep.NBT_CRAFTING_STEP_TYPE))
                {
                    case CraftCraftingStep.ID:
                        abstractCraftingStep = new CraftCraftingStep(network);
                        break;
                    case ProcessCraftingStep.ID:
                        abstractCraftingStep = new ProcessCraftingStep(network);
                        break;
                    default:
                        abstractCraftingStep = null;
                        break;
                }

                if (abstractCraftingStep != null && abstractCraftingStep.readFromNBT(compound)) {
                    toProcess.add(abstractCraftingStep);
                }
            }


            NBTTagList toInsertList = tag.getTagList(CraftingTask.NBT_TO_INSERT_ITEMS, Constants.NBT.TAG_COMPOUND);

            Deque<ItemStack> toInsert = new ArrayDeque<>();

            for (int i = 0; i < toInsertList.tagCount(); ++i) {
                ItemStack insertStack = ItemStack.loadItemStackFromNBT(toInsertList.getCompoundTagAt(i));

                if (insertStack != null) {
                    toInsert.add(insertStack);
                }
            }

            IFluidStackList toTakeFluids = RSUtils.readFluidStackList(tag.getTagList(CraftingTask.NBT_TO_TAKE_FLUIDS, Constants.NBT.TAG_COMPOUND));
            IFluidStackList tookFluids = RSUtils.readFluidStackList(tag.getTagList(CraftingTask.NBT_TOOK_FLUIDS, Constants.NBT.TAG_COMPOUND));

            NBTTagList toInsertFluidsList = tag.getTagList(CraftingTask.NBT_TO_INSERT_FLUIDS, Constants.NBT.TAG_COMPOUND);

            Deque<FluidStack> toInsertFluids = new ArrayDeque<>();

            for (int i = 0; i < toInsertFluidsList.tagCount(); ++i) {
                FluidStack tookStack = FluidStack.loadFluidStackFromNBT(toInsertFluidsList.getCompoundTagAt(i));

                if (tookStack != null) {
                    toInsertFluids.add(tookStack);
                }
            }

            return new CraftingTask(network, stack, pattern, quantity, toProcess, toInsert, toTakeFluids, tookFluids, toInsertFluids);
        }

        return new CraftingTask(network, stack, pattern, quantity);
    }
}
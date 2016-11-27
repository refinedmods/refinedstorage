package com.raoulvdberge.refinedstorage.apiimpl.autocrafting.task;

import com.raoulvdberge.refinedstorage.RSUtils;
import com.raoulvdberge.refinedstorage.api.autocrafting.ICraftingPattern;
import com.raoulvdberge.refinedstorage.api.autocrafting.ICraftingPatternContainer;
import com.raoulvdberge.refinedstorage.api.autocrafting.ICraftingPatternProvider;
import com.raoulvdberge.refinedstorage.api.autocrafting.task.ICraftingStep;
import com.raoulvdberge.refinedstorage.api.network.INetworkMaster;
import com.raoulvdberge.refinedstorage.api.util.IComparer;
import com.raoulvdberge.refinedstorage.api.util.IFluidStackList;
import com.raoulvdberge.refinedstorage.api.util.IItemStackList;
import com.raoulvdberge.refinedstorage.apiimpl.API;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fluids.FluidStack;

import java.util.*;
import java.util.stream.Collectors;

public abstract class CraftingStep implements ICraftingStep {
    public static final String NBT_CRAFTING_STEP_TYPE = "CraftingStepType";
    private static final String NBT_SATISFIED = "Satisfied_%d";
    private static final String NBT_PATTERN = "Pattern";
    private static final String NBT_PATTERN_CONTAINER = "PatternContainer";
    private static final String NBT_STARTED_PROCESSING = "StartedProcessing";

    protected INetworkMaster network;
    protected ICraftingPattern pattern;
    protected Map<Integer, Integer> satisfied;
    protected boolean startedProcessing;

    public CraftingStep(INetworkMaster network, ICraftingPattern pattern) {
        this.network = network;
        this.pattern = pattern;
        this.satisfied = new HashMap<>(getPattern().getOutputs().size());
    }

    public CraftingStep(INetworkMaster network) {
        this.network = network;
    }

    public boolean readFromNBT(NBTTagCompound tag) {
        ItemStack patternStack = new ItemStack(tag.getCompoundTag(NBT_PATTERN));

        if (!patternStack.isEmpty()) {
            TileEntity container = network.getNetworkWorld().getTileEntity(BlockPos.fromLong(tag.getLong(NBT_PATTERN_CONTAINER)));

            if (container instanceof ICraftingPatternContainer) {
                this.pattern = ((ICraftingPatternProvider) patternStack.getItem()).create(network.getNetworkWorld(), patternStack, (ICraftingPatternContainer) container);
                this.satisfied = new HashMap<>(pattern.getOutputs().size());

                for (ItemStack stack : pattern.getOutputs()) {
                    int hashcode = API.instance().getItemStackHashCode(stack);
                    String id = String.format(NBT_SATISFIED, hashcode);

                    if (tag.hasKey(id)) {
                        this.satisfied.put(hashcode, tag.getInteger(id));
                    }
                }

                this.startedProcessing = tag.getBoolean(NBT_STARTED_PROCESSING);

                return true;
            }
        }

        return false;
    }

    @Override
    public ICraftingPattern getPattern() {
        return pattern;
    }

    @Override
    public List<ItemStack> getToInsert() {
        return pattern.getInputs().stream().filter(Objects::nonNull).collect(Collectors.toList());
    }

    @Override
    public boolean canStartProcessing() {
        return true;
    }


    @Override
    public void setStartedProcessing() {
        startedProcessing = true;
    }

    @Override
    public boolean hasStartedProcessing() {
        return startedProcessing;
    }

    @Override
    public boolean hasReceivedOutputs() {
        for (ItemStack stack : pattern.getOutputs()) {
            Integer received = satisfied.get(API.instance().getItemStackHashCode(stack));
            if (received == null || stack.getCount() > received) {
                return false;
            }
        }

        return true;
    }

    @Override
    public boolean hasReceivedOutput(ItemStack stack) {
        Integer received = satisfied.get(API.instance().getItemStackHashCode(stack));
        return received != null && received >= stack.getCount();
    }

    @Override
    public int getReceivedOutput(ItemStack stack) {
        Integer received = satisfied.get(API.instance().getItemStackHashCode(stack));
        return received == null ? 0 : received;
    }

    @Override
    public boolean onReceiveOutput(ItemStack stack) {
        for (ItemStack output : pattern.getOutputs()) {
            int hashcode = API.instance().getItemStackHashCode(output);
            Integer received = satisfied.get(hashcode);
            if (received == null) {
                received = 0;
            }
            if (API.instance().getComparer().isEqual(stack, output, CraftingTask.DEFAULT_COMPARE | (getPattern().isOredict() ? IComparer.COMPARE_OREDICT : 0))) {
                if (received < output.getCount()) {
                    int toReceive = Math.min(output.getCount() - received, stack.getCount());
                    satisfied.put(hashcode, received + toReceive);
                    stack.shrink(toReceive);

                    network.markCraftingMonitorForUpdate();

                    if (stack.getCount() == 0) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        for (Map.Entry<Integer, Integer> entry : satisfied.entrySet()) {
            tag.setInteger(String.format(NBT_SATISFIED, entry.getKey()), entry.getValue());
        }

        tag.setTag(NBT_PATTERN, pattern.getStack().serializeNBT());
        tag.setLong(NBT_PATTERN_CONTAINER, pattern.getContainer().getPosition().toLong());
        tag.setBoolean(NBT_STARTED_PROCESSING, startedProcessing);

        return tag;
    }

    enum AvailableType {
        ITEM, FLUID
    }

    protected AvailableType isItemAvailable(IItemStackList items, IFluidStackList fluids, ItemStack stack, ItemStack actualStack, int compare) {
        if (actualStack == null || actualStack.getCount() == 0 || !items.trackedRemove(actualStack, stack.getCount())) {
            FluidStack fluidInItem = RSUtils.getFluidFromStack(stack, true).getValue();

            if (fluidInItem != null && RSUtils.hasFluidBucket(fluidInItem)) {
                FluidStack fluidStack = fluids.get(fluidInItem, compare);
                ItemStack bucket = items.get(RSUtils.EMPTY_BUCKET, compare);
                if (bucket != null && fluidStack != null && fluids.trackedRemove(fluidStack, fluidInItem.amount, true) && items.trackedRemove(bucket, 1)) {
                    return AvailableType.FLUID;
                }
            }
            return null;
        }
        return AvailableType.ITEM;
    }

    protected boolean extractItems(IItemStackList actualInputs, int compare, Deque<ItemStack> toInsertItems) {
        for (ItemStack insertStack : getToInsert()) {
            // This will be a tool, like a hammer
            if (insertStack.isItemStackDamageable()) {
                compare &= ~IComparer.COMPARE_DAMAGE;
            } else {
                compare |= IComparer.COMPARE_DAMAGE;
            }

            ItemStack input = network.extractItem(insertStack, insertStack.getCount(), compare, false);
            if (input != null) {
                actualInputs.add(input);
            } else {
                boolean abort = true;
                FluidStack fluidInItem = RSUtils.getFluidFromStack(insertStack, true).getValue();
                if (fluidInItem != null) {
                    FluidStack fluidStack = network.extractFluid(fluidInItem, fluidInItem.amount, compare, false);
                    ItemStack bucketStack = network.extractItem(RSUtils.EMPTY_BUCKET, 1, compare, false);
                    if (fluidStack != null && fluidStack.amount == fluidInItem.amount && bucketStack != null) {
                        abort = false;
                        actualInputs.add(insertStack.copy());
                    }
                }

                if (abort) {
                    // Abort task re-insert taken stacks and reset state
                    toInsertItems.addAll(actualInputs.getStacks());
                    startedProcessing = false;
                    return false;
                }
            }
        }

        return true;
    }
}

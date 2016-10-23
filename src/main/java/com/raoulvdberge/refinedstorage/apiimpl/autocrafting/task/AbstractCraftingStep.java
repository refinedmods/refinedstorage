package com.raoulvdberge.refinedstorage.apiimpl.autocrafting.task;

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
import net.minecraft.nbt.NBTTagCompound;;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;

import java.util.*;
import java.util.stream.Collectors;

public abstract class AbstractCraftingStep implements ICraftingStep {
    public static final String NBT_CRAFTING_STEP_TYPE = "CraftingStepType";
    private static final String NBT_SATISFIED = "Satisfied_%d";
    private static final String NBT_PATTERN = "Pattern";
    private static final String NBT_PATTERN_CONTAINER = "PatternContainer";
    private static final String NBT_STARTED_PROCESSING = "StartedProcessing";

    protected INetworkMaster network;
    protected ICraftingPattern pattern;
    protected Map<Integer, Integer> satisfied;
    protected boolean startedProcessing;

    public AbstractCraftingStep(INetworkMaster network, ICraftingPattern pattern) {
        this.network = network;
        this.pattern = pattern;
        this.satisfied = new HashMap<>(getPattern().getOutputs().size());
    }

    public AbstractCraftingStep(INetworkMaster network) {
        this.network = network;
    }

    public boolean readFromNBT(NBTTagCompound tag) {
        ItemStack patternStack = ItemStack.loadItemStackFromNBT(tag.getCompoundTag(NBT_PATTERN));

        if (patternStack != null) {
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
    public boolean canStartProcessing(IItemStackList items, IFluidStackList fluids) {
        items = items.copy(); // So we can edit the list

        for (ItemStack stack : getToInsert()) {
            ItemStack actualStack = items.get(stack, IComparer.COMPARE_DAMAGE | IComparer.COMPARE_NBT | (pattern.isOredict() ? IComparer.COMPARE_OREDICT : 0));

            if (actualStack == null || actualStack.stackSize == 0 || !items.remove(actualStack, stack.stackSize, true)) {
                return false;
            }
        }

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
            if (received == null || stack.stackSize > received) {
                return false;
            }
        }

        return true;
    }

    @Override
    public boolean hasReceivedOutput(ItemStack stack) {
        Integer received = satisfied.get(API.instance().getItemStackHashCode(stack));
        return received != null && received >= stack.stackSize;
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
                if (received < output.stackSize) {
                    satisfied.put(hashcode, received + stack.stackSize);

                    network.sendCraftingMonitorUpdate();

                    return true;
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
}

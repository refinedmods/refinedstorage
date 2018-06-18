package com.raoulvdberge.refinedstorage.apiimpl.autocrafting.task.step;

import com.raoulvdberge.refinedstorage.api.autocrafting.ICraftingPattern;
import com.raoulvdberge.refinedstorage.api.autocrafting.task.CraftingTaskReadException;
import com.raoulvdberge.refinedstorage.api.network.INetwork;
import com.raoulvdberge.refinedstorage.apiimpl.autocrafting.task.CraftingTask;
import com.raoulvdberge.refinedstorage.apiimpl.autocrafting.task.inserter.CraftingInserter;
import net.minecraft.nbt.NBTTagCompound;

public abstract class CraftingStep {
    private static final String NBT_PATTERN = "Pattern";
    private static final String NBT_COMPLETED = "Completed";
    private static final String NBT_TYPE = "Type";

    protected ICraftingPattern pattern;
    private boolean completed;

    public CraftingStep(ICraftingPattern pattern) {
        this.pattern = pattern;
    }

    public abstract boolean canExecute();

    public abstract boolean execute();

    public ICraftingPattern getPattern() {
        return pattern;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted() {
        this.completed = true;
    }

    public abstract String getType();

    public NBTTagCompound writeToNbt() {
        NBTTagCompound tag = new NBTTagCompound();

        tag.setTag(NBT_PATTERN, CraftingTask.writePatternToNbt(pattern));
        tag.setBoolean(NBT_COMPLETED, completed);
        tag.setString(NBT_TYPE, getType());

        return tag;
    }

    public static CraftingStep readFromNbt(INetwork network, CraftingInserter inserter, NBTTagCompound tag) throws CraftingTaskReadException {
        ICraftingPattern pattern = CraftingTask.readPatternFromNbt(tag.getCompoundTag(NBT_PATTERN), network.world());
        boolean completed = tag.getBoolean(NBT_COMPLETED);
        String type = tag.getString(NBT_TYPE);

        CraftingStep step;

        switch (type) {
            case CraftingStepCraft.TYPE:
                step = new CraftingStepCraft(pattern, inserter, network, tag);
                break;
            case CraftingStepProcess.TYPE:
                step = new CraftingStepProcess(pattern, network, tag);
                break;
            default:
                throw new CraftingTaskReadException("Unknown crafting step type");
        }

        if (completed) {
            step.setCompleted();
        }

        return step;
    }
}

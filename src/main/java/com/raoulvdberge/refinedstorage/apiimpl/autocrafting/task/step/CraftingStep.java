package com.raoulvdberge.refinedstorage.apiimpl.autocrafting.task.step;

import com.raoulvdberge.refinedstorage.api.autocrafting.ICraftingPattern;
import com.raoulvdberge.refinedstorage.apiimpl.autocrafting.task.CraftingTask;
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
}

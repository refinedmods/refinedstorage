package refinedstorage.api.autocrafting.registry;

import net.minecraft.nbt.NBTTagCompound;
import refinedstorage.api.autocrafting.ICraftingPattern;
import refinedstorage.api.autocrafting.task.ICraftingTask;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface ICraftingTaskFactory {
    @Nonnull
    ICraftingTask create(@Nullable NBTTagCompound tag, ICraftingPattern pattern);
}

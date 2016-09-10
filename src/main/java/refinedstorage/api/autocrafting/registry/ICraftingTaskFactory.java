package refinedstorage.api.autocrafting.registry;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import refinedstorage.api.autocrafting.ICraftingPattern;
import refinedstorage.api.autocrafting.task.ICraftingTask;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * A factory that creates a crafting task from a NBT tag and crafting pattern.
 * Register this factory to create your own custom crafting tasks.
 */
public interface ICraftingTaskFactory {
    /**
     * Returns a crafting task for a given NBT tag and pattern.
     *
     * @param world   The world
     * @param tag     The NBT tag. If this is null it isn't reading from disk but is used for making a task on demand
     * @param pattern The pattern
     * @return The crafting task
     */
    @Nonnull
    ICraftingTask create(World world, @Nullable NBTTagCompound tag, ICraftingPattern pattern);
}

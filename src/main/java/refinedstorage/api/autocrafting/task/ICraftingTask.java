package refinedstorage.api.autocrafting.task;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import refinedstorage.api.autocrafting.ICraftingPattern;
import refinedstorage.api.network.INetworkMaster;

import javax.annotation.Nullable;

/**
 * Represents a crafting task.
 */
public interface ICraftingTask {
    /**
     * @return the pattern
     */
    ICraftingPattern getPattern();

    /**
     * @return the child task
     */
    @Nullable
    ICraftingTask getChild();

    /**
     * @param child the child task
     */
    void setChild(@Nullable ICraftingTask child);

    /**
     * @param world   the world
     * @param network the network
     * @return true if the crafting task is done, false otherwise
     */
    boolean update(World world, INetworkMaster network);

    /**
     * Gets called when the crafting task is cancelled.
     *
     * @param network the network
     */
    void onCancelled(INetworkMaster network);

    /**
     * Writes this crafting task to NBT.
     *
     * @param tag the NBT tag to write to
     * @return the written NBT tag
     */
    NBTTagCompound writeToNBT(NBTTagCompound tag);

    /**
     * Returns the status of this crafting task that is used for the tooltip in the crafting monitor.
     *
     * @return the status
     */
    String getStatus();

    /**
     * @return the progress for display in the crafting monitor, or -1 to not display any progress
     */
    int getProgress();
}

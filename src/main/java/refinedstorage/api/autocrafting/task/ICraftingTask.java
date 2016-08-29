package refinedstorage.api.autocrafting.task;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import refinedstorage.api.autocrafting.ICraftingPattern;
import refinedstorage.api.network.INetworkMaster;

import java.util.List;

/**
 * Represents a crafting task.
 */
public interface ICraftingTask {
    /**
     * @return The pattern
     */
    ICraftingPattern getPattern();

    /**
     * @return The child tasks
     */
    List<ICraftingTask> getChildren();

    /**
     * @param world   The world
     * @param network The network
     * @return If the crafting task is done
     */
    boolean update(World world, INetworkMaster network);

    /**
     * Gets called when this crafting task is cancelled.
     *
     * @param network The network
     */
    void onCancelled(INetworkMaster network);

    /**
     * Writes this crafting task to NBT.
     *
     * @param tag The NBT tag to write to
     */
    NBTTagCompound writeToNBT(NBTTagCompound tag);

    /**
     * Returns status info used in the tooltip of the crafting monitor.
     *
     * @return The status
     */
    String getStatus();
}

package refinedstorage.api.autocrafting.task;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import refinedstorage.api.autocrafting.ICraftingPattern;
import refinedstorage.api.network.INetworkMaster;

import javax.annotation.Nullable;
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
     * @return The child task
     */
    @Nullable
    ICraftingTask getChild();

    /**
     * @param child The child task
     */
    void setChild(@Nullable ICraftingTask child);

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

    /**
     * @return The progress for display in the crafting monitor, -1 for no progress
     */
    int getProgress();

    /**
     * @return The items that are required for this tasks's crafting pattern, but are not present.
     */
    List<ItemStack> getMissingItems();
}

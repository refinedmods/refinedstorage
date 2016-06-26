package refinedstorage.api.autocrafting;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import refinedstorage.api.network.INetworkMaster;

/**
 * Represents a crafting task.
 */
public interface ICraftingTask {
    /**
     * @return The pattern
     */
    ICraftingPattern getPattern();

    /**
     * @param world   The world
     * @param network The network
     * @return If the crafting task is done
     */
    boolean update(World world, INetworkMaster network);

    /**
     * Gets called as soon as {@link ICraftingTask#update(World, INetworkMaster)} returns true.
     *
     * @param network The network
     */
    void onDone(INetworkMaster network);

    /**
     * Gets called when this crafting task is cancelled.
     *
     * @param network The network
     */
    void onCancelled(INetworkMaster network);

    /**
     * Writes this crafting task to NBT.
     *
     * @param tag The NBT tag
     */
    void writeToNBT(NBTTagCompound tag);

    /**
     * Returns the info string that the crafting monitor uses.
     * Separate every line by the newline character.
     * Use T=x where x is the translation key for translating.
     * Use I=x where x is the translation key for translating and displaying the line in yellow.
     *
     * @return The info string
     */
    String getInfo();
}

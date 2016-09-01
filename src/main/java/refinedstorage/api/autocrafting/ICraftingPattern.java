package refinedstorage.api.autocrafting;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

/**
 * Represents a crafting pattern.
 */
public interface ICraftingPattern {
    /**
     * @param world The world
     * @return The container where the pattern is in
     */
    ICraftingPatternContainer getContainer(World world);

    /**
     * @return The crafting pattern stack
     */
    ItemStack getStack();

    /**
     * @return The inputs
     */
    ItemStack[] getInputs();

    /**
     * @return The outputs
     */
    ItemStack[] getOutputs();

    /**
     * @return The byproducts
     */
    ItemStack[] getByproducts();

    /**
     * @return The id of the crafting task, as defined in the registry
     */
    String getId();

    /**
     * @param requested The item requested
     * @return The quantity returned per request
     */
    int getQuantityPerRequest(ItemStack requested);

    /**
     * Writes this pattern to NBT.
     *
     * @param tag The NBT tag to write to
     * @return The written NBT tag
     */
    NBTTagCompound writeToNBT(NBTTagCompound tag);
}

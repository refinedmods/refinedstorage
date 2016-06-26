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
     * @return Returns the container where the pattern is in
     */
    ICraftingPatternContainer getContainer(World world);

    /**
     * @return If this pattern is a processing pattern
     */
    boolean isProcessing();

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
     * Writes this pattern to NBT.
     *
     * @param tag The NBT tag
     * @return The NBT tag
     */
    NBTTagCompound writeToNBT(NBTTagCompound tag);
}

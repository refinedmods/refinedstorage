package refinedstorage.api.autocrafting;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public interface ICraftingPattern {
    ICraftingPatternContainer getContainer(World world);

    boolean isProcessing();

    ItemStack[] getInputs();

    ItemStack[] getOutputs();

    ItemStack[] getByproducts();

    NBTTagCompound writeToNBT(NBTTagCompound tag);
}

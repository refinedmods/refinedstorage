package refinedstorage.apiimpl.autocrafting.registry;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import refinedstorage.api.autocrafting.ICraftingPattern;
import refinedstorage.api.autocrafting.registry.ICraftingTaskFactory;
import refinedstorage.api.autocrafting.task.ICraftingTask;
import refinedstorage.api.network.INetworkMaster;
import refinedstorage.apiimpl.autocrafting.task.CraftingTask;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class CraftingTaskFactory implements ICraftingTaskFactory {
    public static final String ID = "normal";

    @Override
    @Nonnull
    public ICraftingTask create(World world, INetworkMaster network, @Nullable ItemStack stack, ICraftingPattern pattern, int quantity, @Nullable NBTTagCompound tag) {
        return new CraftingTask(network, stack, pattern, quantity);
    }
}
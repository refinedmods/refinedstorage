package refinedstorage.apiimpl.autocrafting.registry;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import refinedstorage.api.autocrafting.ICraftingPattern;
import refinedstorage.api.autocrafting.registry.ICraftingTaskFactory;
import refinedstorage.api.autocrafting.task.ICraftingTask;
import refinedstorage.api.network.INetworkMaster;
import refinedstorage.apiimpl.autocrafting.task.CraftingTaskNormal;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class CraftingTaskFactoryNormal implements ICraftingTaskFactory {
    public static final String ID = "normal";

    @Override
    @Nonnull
    public ICraftingTask create(World world, INetworkMaster network, ICraftingPattern pattern, int quantity, @Nullable NBTTagCompound tag) {
        return new CraftingTaskNormal(network, pattern, quantity);
    }
}
package refinedstorage.api.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import refinedstorage.api.RefinedStorageAPI;
import refinedstorage.api.autocrafting.ICraftingPattern;
import refinedstorage.api.autocrafting.task.ICraftingTask;
import refinedstorage.api.storage.CompareUtils;

/**
 * Utilities for network manipulation.
 */
public final class NetworkUtils {
    public static ItemStack extractItem(INetworkMaster network, ItemStack stack, int size) {
        return network.extractItem(stack, size, CompareUtils.COMPARE_DAMAGE | CompareUtils.COMPARE_NBT);
    }

    public static FluidStack extractFluid(INetworkMaster network, FluidStack stack, int size) {
        return network.extractFluid(stack, size, CompareUtils.COMPARE_NBT);
    }

    public static ICraftingPattern getPattern(INetworkMaster network, ItemStack stack) {
        return network.getPattern(stack, CompareUtils.COMPARE_DAMAGE | CompareUtils.COMPARE_NBT);
    }

    public static ICraftingTask createCraftingTask(INetworkMaster network, ICraftingPattern pattern) {
        return RefinedStorageAPI.CRAFTING_TASK_REGISTRY.getFactory(pattern.getId()).create(network.getNetworkWorld(), null, pattern);
    }

    public static boolean hasPattern(INetworkMaster network, ItemStack stack) {
        return getPattern(network, stack) != null;
    }

    public static void rebuildGraph(INetworkMaster network) {
        network.getNodeGraph().rebuild(network.getPosition(), true);
    }

    public static int getItemStackHashCode(ItemStack stack) {
        return stack.getItem().hashCode() * (stack.getItemDamage() + 1) * (stack.hasTagCompound() ? stack.getTagCompound().hashCode() : 1);
    }

    public static int getFluidStackHashCode(FluidStack stack) {
        return stack.getFluid().hashCode() * (stack.tag != null ? stack.tag.hashCode() : 1);
    }

    public static void scheduleCraftingTaskIfUnscheduled(INetworkMaster network, ItemStack stack, int toSchedule, int compare) {
        int alreadyScheduled = 0;

        for (ICraftingTask task : network.getCraftingTasks()) {
            for (ItemStack output : task.getPattern().getOutputs()) {
                if (CompareUtils.compareStack(output, stack, compare)) {
                    alreadyScheduled++;
                }
            }
        }

        for (int i = 0; i < toSchedule - alreadyScheduled; ++i) {
            ICraftingPattern pattern = network.getPattern(stack, compare);

            if (pattern != null) {
                network.addCraftingTask(createCraftingTask(network, pattern));
            }
        }
    }

    public static void writeItemStack(ByteBuf buf, INetworkMaster network, ItemStack stack) {
        buf.writeInt(Item.getIdFromItem(stack.getItem()));
        buf.writeInt(stack.stackSize);
        buf.writeInt(stack.getItemDamage());
        ByteBufUtils.writeTag(buf, stack.getTagCompound());
        buf.writeInt(getItemStackHashCode(stack));
        buf.writeBoolean(hasPattern(network, stack));
    }

    public static void writeFluidStack(ByteBuf buf, FluidStack stack) {
        buf.writeInt(getFluidStackHashCode(stack));
        ByteBufUtils.writeUTF8String(buf, FluidRegistry.getFluidName(stack.getFluid()));
        buf.writeInt(stack.amount);
        ByteBufUtils.writeTag(buf, stack.tag);
    }
}

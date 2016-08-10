package refinedstorage.api.network;

import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import refinedstorage.api.autocrafting.ICraftingPattern;
import refinedstorage.api.storage.CompareUtils;

/**
 * Utilities for network manipulation.
 */
public final class NetworkUtils {
    public static ItemStack extractItem(INetworkMaster network, ItemStack stack, int size) {
        return network.extractItem(stack, size, CompareUtils.COMPARE_DAMAGE | CompareUtils.COMPARE_NBT);
    }

    public static ICraftingPattern getPattern(INetworkMaster network, ItemStack stack) {
        return network.getPattern(stack, CompareUtils.COMPARE_DAMAGE | CompareUtils.COMPARE_NBT);
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

    public static int getNodeHashCode(World world, INetworkNode node) {
        int result = node.getPosition().hashCode();
        result = 31 * result + world.provider.getDimension();
        return result;
    }
}

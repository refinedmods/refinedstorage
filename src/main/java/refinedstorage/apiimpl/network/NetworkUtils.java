package refinedstorage.apiimpl.network;

import net.minecraft.item.ItemStack;
import refinedstorage.api.autocrafting.ICraftingPattern;
import refinedstorage.api.network.INetworkMaster;
import refinedstorage.api.storage.CompareUtils;

public final class NetworkUtils {
    public static int convertIC2ToRF(double amount) {
        return amount >= Double.POSITIVE_INFINITY ? Integer.MAX_VALUE : ((int) Math.floor(amount) * 4);
    }

    public static double convertRFToIC2(int amount) {
        return Math.floor(amount / 4);
    }

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
}

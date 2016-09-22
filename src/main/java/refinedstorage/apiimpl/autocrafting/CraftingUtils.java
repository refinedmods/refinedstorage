package refinedstorage.apiimpl.autocrafting;

import net.minecraft.item.ItemStack;
import refinedstorage.api.autocrafting.ICraftingPattern;
import refinedstorage.api.network.INetworkMaster;
import refinedstorage.api.network.NetworkUtils;
import refinedstorage.api.storage.CompareUtils;

import java.util.Collection;
import java.util.HashMap;

public final class CraftingUtils {
    public static Collection<AutoCraftInfoStack> getItems(INetworkMaster network, ItemStack stack, int quantity) {
        AutoCraftInfoData data = new AutoCraftInfoData(network);
        calcItems(network, stack, quantity, data);
        return data.values();
    }

    private static void calcItems(INetworkMaster network, ItemStack stack, int quantity, AutoCraftInfoData data) {
        quantity = -data.add(stack, quantity);
        if (quantity > 0) {
            ICraftingPattern pattern = NetworkUtils.getPattern(network, stack);
            if (pattern != null) {
                int quantityPerRequest = pattern.getQuantityPerRequest(stack);
                while (quantity > 0) {
                    for (ItemStack ingredient : pattern.getInputs()) {
                        calcItems(network, ingredient, ingredient.stackSize, data);
                    }
                    data.get(stack).addExtras(quantityPerRequest);
                    quantity -= quantityPerRequest;
                }
            } else {
                data.get(stack).setCantCraft(true);
            }
        }
    }

    private static class AutoCraftInfoData {
        private HashMap<Integer, AutoCraftInfoStack> data;
        private INetworkMaster network;

        private AutoCraftInfoData(INetworkMaster network) {
            this.data = new HashMap<>();
            this.network = network;
        }

        /**
         * @return available stacks, if negative needs crafting
         */
        private int add(ItemStack stack, int quantity) {
            int hash = NetworkUtils.getItemStackHashCode(stack);
            if (data.containsKey(hash)) {
                AutoCraftInfoStack infoStack = data.get(hash);
                infoStack.addNeeded(quantity);
                return infoStack.getAvailable();
            } else {
                ItemStack networkStack = network.getItemStorage().get(stack, CompareUtils.COMPARE_DAMAGE | CompareUtils.COMPARE_NBT);
                AutoCraftInfoStack infoStack = new AutoCraftInfoStack(stack, quantity, networkStack == null ? 0 : networkStack.stackSize);
                data.put(hash, infoStack);
                return infoStack.getAvailable();
            }
        }

        private AutoCraftInfoStack get(ItemStack stack) {
            return this.data.get(NetworkUtils.getItemStackHashCode(stack));
        }

        private Collection<AutoCraftInfoStack> values() {
            return this.data.values();
        }
    }
}

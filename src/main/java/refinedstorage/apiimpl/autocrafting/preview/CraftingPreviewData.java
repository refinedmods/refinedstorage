package refinedstorage.apiimpl.autocrafting.preview;

import net.minecraft.item.ItemStack;
import refinedstorage.api.autocrafting.ICraftingPattern;
import refinedstorage.api.autocrafting.task.CraftingTask;
import refinedstorage.api.network.INetworkMaster;
import refinedstorage.api.network.NetworkUtils;
import refinedstorage.api.storage.CompareUtils;
import refinedstorage.apiimpl.autocrafting.CraftingPattern;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;

public class CraftingPreviewData {
    private HashMap<Integer, CraftingPreviewStack> data = new HashMap<>();
    private INetworkMaster network;
    private int depth = 0;
    private boolean depthCheck = false;

    public CraftingPreviewData(INetworkMaster network) {
        this.network = network;
    }

    public void calculate(ItemStack stack, int quantity) {
        calculate(stack, quantity, true);
    }

    private void calculate(ItemStack stack, int quantity, boolean baseStack) {
        quantity = -add(stack, quantity, baseStack);
        
        if (quantity > 0 && !get(stack).cantCraft()) {
            ICraftingPattern pattern = NetworkUtils.getPattern(network, stack);

            if (pattern != null && depth < CraftingTask.MAX_DEPTH) {
                int quantityPerRequest = pattern.getQuantityPerRequest(stack);

                while (quantity > 0) {
                    for (ItemStack ingredient : pattern.getInputs()) {
                        depth++;
                        calculate(ingredient, ingredient.stackSize, false);
                        depth--;
                    }

                    get(stack).addExtras(quantityPerRequest);

                    quantity -= quantityPerRequest;
                }
            } else {
                if (depth >= CraftingTask.MAX_DEPTH) {
                    this.depthCheck = true;
                }
                get(stack).setCantCraft(true);
            }
        }
    }

    public int add(ItemStack stack, int quantity, boolean baseStack) {
        int hash = NetworkUtils.getItemStackHashCode(stack);

        CraftingPreviewStack previewStack;
        if (data.containsKey(hash)) {
            previewStack = data.get(hash);

            previewStack.addNeeded(quantity);
        } else {
            ItemStack networkStack = network.getItemStorage().get(stack, CompareUtils.COMPARE_DAMAGE | CompareUtils.COMPARE_NBT);

            previewStack = new CraftingPreviewStack(stack, quantity, networkStack == null || baseStack ? 0 : networkStack.stackSize);

            data.put(hash, previewStack);
        }
        return baseStack ? -quantity : previewStack.getAvailable();
    }

    public CraftingPreviewStack get(ItemStack stack) {
        return data.get(NetworkUtils.getItemStackHashCode(stack));
    }

    public Collection<CraftingPreviewStack> values() {
        return this.depthCheck ? Collections.emptyList() : this.data.values();
    }
}

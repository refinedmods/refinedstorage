package refinedstorage.api.network;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;

public interface IGridHandler {
    void onPull(ItemStack stack, int flags, EntityPlayerMP player);

    ItemStack onPush(ItemStack stack);

    void onHeldItemPush(boolean single, EntityPlayerMP player);

    void onCraftingRequested(ItemStack stack, int quantity);

    void onCraftingCancelRequested(int id);
}

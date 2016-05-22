package refinedstorage.inventory;

import net.minecraft.item.ItemStack;

public interface IItemValidator {
    boolean valid(ItemStack stack);
}

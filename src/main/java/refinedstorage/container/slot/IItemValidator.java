package refinedstorage.container.slot;

import net.minecraft.item.ItemStack;

public interface IItemValidator {
    public boolean isValid(ItemStack stack);
}

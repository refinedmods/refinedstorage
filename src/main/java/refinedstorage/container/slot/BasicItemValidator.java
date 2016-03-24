package refinedstorage.container.slot;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class BasicItemValidator implements IItemValidator {
    private Item item;

    public BasicItemValidator(Item item) {
        this.item = item;
    }

    @Override
    public boolean isValid(ItemStack stack) {
        return item == stack.getItem();
    }
}

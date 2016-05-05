package refinedstorage.container.slot;

import net.minecraft.item.ItemStack;
import refinedstorage.RefinedStorageItems;

import java.util.Arrays;

public class SlotUpgrade implements IItemValidator {
    private int[] allowedUpgrades;

    public SlotUpgrade(int... allowedUpgrades) {
        this.allowedUpgrades = allowedUpgrades;
    }

    @Override
    public boolean isValid(ItemStack stack) {
        return stack.getItem() == RefinedStorageItems.UPGRADE && Arrays.asList(allowedUpgrades).contains(stack.getMetadata());
    }
}

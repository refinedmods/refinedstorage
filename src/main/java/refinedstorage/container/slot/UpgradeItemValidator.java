package refinedstorage.container.slot;

import net.minecraft.item.ItemStack;
import refinedstorage.RefinedStorageItems;

import java.util.Arrays;

public class UpgradeItemValidator implements IItemValidator {
    private int[] allowedUpgrades;

    public UpgradeItemValidator(int... allowedUpgrades) {
        this.allowedUpgrades = allowedUpgrades;
    }

    @Override
    public boolean isValid(ItemStack stack) {
        return stack.getItem() == RefinedStorageItems.UPGRADE && Arrays.asList(allowedUpgrades).contains(stack.getMetadata());
    }
}

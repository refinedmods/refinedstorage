package refinedstorage.container.slot;

import net.minecraft.item.ItemStack;
import refinedstorage.RefinedStorageItems;

public class UpgradeItemValidator implements IItemValidator {
    private int[] allowedUpgrades;

    public UpgradeItemValidator(int... allowedUpgrades) {
        this.allowedUpgrades = allowedUpgrades;
    }

    @Override
    public boolean isValid(ItemStack stack) {
        if (stack.getItem() == RefinedStorageItems.UPGRADE) {
            for (int upgrade : allowedUpgrades) {
                if (upgrade == stack.getMetadata()) {
                    return true;
                }
            }
        }

        return false;
    }
}

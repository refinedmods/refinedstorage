package refinedstorage.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import refinedstorage.RefinedStorage;

import java.util.List;

public class ItemUpgrade extends ItemBase {
    public static final int TYPE_RANGE = 1;
    public static final int TYPE_SPEED = 2;
    public static final int TYPE_CRAFTING = 3;
    public static final int TYPE_STACK = 4;

    public ItemUpgrade() {
        super("upgrade");

        setHasSubtypes(true);
        setMaxDamage(0);
        setMaxStackSize(1);
    }

    @Override
    public void getSubItems(Item item, CreativeTabs tab, List list) {
        for (int i = 0; i <= 4; ++i) {
            list.add(new ItemStack(item, 1, i));
        }
    }

    public static int getEnergyUsage(int type) {
        switch (type) {
            case TYPE_RANGE:
                return RefinedStorage.INSTANCE.rangeUpgradeRfUsage;
            case TYPE_SPEED:
                return RefinedStorage.INSTANCE.speedUpgradeRfUsage;
            case TYPE_CRAFTING:
                return RefinedStorage.INSTANCE.craftingUpgradeRfUsage;
            case TYPE_STACK:
                return RefinedStorage.INSTANCE.stackUpgradeRfUsage;
            default:
                return 0;
        }
    }
}

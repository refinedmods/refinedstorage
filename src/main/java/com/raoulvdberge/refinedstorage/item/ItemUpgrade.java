package com.raoulvdberge.refinedstorage.item;

import com.raoulvdberge.refinedstorage.RS;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class ItemUpgrade extends ItemBase {
    public static final int TYPE_RANGE = 1;
    public static final int TYPE_SPEED = 2;
    public static final int TYPE_CRAFTING = 3;
    public static final int TYPE_STACK = 4;
    public static final int TYPE_INTERDIMENSIONAL = 5;
    public static final int TYPE_SILK_TOUCH = 6;
    public static final int TYPE_FORTUNE_1 = 7;
    public static final int TYPE_FORTUNE_2 = 8;
    public static final int TYPE_FORTUNE_3 = 9;

    public ItemUpgrade() {
        super("upgrade");

        setHasSubtypes(true);
        setMaxDamage(0);
    }

    @Override
    public boolean hasEffect(ItemStack stack) {
        return stack.getMetadata() == TYPE_SILK_TOUCH || getFortuneLevel(stack) > 0;
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag flag) {
        super.addInformation(stack, world, tooltip, flag);

        if (getFortuneLevel(stack) > 0) {
            tooltip.add(I18n.format("enchantment.lootBonusDigger") + " " + I18n.format("enchantment.level." + getFortuneLevel(stack)));
        }
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        if (!isInCreativeTab(tab)) {
            return;
        }

        for (int i = 0; i <= 9; ++i) {
            items.add(new ItemStack(this, 1, i));
        }
    }

    public static int getFortuneLevel(@Nullable ItemStack stack) {
        if (stack != null) {
            if (stack.getMetadata() == TYPE_FORTUNE_1) {
                return 1;
            } else if (stack.getMetadata() == TYPE_FORTUNE_2) {
                return 2;
            } else if (stack.getMetadata() == TYPE_FORTUNE_3) {
                return 3;
            }
        }

        return 0;
    }

    public static int getEnergyUsage(ItemStack stack) {
        switch (stack.getItemDamage()) {
            case TYPE_RANGE:
                return RS.INSTANCE.config.rangeUpgradeUsage;
            case TYPE_SPEED:
                return RS.INSTANCE.config.speedUpgradeUsage;
            case TYPE_CRAFTING:
                return RS.INSTANCE.config.craftingUpgradeUsage;
            case TYPE_STACK:
                return RS.INSTANCE.config.stackUpgradeUsage;
            case TYPE_INTERDIMENSIONAL:
                return RS.INSTANCE.config.interdimensionalUpgradeUsage;
            case TYPE_SILK_TOUCH:
                return RS.INSTANCE.config.silkTouchUpgradeUsage;
            case TYPE_FORTUNE_1:
            case TYPE_FORTUNE_2:
            case TYPE_FORTUNE_3:
                return RS.INSTANCE.config.fortuneUpgradeUsagePerFortune * getFortuneLevel(stack);
            default:
                return 0;
        }
    }
}

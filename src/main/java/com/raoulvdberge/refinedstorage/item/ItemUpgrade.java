package com.raoulvdberge.refinedstorage.item;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.RSItems;
import com.raoulvdberge.refinedstorage.RSUtils;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.init.Items;
import net.minecraft.item.ItemEnchantedBook;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;

import javax.annotation.Nullable;
import java.util.List;

public class ItemUpgrade extends ItemBase {
    public static final int TYPE_RANGE = 1;
    public static final int TYPE_SPEED = 2;
    public static final int TYPE_CRAFTING = 3;
    public static final int TYPE_STACK = 4;
    public static final int TYPE_INTERDIMENSIONAL = 5;
    public static final int TYPE_SILK_TOUCH = 6;
    public static final int TYPE_FORTUNE = 7;

    private static final String NBT_FORTUNE = "Fortune";

    public ItemUpgrade() {
        super("upgrade");

        setHasSubtypes(true);
        setMaxDamage(0);
    }

    @Override
    public boolean hasEffect(ItemStack stack) {
        return stack.getMetadata() == TYPE_SILK_TOUCH || stack.getMetadata() == TYPE_FORTUNE;
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag flag) {
        super.addInformation(stack, world, tooltip, flag);

        if (stack.getItemDamage() == TYPE_FORTUNE) {
            tooltip.add(I18n.format("enchantment.lootBonusDigger") + " " + I18n.format("enchantment.level." + ItemUpgrade.getFortuneLevel(stack)));
        }
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        if (!isInCreativeTab(tab)) {
            return;
        }

        for (int i = 0; i <= 6; ++i) {
            items.add(new ItemStack(this, 1, i));
        }

        for (int j = 1; j <= 3; ++j) {
            items.add(initializeForFortune(j));
        }
    }

    public static ItemStack initializeForFortune(int level) {
        ItemStack stack = new ItemStack(RSItems.UPGRADE, 1, TYPE_FORTUNE);
        stack.setTagCompound(new NBTTagCompound());
        stack.getTagCompound().setInteger(NBT_FORTUNE, level);
        return stack;
    }

    public static int getFortuneLevel(ItemStack stack) {
        if (stack != null && stack.getItemDamage() == ItemUpgrade.TYPE_FORTUNE) {
            NBTTagCompound tag = stack.getTagCompound();

            if (tag.hasKey(ItemUpgrade.NBT_FORTUNE)) {
                return tag.getInteger(ItemUpgrade.NBT_FORTUNE);
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
            case TYPE_FORTUNE:
                return RS.INSTANCE.config.fortuneUpgradeUsagePerFortune * ItemUpgrade.getFortuneLevel(stack);
            default:
                return 0;
        }
    }

    public static NonNullList<ItemStack> getRequirement(ItemStack stack) {
        switch (stack.getItemDamage()) {
            case ItemUpgrade.TYPE_RANGE:
                return OreDictionary.getOres("enderpearl");
            case ItemUpgrade.TYPE_SPEED:
                return NonNullList.withSize(1, new ItemStack(Items.SUGAR));
            case ItemUpgrade.TYPE_CRAFTING:
                return OreDictionary.getOres("workbench");
            case ItemUpgrade.TYPE_INTERDIMENSIONAL:
                return OreDictionary.getOres("netherStar");
            case ItemUpgrade.TYPE_SILK_TOUCH:
                return NonNullList.withSize(1, ItemEnchantedBook.getEnchantedItemStack(new EnchantmentData(Enchantment.getEnchantmentByLocation("silk_touch"), 1)));
            case ItemUpgrade.TYPE_FORTUNE:
                return NonNullList.withSize(1, ItemEnchantedBook.getEnchantedItemStack(new EnchantmentData(Enchantment.getEnchantmentByLocation("fortune"), getFortuneLevel(stack))));
            default:
                return RSUtils.emptyNonNullList();
        }
    }
}

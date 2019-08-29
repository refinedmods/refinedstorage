package com.raoulvdberge.refinedstorage.recipe;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.RSItems;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemEnchantedBook;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;

// MC JSON recipes don't like comparing to NBT, that's why we need a custom recipe class.
// We need to compare to NBT for the enchanted book.
public class RecipeUpgradeWithEnchantedBook extends ShapedRecipes {
    private EnchantmentData enchant;

    public RecipeUpgradeWithEnchantedBook(String enchantmentId, int enchantmentLevel, int upgradeId) {
        super(RS.ID, 3, 3, NonNullList.from(Ingredient.EMPTY,
            Ingredient.fromStacks(new ItemStack(RSItems.QUARTZ_ENRICHED_IRON)),
            Ingredient.fromStacks(ItemEnchantedBook.getEnchantedItemStack(new EnchantmentData(Enchantment.getEnchantmentByLocation(enchantmentId), enchantmentLevel))),
            Ingredient.fromStacks(new ItemStack(RSItems.QUARTZ_ENRICHED_IRON)),
            Ingredient.fromStacks(new ItemStack(Blocks.BOOKSHELF)),
            Ingredient.fromStacks(new ItemStack(RSItems.UPGRADE)),
            Ingredient.fromStacks(new ItemStack(Blocks.BOOKSHELF)),
            Ingredient.fromStacks(new ItemStack(RSItems.QUARTZ_ENRICHED_IRON)),
            Ingredient.fromStacks(new ItemStack(RSItems.QUARTZ_ENRICHED_IRON)),
            Ingredient.fromStacks(new ItemStack(RSItems.QUARTZ_ENRICHED_IRON))
        ), new ItemStack(RSItems.UPGRADE, 1, upgradeId));

        this.enchant = new EnchantmentData(Enchantment.getEnchantmentByLocation(enchantmentId), enchantmentLevel);
    }

    @Override
    public boolean matches(InventoryCrafting inv, World world) {
        if (super.matches(inv, world)) {
            ListNBT enchantments = ItemEnchantedBook.getEnchantments(inv.getStackInSlot(1));

            for (int i = 0; i < enchantments.size(); ++i) {
                CompoundNBT enchantmentNbt = enchantments.getCompound(i);

                // @Volatile: NBT tags from ItemEnchantedBook
                if (Enchantment.getEnchantmentByID(enchantmentNbt.getShort("id")) == enchant.enchantment && enchantmentNbt.getShort("lvl") == enchant.enchantmentLevel) {
                    return true;
                }
            }
        }

        return false;
    }
}

package com.refinedmods.refinedstorage.recipe;

import com.refinedmods.refinedstorage.RSItems;
import com.refinedmods.refinedstorage.item.UpgradeItem;
import net.minecraft.block.Blocks;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipe;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistries;

public class UpgradeWithEnchantedBookRecipe extends ShapedRecipe {
    private final EnchantmentData enchant;
    private final ItemStack result;

    public UpgradeWithEnchantedBookRecipe(ResourceLocation recipeId, Enchantment enchantment, int enchantmentLevel, ItemStack result) {
        super(recipeId, "", 3, 3, NonNullList.of(Ingredient.EMPTY,
            Ingredient.of(new ItemStack(RSItems.QUARTZ_ENRICHED_IRON.get())),
            Ingredient.of(EnchantedBookItem.createForEnchantment(new EnchantmentData(enchantment, enchantmentLevel))),
            Ingredient.of(new ItemStack(RSItems.QUARTZ_ENRICHED_IRON.get())),
            Ingredient.of(new ItemStack(Blocks.BOOKSHELF)),
            Ingredient.of(new ItemStack(RSItems.UPGRADE_ITEMS.get(UpgradeItem.Type.NORMAL).get())),
            Ingredient.of(new ItemStack(Blocks.BOOKSHELF)),
            Ingredient.of(new ItemStack(RSItems.QUARTZ_ENRICHED_IRON.get())),
            Ingredient.of(new ItemStack(RSItems.QUARTZ_ENRICHED_IRON.get())),
            Ingredient.of(new ItemStack(RSItems.QUARTZ_ENRICHED_IRON.get()))
        ), result);

        this.enchant = new EnchantmentData(enchantment, enchantmentLevel);
        this.result = result;
    }

    public EnchantmentData getEnchant() {
        return enchant;
    }

    public ItemStack getResult() {
        return result;
    }

    @Override
    public boolean matches(CraftingInventory inv, World world) {
        if (super.matches(inv, world)) {
            ListNBT enchantments = EnchantedBookItem.getEnchantments(inv.getItem(1));

            for (int i = 0; i < enchantments.size(); ++i) {
                CompoundNBT enchantmentNbt = enchantments.getCompound(i);

                // @Volatile: NBT tags from EnchantedBookItem
                if (ForgeRegistries.ENCHANTMENTS.getValue(new ResourceLocation(enchantmentNbt.getString("id"))) == enchant.enchantment &&
                    enchantmentNbt.getShort("lvl") == enchant.level) {
                    return true;
                }
            }
        }

        return false;
    }
}

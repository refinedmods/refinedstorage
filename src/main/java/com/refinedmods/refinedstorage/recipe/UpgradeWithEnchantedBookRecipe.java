package com.refinedmods.refinedstorage.recipe;

import com.refinedmods.refinedstorage.RSItems;
import com.refinedmods.refinedstorage.item.UpgradeItem;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.registries.ForgeRegistries;

public class UpgradeWithEnchantedBookRecipe extends ShapedRecipe {
    private final EnchantmentInstance enchant;
    private final ItemStack result;

    public UpgradeWithEnchantedBookRecipe(ResourceLocation recipeId, Enchantment enchantment, int enchantmentLevel, ItemStack result) {
        super(recipeId, "", 3, 3, NonNullList.of(Ingredient.EMPTY,
            Ingredient.of(new ItemStack(RSItems.QUARTZ_ENRICHED_IRON.get())),
            Ingredient.of(EnchantedBookItem.createForEnchantment(new EnchantmentInstance(enchantment, enchantmentLevel))),
            Ingredient.of(new ItemStack(RSItems.QUARTZ_ENRICHED_IRON.get())),
            Ingredient.of(new ItemStack(Blocks.BOOKSHELF)),
            Ingredient.of(new ItemStack(RSItems.UPGRADE_ITEMS.get(UpgradeItem.Type.NORMAL).get())),
            Ingredient.of(new ItemStack(Blocks.BOOKSHELF)),
            Ingredient.of(new ItemStack(RSItems.QUARTZ_ENRICHED_IRON.get())),
            Ingredient.of(new ItemStack(RSItems.QUARTZ_ENRICHED_IRON.get())),
            Ingredient.of(new ItemStack(RSItems.QUARTZ_ENRICHED_IRON.get()))
        ), result);

        this.enchant = new EnchantmentInstance(enchantment, enchantmentLevel);
        this.result = result;
    }

    public EnchantmentInstance getEnchant() {
        return enchant;
    }

    public ItemStack getResult() {
        return result;
    }

    @Override
    public boolean matches(CraftingContainer inv, Level world) {
        if (super.matches(inv, world)) {
            ListTag enchantments = EnchantedBookItem.getEnchantments(inv.getItem(1));

            for (int i = 0; i < enchantments.size(); ++i) {
                CompoundTag enchantmentNbt = enchantments.getCompound(i);

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

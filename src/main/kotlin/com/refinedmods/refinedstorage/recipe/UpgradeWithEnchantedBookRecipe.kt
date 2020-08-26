package com.refinedmods.refinedstorage.recipe

import com.refinedmods.refinedstorage.RSItems
import net.minecraft.block.Blocks
import net.minecraft.enchantment.Enchantment
import net.minecraft.enchantment.EnchantmentData
import net.minecraft.inventory.CraftingInventory
import net.minecraft.item.EnchantedBookItem
import net.minecraft.item.ItemStack
import net.minecraft.item.crafting.Ingredient
import net.minecraft.item.crafting.ShapedRecipe
import net.minecraft.nbt.ListTag
import net.minecraft.util.NonNullList
import net.minecraft.util.Identifier
import net.minecraft.world.World
import net.minecraftforge.registries.ForgeRegistries

class UpgradeWithEnchantedBookRecipe(recipeId: Identifier?, enchantment: Enchantment?, enchantmentLevel: Int, result: ItemStack) : ShapedRecipe(recipeId, "", 3, 3, NonNullList.from(Ingredient.EMPTY,
        Ingredient.fromStacks(ItemStack(RSItems.QUARTZ_ENRICHED_IRON)),
        Ingredient.fromStacks(EnchantedBookItem.getEnchantedItemStack(EnchantmentData(enchantment, enchantmentLevel))),
        Ingredient.fromStacks(ItemStack(RSItems.QUARTZ_ENRICHED_IRON)),
        Ingredient.fromStacks(ItemStack(Blocks.BOOKSHELF)),
        Ingredient.fromStacks(ItemStack(RSItems.UPGRADE)),
        Ingredient.fromStacks(ItemStack(Blocks.BOOKSHELF)),
        Ingredient.fromStacks(ItemStack(RSItems.QUARTZ_ENRICHED_IRON)),
        Ingredient.fromStacks(ItemStack(RSItems.QUARTZ_ENRICHED_IRON)),
        Ingredient.fromStacks(ItemStack(RSItems.QUARTZ_ENRICHED_IRON))
), result) {
    private val enchant: EnchantmentData
    val result: ItemStack
    fun getEnchant(): EnchantmentData {
        return enchant
    }

    fun matches(inv: CraftingInventory, world: World?): Boolean {
        if (super.matches(inv, world)) {
            val enchantments: ListTag = EnchantedBookItem.getEnchantments(inv.getStackInSlot(1))
            for (i in enchantments.indices) {
                val enchantmentNbt = enchantments.getCompound(i)

                // @Volatile: NBT tags from ItemEnchantedBook
                if (ForgeRegistries.ENCHANTMENTS.getValue(Identifier(enchantmentNbt.getString("id"))) === enchant.enchantment && enchantmentNbt.getShort("lvl") == enchant.enchantmentLevel) {
                    return true
                }
            }
        }
        return false
    }

    init {
        enchant = EnchantmentData(enchantment, enchantmentLevel)
        this.result = result
    }
}
package com.refinedmods.refinedstorage.recipe;

import com.refinedmods.refinedstorage.RSItems;
import com.refinedmods.refinedstorage.item.UpgradeItem;

import java.util.Objects;
import java.util.Optional;
import javax.annotation.Nullable;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.RegistryFixedCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.crafting.ShapedRecipePattern;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;

public class UpgradeWithEnchantedBookRecipe extends ShapedRecipe {
    public static final Codec<UpgradeWithEnchantedBookRecipe> CODEC = RecordCodecBuilder.create(
        instance -> instance.group(
            RegistryFixedCodec.create(Registries.ENCHANTMENT).fieldOf("enchantment")
                .forGetter(UpgradeWithEnchantedBookRecipe::getEnchantment),
            Codec.INT.fieldOf("level").orElse(1)
                .forGetter(UpgradeWithEnchantedBookRecipe::getEnchantmentLevel),
            RegistryFixedCodec.create(Registries.ITEM).fieldOf("result")
                .forGetter(UpgradeWithEnchantedBookRecipe::getResultItem)
        ).apply(instance, UpgradeWithEnchantedBookRecipe::new)
    );

    private final Holder<Enchantment> enchantment;
    private final int level;
    private final Holder<Item> resultItem;

    UpgradeWithEnchantedBookRecipe(final Holder<Enchantment> enchantment,
                                   final int level,
                                   final Holder<Item> resultItem) {
        super("", CraftingBookCategory.MISC, new ShapedRecipePattern(3, 3, NonNullList.of(
            Ingredient.EMPTY,
            Ingredient.of(new ItemStack(RSItems.QUARTZ_ENRICHED_IRON.get())),
            Ingredient.of(EnchantedBookItem.createForEnchantment(new EnchantmentInstance(enchantment.value(), level))),
            Ingredient.of(new ItemStack(RSItems.QUARTZ_ENRICHED_IRON.get())),
            Ingredient.of(new ItemStack(Blocks.BOOKSHELF)),
            Ingredient.of(new ItemStack(RSItems.UPGRADE_ITEMS.get(UpgradeItem.Type.NORMAL).get())),
            Ingredient.of(new ItemStack(Blocks.BOOKSHELF)),
            Ingredient.of(new ItemStack(RSItems.QUARTZ_ENRICHED_IRON.get())),
            Ingredient.of(new ItemStack(RSItems.QUARTZ_ENRICHED_IRON.get())),
            Ingredient.of(new ItemStack(RSItems.QUARTZ_ENRICHED_IRON.get()))
        ), Optional.empty()), new ItemStack(resultItem.value()));
        this.enchantment = enchantment;
        this.level = level;
        this.resultItem = resultItem;
    }

    public Holder<Item> getResultItem() {
        return resultItem;
    }

    public Holder<Enchantment> getEnchantment() {
        return enchantment;
    }

    @Nullable
    public ResourceLocation getEnchantmentId() {
        return enchantment.unwrapKey().map(ResourceKey::location).orElse(null);
    }

    public int getEnchantmentLevel() {
        return level;
    }

    @Override
    public boolean matches(final CraftingContainer craftingContainer, final Level theLevel) {
        if (!super.matches(craftingContainer, theLevel)) {
            return false;
        }
        final ListTag enchantments = EnchantedBookItem.getEnchantments(craftingContainer.getItem(1));
        for (int i = 0; i < enchantments.size(); ++i) {
            final CompoundTag tag = enchantments.getCompound(i);
            final int containerLevel = EnchantmentHelper.getEnchantmentLevel(tag);
            final ResourceLocation containerEnchantment = EnchantmentHelper.getEnchantmentId(tag);
            if (Objects.equals(containerEnchantment, getEnchantmentId()) && containerLevel == level) {
                return true;
            }
        }
        return false;
    }
}

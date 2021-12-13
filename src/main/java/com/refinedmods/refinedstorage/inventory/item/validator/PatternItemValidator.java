package com.refinedmods.refinedstorage.inventory.item.validator;

import com.refinedmods.refinedstorage.RSItems;
import com.refinedmods.refinedstorage.api.autocrafting.ICraftingPatternProvider;
import com.refinedmods.refinedstorage.item.PatternItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.function.Predicate;

public class PatternItemValidator implements Predicate<ItemStack> {
    private final Level level;

    public PatternItemValidator(Level level) {
        this.level = level;
    }

    @Override
    public boolean test(ItemStack stack) {
        if (stack.getItem() == RSItems.PATTERN.get()) {
            return PatternItem.fromCache(level, stack).isValid();
        }
        return stack.getItem() instanceof ICraftingPatternProvider && ((ICraftingPatternProvider) stack.getItem()).create(level, stack, null).isValid();
    }
}

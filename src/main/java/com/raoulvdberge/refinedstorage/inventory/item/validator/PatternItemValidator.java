package com.raoulvdberge.refinedstorage.inventory.item.validator;

import com.raoulvdberge.refinedstorage.api.autocrafting.ICraftingPatternProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import java.util.function.Predicate;

public class PatternItemValidator implements Predicate<ItemStack> {
    private final World world;

    public PatternItemValidator(World world) {
        this.world = world;
    }

    @Override
    public boolean test(ItemStack stack) {
        return stack.getItem() instanceof ICraftingPatternProvider && ((ICraftingPatternProvider) stack.getItem()).create(world, stack, null).isValid();
    }
}

package com.refinedmods.refinedstorage.item.property;

import com.refinedmods.refinedstorage.item.NetworkItem;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;

public class NetworkItemPropertyGetter implements IItemPropertyGetter {
    @Override
    public float call(ItemStack stack, @Nullable ClientWorld world, @Nullable LivingEntity entity) {
        return entity != null && NetworkItem.isValid(stack) ? 1.0f : 0.0f;
    }
}

package com.refinedmods.refinedstorage.item.property;

import com.refinedmods.refinedstorage.item.SecurityCardItem;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.ClampedItemPropertyFunction;
import net.minecraft.client.renderer.item.ItemPropertyFunction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;

public class SecurityCardItemPropertyGetter implements ClampedItemPropertyFunction {
    @Override
    public float unclampedCall(ItemStack stack, @Nullable ClientLevel level, @Nullable LivingEntity entity, int p) {
        return entity != null && SecurityCardItem.isValid(stack) ? 1.0f : 0.0f;
    }
}

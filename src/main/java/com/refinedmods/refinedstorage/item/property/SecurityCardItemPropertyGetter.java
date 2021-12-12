package com.refinedmods.refinedstorage.item.property;

import com.refinedmods.refinedstorage.item.SecurityCardItem;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.ItemPropertyFunction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;

public class SecurityCardItemPropertyGetter implements ItemPropertyFunction {
    @Override
    public float call(ItemStack stack, @Nullable ClientLevel world, @Nullable LivingEntity entity, int p) {
        return entity != null && SecurityCardItem.isValid(stack) ? 1.0f : 0.0f;
    }
}

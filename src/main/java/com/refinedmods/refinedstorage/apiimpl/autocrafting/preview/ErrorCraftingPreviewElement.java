package com.refinedmods.refinedstorage.apiimpl.autocrafting.preview;

import com.mojang.blaze3d.vertex.PoseStack;
import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.api.autocrafting.preview.ICraftingPreviewElement;
import com.refinedmods.refinedstorage.api.autocrafting.task.CalculationResultType;
import com.refinedmods.refinedstorage.api.render.IElementDrawers;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class ErrorCraftingPreviewElement implements ICraftingPreviewElement {
    public static final ResourceLocation ID = new ResourceLocation(RS.ID, "error");

    private final CalculationResultType type;
    private final ItemStack recursedPattern;

    public ErrorCraftingPreviewElement(CalculationResultType type, ItemStack recursedPattern) {
        this.type = type;
        this.recursedPattern = recursedPattern;
    }

    public static ErrorCraftingPreviewElement read(FriendlyByteBuf buf) {
        int errorIdx = buf.readInt();
        CalculationResultType error = errorIdx >= 0 && errorIdx < CalculationResultType.values().length ? CalculationResultType.values()[errorIdx] : CalculationResultType.TOO_COMPLEX;
        ItemStack stack = buf.readItem();

        return new ErrorCraftingPreviewElement(error, stack);
    }

    public ItemStack getRecursedPattern() {
        return recursedPattern;
    }

    @Override
    public void draw(PoseStack poseStack, int x, int y, IElementDrawers drawers) {
        // NO OP
    }

    @Override
    public boolean doesDisableTaskStarting() {
        return true;
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeInt(type.ordinal());
        buf.writeItem(recursedPattern);
    }

    public CalculationResultType getType() {
        return type;
    }

    @Override
    public ResourceLocation getId() {
        return ID;
    }
}

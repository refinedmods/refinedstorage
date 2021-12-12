package com.refinedmods.refinedstorage.apiimpl.autocrafting.preview;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.api.autocrafting.preview.ICraftingPreviewElement;
import com.refinedmods.refinedstorage.api.autocrafting.task.CalculationResultType;
import com.refinedmods.refinedstorage.api.render.IElementDrawers;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;

public class ErrorCraftingPreviewElement implements ICraftingPreviewElement {
    public static final ResourceLocation ID = new ResourceLocation(RS.ID, "error");

    private final CalculationResultType type;
    private final ItemStack recursedPattern;

    public ErrorCraftingPreviewElement(CalculationResultType type, ItemStack recursedPattern) {
        this.type = type;
        this.recursedPattern = recursedPattern;
    }

    public ItemStack getRecursedPattern() {
        return recursedPattern;
    }

    @Override
    public void draw(MatrixStack matrixStack, int x, int y, IElementDrawers drawers) {
        // NO OP
    }

    @Override
    public boolean doesDisableTaskStarting() {
        return true;
    }

    @Override
    public void write(PacketBuffer buf) {
        buf.writeInt(type.ordinal());
        buf.writeItem(recursedPattern);
    }

    public CalculationResultType getType() {
        return type;
    }

    public static ErrorCraftingPreviewElement read(PacketBuffer buf) {
        int errorIdx = buf.readInt();
        CalculationResultType error = errorIdx >= 0 && errorIdx < CalculationResultType.values().length ? CalculationResultType.values()[errorIdx] : CalculationResultType.TOO_COMPLEX;
        ItemStack stack = buf.readItem();

        return new ErrorCraftingPreviewElement(error, stack);
    }

    @Override
    public ResourceLocation getId() {
        return ID;
    }
}

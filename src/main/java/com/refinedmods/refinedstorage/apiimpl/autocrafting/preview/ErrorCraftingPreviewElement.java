package com.refinedmods.refinedstorage.apiimpl.autocrafting.preview;

import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.api.autocrafting.preview.ICraftingPreviewElement;
import com.refinedmods.refinedstorage.api.autocrafting.task.CraftingTaskErrorType;
import com.refinedmods.refinedstorage.api.render.IElementDrawers;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;

public class ErrorCraftingPreviewElement implements ICraftingPreviewElement<ItemStack> {
    public static final ResourceLocation ID = new ResourceLocation(RS.ID, "error");

    private CraftingTaskErrorType type;
    private ItemStack stack;

    public ErrorCraftingPreviewElement(CraftingTaskErrorType type, ItemStack stack) {
        this.type = type;
        this.stack = stack;
    }

    @Override
    public ItemStack getElement() {
        return stack;
    }

    @Override
    public void draw(int x, int y, IElementDrawers drawers) {
        // NO OP
    }

    @Override
    public int getAvailable() {
        return 0;
    }

    @Override
    public int getToCraft() {
        return 0;
    }

    @Override
    public boolean hasMissing() {
        return false;
    }

    @Override
    public void write(PacketBuffer buf) {
        buf.writeInt(type.ordinal());
        buf.writeItemStack(stack);
    }

    public CraftingTaskErrorType getType() {
        return type;
    }

    public static ErrorCraftingPreviewElement read(PacketBuffer buf) {
        int errorIdx = buf.readInt();
        CraftingTaskErrorType error = errorIdx >= 0 && errorIdx < CraftingTaskErrorType.values().length ? CraftingTaskErrorType.values()[errorIdx] : CraftingTaskErrorType.TOO_COMPLEX;
        ItemStack stack = buf.readItemStack();

        return new ErrorCraftingPreviewElement(error, stack);
    }

    @Override
    public ResourceLocation getId() {
        return ID;
    }
}

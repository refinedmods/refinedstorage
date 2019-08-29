package com.raoulvdberge.refinedstorage.apiimpl.autocrafting.preview;

import com.raoulvdberge.refinedstorage.api.autocrafting.preview.ICraftingPreviewElement;
import com.raoulvdberge.refinedstorage.api.autocrafting.task.CraftingTaskErrorType;
import com.raoulvdberge.refinedstorage.api.render.IElementDrawers;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;

public class CraftingPreviewElementError implements ICraftingPreviewElement<ItemStack> {
    public static final String ID = "error";

    private CraftingTaskErrorType type;
    private ItemStack stack;

    public CraftingPreviewElementError(CraftingTaskErrorType type, ItemStack stack) {
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

    // TODO: Rename to writeToBuffer.
    @Override
    public void writeToByteBuf(PacketBuffer buf) {
        buf.writeInt(type.ordinal());
        // TODO can't we use writeItemStack here?
        buf.writeInt(Item.getIdFromItem(stack.getItem()));
        buf.writeCompoundTag(stack.getTag());
    }

    public CraftingTaskErrorType getType() {
        return type;
    }

    // TODO: Rename to fromBuffer
    public static CraftingPreviewElementError fromByteBuf(PacketBuffer buf) {
        int errorIdx = buf.readInt();
        CraftingTaskErrorType error = errorIdx >= 0 && errorIdx < CraftingTaskErrorType.values().length ? CraftingTaskErrorType.values()[errorIdx] : CraftingTaskErrorType.TOO_COMPLEX;

        // TODO can't we use readItemStack here?
        Item item = Item.getItemById(buf.readInt());
        CompoundNBT tag = buf.readCompoundTag();

        ItemStack stack = new ItemStack(item, 1);
        stack.put(tag);

        return new CraftingPreviewElementError(error, stack);
    }

    @Override
    public String getId() {
        return ID;
    }
}

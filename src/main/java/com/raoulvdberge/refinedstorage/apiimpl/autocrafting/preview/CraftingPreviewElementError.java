package com.raoulvdberge.refinedstorage.apiimpl.autocrafting.preview;

import com.raoulvdberge.refinedstorage.api.autocrafting.preview.ICraftingPreviewElement;
import com.raoulvdberge.refinedstorage.api.autocrafting.task.CraftingTaskErrorType;
import com.raoulvdberge.refinedstorage.api.render.IElementDrawers;
import io.netty.buffer.ByteBuf;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;

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

    @Override
    public void writeToByteBuf(ByteBuf buf) {
        buf.writeInt(type.ordinal());
        buf.writeInt(Item.getIdFromItem(stack.getItem()));
        buf.writeInt(stack.getMetadata());
        ByteBufUtils.writeTag(buf, stack.getTagCompound());
    }

    public CraftingTaskErrorType getType() {
        return type;
    }

    public static CraftingPreviewElementError fromByteBuf(ByteBuf buf) {
        int errorIdx = buf.readInt();
        CraftingTaskErrorType error = errorIdx >= 0 && errorIdx < CraftingTaskErrorType.values().length ? CraftingTaskErrorType.values()[errorIdx] : CraftingTaskErrorType.TOO_COMPLEX;

        Item item = Item.getItemById(buf.readInt());
        int meta = buf.readInt();
        NBTTagCompound tag = ByteBufUtils.readTag(buf);

        ItemStack stack = new ItemStack(item, 1, meta);
        stack.setTagCompound(tag);

        return new CraftingPreviewElementError(error, stack);
    }

    @Override
    public String getId() {
        return ID;
    }
}

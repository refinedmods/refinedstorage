package com.raoulvdberge.refinedstorage.apiimpl.autocrafting.preview;

import com.mojang.blaze3d.platform.GlStateManager;
import com.raoulvdberge.refinedstorage.api.autocrafting.preview.ICraftingPreviewElement;
import com.raoulvdberge.refinedstorage.api.render.IElementDrawers;
import com.raoulvdberge.refinedstorage.util.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.items.ItemHandlerHelper;

public class CraftingPreviewElementItemStack implements ICraftingPreviewElement<ItemStack> {
    public static final String ID = "item_renderer";

    private ItemStack stack;
    private int available;
    private boolean missing;
    // If missing is true then toCraft is the missing amount
    private int toCraft;

    public CraftingPreviewElementItemStack(ItemStack stack) {
        this.stack = ItemHandlerHelper.copyStackWithSize(stack, 1);
    }

    public CraftingPreviewElementItemStack(ItemStack stack, int available, boolean missing, int toCraft) {
        this.stack = stack;
        this.available = available;
        this.missing = missing;
        this.toCraft = toCraft;
    }

    // TODO ren
    @Override
    public void writeToByteBuf(PacketBuffer buf) {
        // TODO can't we use writeItemSTack
        buf.writeInt(Item.getIdFromItem(stack.getItem()));
        buf.writeCompoundTag(stack.getTag());
        buf.writeInt(available);
        buf.writeBoolean(missing);
        buf.writeInt(toCraft);
    }

    // TODO ren
    public static CraftingPreviewElementItemStack fromByteBuf(PacketBuffer buf) {
        // TODO readItemStack

        Item item = Item.getItemById(buf.readInt());
        CompoundNBT tag = buf.readCompoundTag();
        int available = buf.readInt();
        boolean missing = buf.readBoolean();
        int toCraft = buf.readInt();

        ItemStack stack = new ItemStack(item, 1);
        stack.setTag(tag);

        return new CraftingPreviewElementItemStack(stack, available, missing, toCraft);
    }

    @Override
    public ItemStack getElement() {
        return stack;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void draw(int x, int y, IElementDrawers drawers) {
        if (missing) {
            drawers.getOverlayDrawer().draw(x, y, 0xFFF2DEDE);
        }

        x += 5;
        y += 7;

        drawers.getItemDrawer().draw(x, y, getElement());

        float scale = Minecraft.getInstance().getForceUnicodeFont() ? 1F : 0.5F;

        y += 2;

        GlStateManager.pushMatrix();
        GlStateManager.scalef(scale, scale, 1);

        if (getToCraft() > 0) {
            String format = hasMissing() ? "gui.refinedstorage:crafting_preview.missing" : "gui.refinedstorage:crafting_preview.to_craft";
            drawers.getStringDrawer().draw(RenderUtils.getOffsetOnScale(x + 23, scale), RenderUtils.getOffsetOnScale(y, scale), I18n.format(format, getToCraft()));

            y += 7;
        }

        if (getAvailable() > 0) {
            drawers.getStringDrawer().draw(RenderUtils.getOffsetOnScale(x + 23, scale), RenderUtils.getOffsetOnScale(y, scale), I18n.format("gui.refinedstorage:crafting_preview.available", getAvailable()));
        }

        GlStateManager.popMatrix();
    }

    public void addAvailable(int amount) {
        this.available += amount;
    }

    @Override
    public int getAvailable() {
        return available;
    }

    public void addToCraft(int amount) {
        this.toCraft += amount;
    }

    @Override
    public int getToCraft() {
        return this.toCraft;
    }

    public void setMissing(boolean missing) {
        this.missing = missing;
    }

    @Override
    public boolean hasMissing() {
        return missing;
    }

    @Override
    public String getId() {
        return ID;
    }
}

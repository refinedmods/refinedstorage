package refinedstorage.apiimpl.autocrafting.preview;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.ItemHandlerHelper;
import refinedstorage.api.autocrafting.preview.ICraftingPreviewElement;
import refinedstorage.api.render.IElementDrawer;
import refinedstorage.gui.GuiBase;

public class CraftingPreviewElementItemStack implements ICraftingPreviewElement<ItemStack> {
    public static final String ID = "item_renderer";

    private ItemStack stack;
    private int available;
    private boolean missing;
    private int toCraft;
    // if missing is true then toCraft is the missing amount

    public CraftingPreviewElementItemStack(ItemStack stack) {
        this.stack = ItemHandlerHelper.copyStackWithSize(stack, 1);
    }

    public CraftingPreviewElementItemStack(ItemStack stack, int available, boolean missing, int toCraft) {
        this.stack = stack;
        this.available = available;
        this.missing = missing;
        this.toCraft = toCraft;
    }

    @Override
    public void writeToByteBuf(ByteBuf buf) {
        buf.writeInt(Item.getIdFromItem(stack.getItem()));
        buf.writeInt(stack.getMetadata());
        ByteBufUtils.writeTag(buf, stack.getTagCompound());
        buf.writeInt(available);
        buf.writeBoolean(missing);
        buf.writeInt(toCraft);
    }

    public static CraftingPreviewElementItemStack fromByteBuf(ByteBuf buf) {
        Item item = Item.getItemById(buf.readInt());
        int meta = buf.readInt();
        NBTTagCompound tag = ByteBufUtils.readTag(buf);
        int available = buf.readInt();
        boolean missing = buf.readBoolean();
        int toCraft = buf.readInt();

        ItemStack stack = new ItemStack(item, 1, meta);
        stack.setTagCompound(tag);
        return new CraftingPreviewElementItemStack(stack, available, missing, toCraft);
    }

    @Override
    public ItemStack getElement() {
        return stack;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void draw(int x, int y, IElementDrawer<ItemStack> itemDrawer, IElementDrawer<FluidStack> fluidDrawer, IElementDrawer<String> stringDrawer) {
        itemDrawer.draw(x, y, getElement());

        float scale = 0.5f;
        y += 2;

        GlStateManager.pushMatrix();
        GlStateManager.scale(scale, scale, 1);

        if (getToCraft() > 0) {
            String format = hasMissing() ? "gui.refinedstorage:crafting_preview.missing" : "gui.refinedstorage:crafting_preview.to_craft";
            stringDrawer.draw(GuiBase.calculateOffsetOnScale(x + 23, scale), GuiBase.calculateOffsetOnScale(y, scale), GuiBase.t(format, getToCraft()));

            y += 7;
        }

        if (getAvailable() > 0) {
            stringDrawer.draw(GuiBase.calculateOffsetOnScale(x + 23, scale), GuiBase.calculateOffsetOnScale(y, scale), GuiBase.t("gui.refinedstorage:crafting_preview.available", getAvailable()));
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

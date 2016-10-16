package refinedstorage.apiimpl.autocrafting.preview;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import refinedstorage.api.autocrafting.preview.ICraftingPreviewElement;
import refinedstorage.api.render.ElementDrawer;
import refinedstorage.gui.GuiBase;

public class CraftingPreviewElementFluidStack implements ICraftingPreviewElement<FluidStack> {
    public static final String ID = "fluid_renderer";

    private FluidStack stack;
    private int available;
    private boolean missing;
    private int toCraft;
    // if missing is true then toCraft is the missing amount

    public CraftingPreviewElementFluidStack(FluidStack stack) {
        this.stack = stack.copy();
        this.available = stack.amount;
    }

    public CraftingPreviewElementFluidStack(FluidStack stack, int available, boolean missing, int toCraft) {
        this.stack = stack.copy();
        this.available = available;
        this.missing = missing;
        this.toCraft = toCraft;
    }

    @Override
    public void writeToByteBuf(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, FluidRegistry.getFluidName(stack));
        ByteBufUtils.writeTag(buf, stack.tag);
        buf.writeInt(available);
        buf.writeBoolean(missing);
        buf.writeInt(toCraft);
    }

    public static CraftingPreviewElementFluidStack fromByteBuf(ByteBuf buf) {
        Fluid fluid = FluidRegistry.getFluid(ByteBufUtils.readUTF8String(buf));
        NBTTagCompound tag = ByteBufUtils.readTag(buf);
        int available = buf.readInt();
        boolean missing = buf.readBoolean();
        int toCraft = buf.readInt();

        return new CraftingPreviewElementFluidStack(new FluidStack(fluid, 1, tag), available, missing, toCraft);
    }

    @Override
    public FluidStack getElement() {
        return stack;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void draw(int x, int y, ElementDrawer<FluidStack> elementDrawer, ElementDrawer<String> stringDrawer) {
        elementDrawer.draw(x, y, getElement());

        float scale = 0.5f;

        GlStateManager.pushMatrix();
        GlStateManager.scale(scale, scale, 1);

        stringDrawer.draw(GuiBase.calculateOffsetOnScale(x + 23, scale), GuiBase.calculateOffsetOnScale(y + 3, scale), GuiBase.t("gui.refinedstorage:crafting_preview.available", ""));
        stringDrawer.draw(GuiBase.calculateOffsetOnScale(x + 23, scale), GuiBase.calculateOffsetOnScale(y + 9, scale), getAvailable() + " mB");

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

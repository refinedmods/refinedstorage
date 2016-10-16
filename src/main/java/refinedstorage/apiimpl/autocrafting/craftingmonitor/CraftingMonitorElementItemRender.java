package refinedstorage.apiimpl.autocrafting.craftingmonitor;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import refinedstorage.api.autocrafting.craftingmonitor.ICraftingMonitorElement;
import refinedstorage.api.render.IElementDrawer;
import refinedstorage.gui.GuiBase;

public class CraftingMonitorElementItemRender implements ICraftingMonitorElement<ItemStack> {
    public static final String ID = "item_render";

    private int taskId;
    private ItemStack stack;
    private int quantity;
    private int offset;

    public CraftingMonitorElementItemRender(int taskId, ItemStack stack, int quantity, int offset) {
        this.taskId = taskId;
        this.stack = stack;
        this.quantity = quantity;
        this.offset = offset;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void draw(int x, int y, IElementDrawer<ItemStack> itemDrawer, IElementDrawer<FluidStack> fluidDrawer, IElementDrawer<String> stringDrawer) {
        itemDrawer.draw(x + 2 + offset, y + 1, stack);

        float scale = 0.5f;

        GlStateManager.pushMatrix();
        GlStateManager.scale(scale, scale, 1);

        stringDrawer.draw(GuiBase.calculateOffsetOnScale(x + 21 + offset, scale), GuiBase.calculateOffsetOnScale(y + 7, scale), quantity + " " + stack.getDisplayName());

        GlStateManager.popMatrix();
    }

    @Override
    public int getTaskId() {
        return taskId;
    }

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public void write(ByteBuf buf) {
        buf.writeInt(taskId);
        ByteBufUtils.writeItemStack(buf, stack);
        buf.writeInt(quantity);
        buf.writeInt(offset);
    }
}

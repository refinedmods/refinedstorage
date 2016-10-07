package refinedstorage.apiimpl.autocrafting.craftingmonitor;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import refinedstorage.api.autocrafting.craftingmonitor.ICraftingMonitorElement;
import refinedstorage.gui.GuiBase;

public abstract class CraftingMonitorElementItemRender implements ICraftingMonitorElement<GuiBase> {
    @Override
    public void draw(GuiBase gui, int x, int y) {
        gui.drawItem(x + 2, y + 1, getItem());

        float scale = 0.5f;

        GlStateManager.pushMatrix();
        GlStateManager.scale(scale, scale, 1);

        gui.drawString(gui.calculateOffsetOnScale(x + 21, scale), gui.calculateOffsetOnScale(y + 7, scale), getQuantity() + " " + getItem().getDisplayName());

        GlStateManager.popMatrix();
    }

    protected abstract ItemStack getItem();

    protected abstract int getQuantity();
}

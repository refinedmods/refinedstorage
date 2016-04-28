package refinedstorage.gui;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;
import refinedstorage.container.ContainerCrafter;
import refinedstorage.gui.sidebutton.SideButtonRedstoneMode;
import refinedstorage.item.ItemPattern;
import refinedstorage.tile.TileCrafter;

public class GuiCrafter extends GuiBase {
    private TileCrafter crafter;

    public GuiCrafter(ContainerCrafter container, TileCrafter crafter) {
        super(container, 211, 173);

        this.crafter = crafter;
    }

    @Override
    public void init(int x, int y) {
        addSideButton(new SideButtonRedstoneMode(crafter));
    }

    @Override
    public void update(int x, int y) {
    }

    @Override
    public void drawBackground(int x, int y, int mouseX, int mouseY) {
        bindTexture("gui/crafter.png");

        drawTexture(x, y, 0, 0, width, height);
    }

    private int calculateOffsetOnScale(int pos, float scale) {
        float multiplier = (pos / scale);
        return (int) multiplier;
    }

    @Override
    public void drawForeground(int mouseX, int mouseY) {
        drawString(7, 7, t("gui.refinedstorage:crafter"));
        drawString(7, 77, t("container.inventory"));

        RenderHelper.enableGUIStandardItemLighting();

        for (int i = 0; i < 6; ++i) {
            int x = i >= 3 ? 109 : 27;
            int y = 19 + ((i - (i >= 3 ? 3 : 0)) * 18);

            if (crafter.getStackInSlot(i) != null) {
                ItemStack result = ItemPattern.getResult(crafter.getStackInSlot(i));

                drawItem(x, y, result);

                GlStateManager.pushMatrix();

                float scale = 0.5f;
                GlStateManager.scale(scale, scale, 1);
                drawString(calculateOffsetOnScale(x + 20, scale), calculateOffsetOnScale(y + 6, scale), result.getDisplayName());

                GlStateManager.popMatrix();
            }
        }
    }
}

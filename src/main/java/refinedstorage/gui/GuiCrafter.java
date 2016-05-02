package refinedstorage.gui;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;
import refinedstorage.container.ContainerCrafter;
import refinedstorage.gui.sidebutton.SideButtonRedstoneMode;
import refinedstorage.item.ItemPattern;
import refinedstorage.tile.autocrafting.TileCrafter;

public class GuiCrafter extends GuiBase {
    private TileCrafter crafter;

    public GuiCrafter(ContainerCrafter container, TileCrafter crafter) {
        super(container, 211, 226);

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
        drawString(7, 131, t("container.inventory"));

        RenderHelper.enableGUIStandardItemLighting();

        for (int i = 0; i < TileCrafter.PATTERN_SLOTS; ++i) {
            int x = 27;
            int y = 19 + (i * 18);

            if (crafter.getStackInSlot(i) != null) {
                ItemStack pattern = crafter.getStackInSlot(i);

                String text = "Processing";

                if (!ItemPattern.isProcessing(pattern)) {
                    ItemStack result = ItemPattern.getOutputs(pattern)[0];

                    drawItem(x, y, result);

                    text = result.getDisplayName();
                }

                GlStateManager.pushMatrix();

                float scale = 0.5f;
                GlStateManager.scale(scale, scale, 1);
                drawString(calculateOffsetOnScale(x + (ItemPattern.isProcessing(pattern) ? 0 : 20), scale), calculateOffsetOnScale(y + 6, scale), text);

                GlStateManager.popMatrix();
            }
        }
    }
}

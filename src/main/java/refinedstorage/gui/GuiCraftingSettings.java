package refinedstorage.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;

public class GuiCraftingSettings extends GuiBase {
    public GuiCraftingSettings() {
        super(new Container() {
            @Override
            public boolean canInteractWith(EntityPlayer player) {
                return false;
            }
        }, 143, 61);
    }

    @Override
    public void init(int x, int y) {
        addButton(x + 56, y + 38, 50, 50, "Craft");
    }

    @Override
    public void update(int x, int y) {
    }

    @Override
    public void drawBackground(int x, int y, int mouseX, int mouseY) {
        bindTexture("gui/crafting_settings.png");

        drawTexture(x, y, 0, 0, width, height);
    }

    @Override
    public void drawForeground(int mouseX, int mouseY) {
        drawString(55, 7, "Crafting");
    }
}

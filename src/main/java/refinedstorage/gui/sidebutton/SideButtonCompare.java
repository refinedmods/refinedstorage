package refinedstorage.gui.sidebutton;

import net.minecraft.client.Minecraft;
import net.minecraft.util.text.TextFormatting;
import refinedstorage.api.storage.CompareUtils;
import refinedstorage.gui.GuiBase;
import refinedstorage.tile.data.TileDataManager;
import refinedstorage.tile.data.TileDataParameter;

public class SideButtonCompare extends SideButton {
    private TileDataParameter<Integer> parameter;
    private int mask;

    public SideButtonCompare(GuiBase gui, TileDataParameter<Integer> parameter, int mask) {
        super(gui);

        this.parameter = parameter;
        this.mask = mask;
    }

    @Override
    public String getTooltip(GuiBase gui) {
        String tooltip = TextFormatting.YELLOW + gui.t("sidebutton.refinedstorage:compare." + mask) + TextFormatting.RESET + "\n";

        if ((parameter.getValue() & mask) == mask) {
            tooltip += gui.t("gui.yes");
        } else {
            tooltip += gui.t("gui.no");
        }

        return tooltip;
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY) {
        super.drawButton(mc, mouseX, mouseY);

        int ty = 0;

        if (mask == CompareUtils.COMPARE_DAMAGE) {
            ty = 80;
        } else if (mask == CompareUtils.COMPARE_NBT) {
            ty = 48;
        }

        int tx = (parameter.getValue() & mask) == mask ? 0 : 16;

        gui.drawTexture(xPosition, yPosition, tx, ty, 16, 16);
    }

    @Override
    public void actionPerformed() {
        TileDataManager.setParameter(parameter, parameter.getValue() ^ mask);
    }
}

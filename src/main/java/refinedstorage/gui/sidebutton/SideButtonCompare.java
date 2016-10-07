package refinedstorage.gui.sidebutton;

import net.minecraft.util.text.TextFormatting;
import refinedstorage.api.util.IComparer;
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
    public String getTooltip() {
        String tooltip = TextFormatting.YELLOW + gui.t("sidebutton.refinedstorage:compare." + mask) + TextFormatting.RESET + "\n";

        if ((parameter.getValue() & mask) == mask) {
            tooltip += gui.t("gui.yes");
        } else {
            tooltip += gui.t("gui.no");
        }

        return tooltip;
    }

    @Override
    protected void drawButtonIcon(int x, int y) {
        int ty = 0;

        if (mask == IComparer.COMPARE_DAMAGE) {
            ty = 80;
        } else if (mask == IComparer.COMPARE_NBT) {
            ty = 48;
        }

        int tx = (parameter.getValue() & mask) == mask ? 0 : 16;

        gui.drawTexture(x, y, tx, ty, 16, 16);
    }

    @Override
    public void actionPerformed() {
        TileDataManager.setParameter(parameter, parameter.getValue() ^ mask);
    }
}

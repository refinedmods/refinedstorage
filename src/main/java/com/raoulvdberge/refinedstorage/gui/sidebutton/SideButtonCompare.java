package com.raoulvdberge.refinedstorage.gui.sidebutton;

import com.raoulvdberge.refinedstorage.api.util.IComparer;
import com.raoulvdberge.refinedstorage.gui.GuiBase;
import com.raoulvdberge.refinedstorage.tile.data.TileDataManager;
import com.raoulvdberge.refinedstorage.tile.data.TileDataParameter;
import net.minecraft.util.text.TextFormatting;

public class SideButtonCompare extends SideButton {
    private TileDataParameter<Integer, ?> parameter;
    private int mask;

    public SideButtonCompare(GuiBase gui, TileDataParameter<Integer, ?> parameter, int mask) {
        super(gui);

        this.parameter = parameter;
        this.mask = mask;
    }

    @Override
    public String getTooltip() {
        String tooltip = GuiBase.t("sidebutton.refinedstorage:compare." + mask) + "\n" + TextFormatting.GRAY;

        if ((parameter.getValue() & mask) == mask) {
            tooltip += GuiBase.t("gui.yes");
        } else {
            tooltip += GuiBase.t("gui.no");
        }

        return tooltip;
    }

    @Override
    protected void drawButtonIcon(int x, int y) {
        int ty = 0;
		boolean forestry = ((mask & IComparer.COMPARE_FORESTRY) == IComparer.COMPARE_FORESTRY); // Unmask this particular flag;

        if (mask == IComparer.COMPARE_DAMAGE) {
            ty = 80;
        } else if (mask == IComparer.COMPARE_NBT) {
            ty = 48;
        } else if (mask == IComparer.COMPARE_OREDICT) {
            ty = 224;
        } else if (forestry) {
			 ty = 48;
        }

        int tx = (parameter.getValue() & mask) == mask ? 0 : 16;

        // This is reversed in icons.png :D
        if (mask == IComparer.COMPARE_OREDICT) {
            tx = tx == 16 ? 0 : 16;
        } else if (forestry) {
			tx = tx == 16 ? 32 : 48;
        }

        gui.drawTexture(x, y, tx, ty, 16, 16);
    }

    @Override
    public void actionPerformed() {
        TileDataManager.setParameter(parameter, parameter.getValue() ^ mask);
    }
}

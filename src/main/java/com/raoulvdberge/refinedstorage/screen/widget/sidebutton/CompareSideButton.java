package com.raoulvdberge.refinedstorage.screen.widget.sidebutton;

import com.raoulvdberge.refinedstorage.api.util.IComparer;
import com.raoulvdberge.refinedstorage.screen.BaseScreen;
import com.raoulvdberge.refinedstorage.tile.data.TileDataManager;
import com.raoulvdberge.refinedstorage.tile.data.TileDataParameter;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TextFormatting;

public class CompareSideButton extends SideButton {
    private TileDataParameter<Integer, ?> parameter;
    private int mask;

    public CompareSideButton(BaseScreen screen, TileDataParameter<Integer, ?> parameter, int mask) {
        super(screen);

        this.parameter = parameter;
        this.mask = mask;
    }

    @Override
    public String getTooltip() {
        String tooltip = I18n.format("sidebutton.refinedstorage.compare." + mask) + "\n" + TextFormatting.GRAY;

        if ((parameter.getValue() & mask) == mask) {
            tooltip += I18n.format("gui.yes");
        } else {
            tooltip += I18n.format("gui.no");
        }

        return tooltip;
    }

    @Override
    protected void renderButtonIcon(int x, int y) {
        int ty = 0;

        if (mask == IComparer.COMPARE_NBT) {
            ty = 48;
        }

        int tx = (parameter.getValue() & mask) == mask ? 0 : 16;

        screen.blit(x, y, tx, ty, 16, 16);
    }

    @Override
    public void onPress() {
        TileDataManager.setParameter(parameter, parameter.getValue() ^ mask);
    }
}

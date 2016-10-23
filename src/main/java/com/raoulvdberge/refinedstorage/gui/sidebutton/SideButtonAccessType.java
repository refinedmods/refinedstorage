package com.raoulvdberge.refinedstorage.gui.sidebutton;

import com.raoulvdberge.refinedstorage.RSUtils;
import com.raoulvdberge.refinedstorage.api.storage.AccessType;
import com.raoulvdberge.refinedstorage.gui.GuiBase;
import com.raoulvdberge.refinedstorage.tile.data.TileDataManager;
import com.raoulvdberge.refinedstorage.tile.data.TileDataParameter;
import net.minecraft.util.text.TextFormatting;

public class SideButtonAccessType extends SideButton {
    private TileDataParameter<AccessType> parameter;

    public SideButtonAccessType(GuiBase gui, TileDataParameter<AccessType> parameter) {
        super(gui);

        this.parameter = parameter;
    }

    @Override
    protected void drawButtonIcon(int x, int y) {
    }

    @Override
    public String getTooltip() {
        return TextFormatting.RED + GuiBase.t("sidebutton.refinedstorage:access_type") + TextFormatting.RESET + "\n" + GuiBase.t("sidebutton.refinedstorage:access_type." + parameter.getValue().getId());
    }

    @Override
    public void actionPerformed() {
        TileDataManager.setParameter(parameter, RSUtils.getAccessType(parameter.getValue().getId() + 1));
    }
}

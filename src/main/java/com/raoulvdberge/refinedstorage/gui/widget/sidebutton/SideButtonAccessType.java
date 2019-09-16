package com.raoulvdberge.refinedstorage.gui.widget.sidebutton;

import com.raoulvdberge.refinedstorage.api.storage.AccessType;
import com.raoulvdberge.refinedstorage.gui.GuiBase;
import com.raoulvdberge.refinedstorage.tile.data.TileDataManager;
import com.raoulvdberge.refinedstorage.tile.data.TileDataParameter;
import com.raoulvdberge.refinedstorage.util.AccessTypeUtils;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TextFormatting;

public class SideButtonAccessType extends SideButton {
    private TileDataParameter<AccessType, ?> parameter;

    public SideButtonAccessType(GuiBase gui, TileDataParameter<AccessType, ?> parameter) {
        super(gui);

        this.parameter = parameter;
    }

    @Override
    protected void drawButtonIcon(int x, int y) {
        gui.blit(x, y, 16 * parameter.getValue().getId(), 240, 16, 16);
    }

    @Override
    public String getTooltip() {
        return I18n.format("sidebutton.refinedstorage:access_type") + "\n" + TextFormatting.GRAY + I18n.format("sidebutton.refinedstorage:access_type." + parameter.getValue().getId());
    }

    @Override
    public void onPress() {
        TileDataManager.setParameter(parameter, AccessTypeUtils.getAccessType(parameter.getValue().getId() + 1));
    }
}

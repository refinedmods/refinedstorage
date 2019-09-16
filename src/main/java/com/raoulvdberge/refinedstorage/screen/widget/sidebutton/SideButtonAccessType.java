package com.raoulvdberge.refinedstorage.screen.widget.sidebutton;

import com.raoulvdberge.refinedstorage.api.storage.AccessType;
import com.raoulvdberge.refinedstorage.screen.BaseScreen;
import com.raoulvdberge.refinedstorage.tile.data.TileDataManager;
import com.raoulvdberge.refinedstorage.tile.data.TileDataParameter;
import com.raoulvdberge.refinedstorage.util.AccessTypeUtils;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TextFormatting;

public class SideButtonAccessType extends SideButton {
    private TileDataParameter<AccessType, ?> parameter;

    public SideButtonAccessType(BaseScreen gui, TileDataParameter<AccessType, ?> parameter) {
        super(gui);

        this.parameter = parameter;
    }

    @Override
    protected void renderButtonIcon(int x, int y) {
        screen.blit(x, y, 16 * parameter.getValue().getId(), 240, 16, 16);
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

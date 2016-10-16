package refinedstorage.gui.sidebutton;

import net.minecraft.util.text.TextFormatting;
import refinedstorage.RSUtils;
import refinedstorage.api.storage.AccessType;
import refinedstorage.gui.GuiBase;
import refinedstorage.tile.data.TileDataManager;
import refinedstorage.tile.data.TileDataParameter;

public class SideButtonAccessType extends SideButton {
    private TileDataParameter<AccessType> parameter;

    public SideButtonAccessType(GuiBase gui, TileDataParameter<AccessType> parameter) {
        super(gui);

        this.parameter = parameter;
    }

    @Override
    protected void drawButtonIcon(int x, int y) {
        // @TODO
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

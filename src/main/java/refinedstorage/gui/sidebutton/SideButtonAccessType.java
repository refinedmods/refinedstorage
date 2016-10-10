package refinedstorage.gui.sidebutton;

import refinedstorage.gui.GuiBase;
import refinedstorage.tile.config.IAccessType;
import refinedstorage.tile.data.TileDataManager;
import refinedstorage.tile.data.TileDataParameter;

public class SideButtonAccessType extends SideButton {
    private TileDataParameter<Integer> parameter;

    public SideButtonAccessType(GuiBase gui, TileDataParameter<Integer> parameter) {
        super(gui);

        this.parameter = parameter;
    }

    protected void drawButtonIcon(int x, int y) {
        //TODO
    }

    public String getTooltip() {
        switch (parameter.getValue()) {
            case IAccessType.READ:
                return gui.t("sidebutton.refinedstorage:access_type.read");
            case IAccessType.WRITE:
                return gui.t("sidebutton.refinedstorage:access_type.write");
            default:
            case IAccessType.READ_WRITE:
                return gui.t("sidebutton.refinedstorage:access_type.read_write");
        }
    }

    public void actionPerformed() {
        TileDataManager.setParameter(parameter, ((parameter.getValue() + 1 > IAccessType.READ_WRITE) ? IAccessType.READ : parameter.getValue() + 1));
    }
}

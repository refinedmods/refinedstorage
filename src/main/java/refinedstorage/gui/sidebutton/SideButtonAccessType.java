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
        switch (parameter.getValue())
        {
            case IAccessType.READ:
                return "Read";
            case IAccessType.WRITE:
                return "Write";
            default:
            case IAccessType.READ_WRITE:
                return "Read & Write";
        }
    }

    public void actionPerformed() {
       TileDataManager.setParameter(parameter, ((parameter.getValue() + 1 > IAccessType.READ_WRITE) ? 1 : parameter.getValue() + 1));
    }
}

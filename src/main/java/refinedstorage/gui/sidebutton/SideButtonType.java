package refinedstorage.gui.sidebutton;

import net.minecraft.util.text.TextFormatting;
import refinedstorage.gui.GuiBase;
import refinedstorage.tile.config.IType;
import refinedstorage.tile.data.TileDataManager;
import refinedstorage.tile.data.TileDataParameter;

public class SideButtonType extends SideButton {
    private TileDataParameter<Integer> type;

    public SideButtonType(GuiBase gui, TileDataParameter<Integer> type) {
        super(gui);

        this.type = type;
    }

    @Override
    public String getTooltip() {
        return TextFormatting.GREEN + gui.t("sidebutton.refinedstorage:type") + TextFormatting.RESET + "\n" + gui.t("sidebutton.refinedstorage:type." + type.getValue());
    }

    @Override
    protected void drawButtonIcon(int x, int y) {
        gui.drawTexture(x, y, 16 * type.getValue(), 128, 16, 16);
    }

    @Override
    public void actionPerformed() {
        TileDataManager.setParameter(type, type.getValue() == IType.ITEMS ? IType.FLUIDS : IType.ITEMS);
    }
}

package refinedstorage.gui.sidebutton;

import net.minecraft.client.Minecraft;
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
    public String getTooltip(GuiBase gui) {
        return TextFormatting.GREEN + gui.t("sidebutton.refinedstorage:type") + TextFormatting.RESET + "\n" + gui.t("sidebutton.refinedstorage:type." + type.getValue());
    }

    @Override
    public void drawButton(Minecraft mc, int x, int y) {
        super.drawButton(mc, x, y);

        gui.drawTexture(xPosition, yPosition, 16 * type.getValue(), 128, 16, 16);
    }

    @Override
    public void actionPerformed() {
        TileDataManager.setParameter(type, type.getValue() == IType.ITEMS ? IType.FLUIDS : IType.ITEMS);
    }
}

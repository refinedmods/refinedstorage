package storagecraft.gui.sidebutton;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import storagecraft.StorageCraft;
import storagecraft.gui.GuiBase;
import storagecraft.network.MessageRedstoneModeUpdate;
import storagecraft.tile.IRedstoneControllable;

public class SideButtonRedstoneMode extends SideButton {
	private IRedstoneControllable control;

	public SideButtonRedstoneMode(IRedstoneControllable control) {
		this.control = control;
	}

	@Override
	public String getTooltip(GuiBase gui) {
		return gui.t("misc.storagecraft:redstoneMode." + control.getRedstoneMode().id);
	}

	@Override
	public void draw(GuiBase gui, int x, int y) {
		gui.drawItem(x, y, new ItemStack(Items.redstone, 1));
	}

	@Override
	public void actionPerformed() {
		StorageCraft.NETWORK.sendToServer(new MessageRedstoneModeUpdate(control));
	}
}

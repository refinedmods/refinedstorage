package storagecraft.gui.sidebutton;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import storagecraft.StorageCraft;
import storagecraft.gui.GuiBase;
import storagecraft.network.MessageRedstoneModeUpdate;
import storagecraft.tile.IRedstoneModeSetting;

public class SideButtonRedstoneMode extends SideButton
{
	private IRedstoneModeSetting setting;

	public SideButtonRedstoneMode(IRedstoneModeSetting setting)
	{
		this.setting = setting;
	}

	@Override
	public String getTooltip(GuiBase gui)
	{
		StringBuilder builder = new StringBuilder();

		builder.append(EnumChatFormatting.RED).append(gui.t("sidebutton.storagecraft:redstone_mode")).append(EnumChatFormatting.RESET).append("\n");

		builder.append(gui.t("sidebutton.storagecraft:redstone_mode." + setting.getRedstoneMode().id));

		return builder.toString();
	}

	@Override
	public void draw(GuiBase gui, int x, int y)
	{
		gui.drawItem(x, y, new ItemStack(Items.redstone, 1));
	}

	@Override
	public void actionPerformed()
	{
		StorageCraft.NETWORK.sendToServer(new MessageRedstoneModeUpdate(setting));
	}
}

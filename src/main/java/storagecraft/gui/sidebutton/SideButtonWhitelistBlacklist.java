package storagecraft.gui.sidebutton;

import net.minecraft.util.EnumChatFormatting;
import storagecraft.StorageCraft;
import storagecraft.gui.GuiBase;
import storagecraft.network.MessageWhitelistBlacklistToggle;
import storagecraft.tile.IWhitelistBlacklistSetting;

public class SideButtonWhitelistBlacklist extends SideButton
{
	private IWhitelistBlacklistSetting wb;

	public SideButtonWhitelistBlacklist(IWhitelistBlacklistSetting wb)
	{
		this.wb = wb;
	}

	@Override
	public String getTooltip(GuiBase gui)
	{
		StringBuilder builder = new StringBuilder();

		builder.append(EnumChatFormatting.GREEN).append(gui.t("sidebutton.storagecraft:whitelist_blacklist")).append(EnumChatFormatting.RESET).append("\n");

		builder.append(gui.t("sidebutton.storagecraft:whitelist_blacklist." + (wb.isWhitelist() ? "whitelist" : "blacklist")));

		return builder.toString();
	}

	@Override
	public void draw(GuiBase gui, int x, int y)
	{
		gui.bindTexture("icons.png");

		int tx = 0;

		if (wb.isWhitelist())
		{
			tx = 0;
		}
		else if (wb.isBlacklist())
		{
			tx = 16;
		}

		gui.drawTexture(x, y + 1, tx, 64, 16, 16);
	}

	@Override
	public void actionPerformed()
	{
		StorageCraft.NETWORK.sendToServer(new MessageWhitelistBlacklistToggle(wb));
	}
}

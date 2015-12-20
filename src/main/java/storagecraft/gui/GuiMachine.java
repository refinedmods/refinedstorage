package storagecraft.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import storagecraft.StorageCraft;
import storagecraft.network.MessageRedstoneModeUpdate;
import storagecraft.tile.TileMachine;

public abstract class GuiMachine extends GuiBase {
	private TileMachine machine;

	public static final ItemStack REDSTONE_MODE_ITEM = new ItemStack(Items.redstone, 1);

	private int redstoneModeX;
	private int redstoneModeY = 6;
	private int redstoneModeWidth = 20;
	private int redstoneModeHeight = 20;

	public GuiMachine(Container container, int w, int h, TileMachine machine) {
		super(container, w, h);

		this.redstoneModeX = w - 1;
		this.machine = machine;
	}

	@Override
	public void init(int x, int y) {
		buttonList.add(new GuiButton(0, x + redstoneModeX, y + redstoneModeY, redstoneModeWidth, redstoneModeHeight, ""));
	}

	@Override
	public void update(int x, int y) {
	}

	@Override
	public void drawForeground(int mouseX, int mouseY) {
		drawItem(redstoneModeX + 2, redstoneModeY + 1, REDSTONE_MODE_ITEM);

		if (inBounds(redstoneModeX, redstoneModeY, redstoneModeWidth, redstoneModeHeight, mouseX, mouseY)) {
			drawTooltip(mouseX, mouseY, t("misc.storagecraft:redstoneMode." + machine.getRedstoneMode().id));
		}
	}

	@Override
	protected void actionPerformed(GuiButton button) {
		super.actionPerformed(button);

		if (button.id == 0) {
			StorageCraft.NETWORK.sendToServer(new MessageRedstoneModeUpdate(machine.xCoord, machine.yCoord, machine.zCoord));
		}
	}
}

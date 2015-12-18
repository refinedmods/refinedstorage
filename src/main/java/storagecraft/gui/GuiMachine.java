package storagecraft.gui;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import storagecraft.StorageCraft;
import storagecraft.network.MessageRedstoneModeUpdate;
import storagecraft.tile.TileMachine;

public abstract class GuiMachine extends GuiContainer {
	private TileMachine machine;

	private int bx;
	private int by = 6;
	private int bw = 20;
	private int bh = 20;

	public GuiMachine(Container container, TileMachine machine) {
		super(container);

		this.bx = xSize - 1;
		this.machine = machine;
	}

	@Override
	public void initGui() {
		super.initGui();

		buttonList.add(new GuiButton(0, ((this.width - xSize) / 2) + bx, ((this.height - ySize) / 2) + by, bw, bh, ""));
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		int mx = mouseX - ((this.width - xSize) / 2);
		int my = mouseY - ((this.height - ySize) / 2);

		itemRender.renderItemIntoGUI(fontRendererObj, mc.getTextureManager(), new ItemStack(Items.redstone, 1), bx + 2, by + 1);

		if (mx >= bx && mx <= bx + bw && my >= by && my <= by + bh) {
			List<String> lines = new ArrayList<String>();

			lines.add(StatCollector.translateToLocal("misc.storagecraft:redstoneMode." + machine.getRedstoneMode().id));

			this.drawHoveringText(lines, mx, my, fontRendererObj);
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

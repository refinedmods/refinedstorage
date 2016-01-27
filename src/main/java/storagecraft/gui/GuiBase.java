package storagecraft.gui;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import org.lwjgl.opengl.GL11;
import storagecraft.StorageCraft;
import storagecraft.gui.sidebutton.SideButton;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class GuiBase extends GuiContainer
{
	public static final int SIDE_BUTTON_WIDTH = 20;
	public static final int SIDE_BUTTON_HEIGHT = 20;

	private List<SideButton> sideButtons = new ArrayList<SideButton>();

	private int lastButtonId = 0;
	private int lastSideButtonY = 6;

	public GuiBase(Container container, int w, int h)
	{
		super(container);

		this.xSize = w;
		this.ySize = h;
	}

	@Override
	public void initGui()
	{
		super.initGui();

		sideButtons.clear();

		lastButtonId = 0;
		lastSideButtonY = 6;

		init(guiLeft, guiTop);
	}

	@Override
	public void updateScreen()
	{
		super.updateScreen();

		update(guiLeft, guiTop);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float renderPartialTicks, int mouseX, int mouseY)
	{
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

		drawBackground(guiLeft, guiTop, mouseX, mouseY);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
	{
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

		mouseX -= guiLeft;
		mouseY -= guiTop;

		for (SideButton sideButton : sideButtons)
		{
			sideButton.draw(this, sideButton.getX() + 2, sideButton.getY() + 1);

			if (inBounds(sideButton.getX(), sideButton.getY(), SIDE_BUTTON_WIDTH, SIDE_BUTTON_HEIGHT, mouseX, mouseY))
			{
				drawTooltip(mouseX, mouseY, sideButton.getTooltip(this));
			}
		}

		drawForeground(mouseX, mouseY);
	}

	@Override
	protected void actionPerformed(GuiButton button) throws IOException
	{
		super.actionPerformed(button);

		for (SideButton sideButton : sideButtons)
		{
			if (sideButton.getId() == button.id)
			{
				sideButton.actionPerformed();
			}
		}
	}

	public GuiButton addButton(int x, int y, int w, int h)
	{
		return addButton(x, y, w, h, "");
	}

	public GuiButton addButton(int x, int y, int w, int h, String text)
	{
		GuiButton button = new GuiButton(lastButtonId++, x, y, w, h, text);

		buttonList.add(button);

		return button;
	}

	public void addSideButton(SideButton button)
	{
		button.setX(xSize - 1);
		button.setY(lastSideButtonY);
		button.setId(addButton(guiLeft + button.getX(), guiTop + button.getY(), SIDE_BUTTON_WIDTH, SIDE_BUTTON_HEIGHT).id);

		lastSideButtonY += SIDE_BUTTON_HEIGHT + 4;

		sideButtons.add(button);
	}

	public boolean inBounds(int x, int y, int w, int h, int ox, int oy)
	{
		return ox >= x && ox <= x + w && oy >= y && oy <= y + h;
	}

	public void bindTexture(String file)
	{
		bindTexture(StorageCraft.ID, file);
	}

	public void bindTexture(String base, String file)
	{
		mc.getTextureManager().bindTexture(new ResourceLocation(base, "textures/" + file));
	}

	public void drawItem(int x, int y, ItemStack stack)
	{
		drawItem(x, y, stack, false);
	}

	public void drawItem(int x, int y, ItemStack stack, boolean withOverlay)
	{
		zLevel = 200.0F;
		itemRender.zLevel = 200.0F;

		FontRenderer font = null;

		if (stack != null)
		{
			font = stack.getItem().getFontRenderer(stack);
		}

		if (font == null)
		{
			font = fontRendererObj;
		}

		itemRender.renderItemIntoGUI(stack, x, y);

		if (withOverlay)
		{
			itemRender.renderItemOverlayIntoGUI(font, stack, x, y, null);
		}

		zLevel = 0.0F;
		itemRender.zLevel = 0.0F;
	}

	public void drawString(int x, int y, String message)
	{
		drawString(x, y, message, 4210752);
	}

	public void drawString(int x, int y, String message, int color)
	{
		GL11.glDisable(GL11.GL_LIGHTING);
		fontRendererObj.drawString(message, x, y, color);
		GL11.glEnable(GL11.GL_LIGHTING);
	}

	public void drawTooltip(int x, int y, String message)
	{
		drawHoveringText(Arrays.asList(message.split("\n")), x, y);
	}

	public void drawTooltip(int x, int y, ItemStack stack)
	{
		renderToolTip(stack, x, y);
	}

	public String t(String name, Object... format)
	{
		return StatCollector.translateToLocalFormatted(name, format);
	}

	public abstract void init(int x, int y);

	public abstract void update(int x, int y);

	public abstract void drawBackground(int x, int y, int mouseX, int mouseY);

	public abstract void drawForeground(int mouseX, int mouseY);
}

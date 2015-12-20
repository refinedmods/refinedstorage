package storagecraft.gui;

import com.google.common.base.Joiner;
import java.util.List;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import storagecraft.StorageCraft;

public abstract class GuiBase extends GuiContainer {
	public GuiBase(Container container, int w, int h) {
		super(container);

		this.xSize = w;
		this.ySize = h;
	}

	@Override
	public void initGui() {
		super.initGui();

		init(guiLeft, guiTop);
	}

	@Override
	public void updateScreen() {
		super.updateScreen();

		update(guiLeft, guiTop);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float renderPartialTicks, int mouseX, int mouseY) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

		drawBackground(guiLeft, guiTop, mouseX, mouseY);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

		drawForeground(mouseX - guiLeft, mouseY - guiTop);
	}

	protected boolean inBounds(int x, int y, int w, int h, int ox, int oy) {
		return ox >= x && ox <= x + w && oy >= y && oy <= y + h;
	}

	protected void bindTexture(String file) {
		bindTexture(StorageCraft.ID, file);
	}

	protected void bindTexture(String base, String file) {
		mc.getTextureManager().bindTexture(new ResourceLocation(base, "textures/" + file));
	}

	protected void drawItem(int x, int y, ItemStack stack) {
		drawItem(x, y, stack, false);
	}

	protected void drawItem(int x, int y, ItemStack stack, boolean withOverlay) {
		zLevel = 100;
		itemRender.zLevel = 100;

		GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);

		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glEnable(GL12.GL_RESCALE_NORMAL);
		GL11.glEnable(GL11.GL_DEPTH_TEST);

		RenderHelper.enableGUIStandardItemLighting();

		itemRender.renderItemAndEffectIntoGUI(fontRendererObj, mc.renderEngine, stack, x, y);

		if (withOverlay) {
			itemRender.renderItemOverlayIntoGUI(fontRendererObj, mc.renderEngine, stack, x, y);
		}

		GL11.glPopAttrib();

		itemRender.zLevel = 0;
		zLevel = 0;
	}

	protected void drawString(int x, int y, String message) {
		drawString(x, y, message, 4210752);
	}

	protected void drawString(int x, int y, String message, int color) {
		fontRendererObj.drawString(message, x, y, color);
	}

	// https://github.com/AppliedEnergistics/Applied-Energistics-2/blob/master/src/main/java/appeng/client/gui/AEBaseGui.java
	protected void drawTooltip(int x, int y, String message) {
		GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);

		GL11.glDisable(GL12.GL_RESCALE_NORMAL);

		RenderHelper.disableStandardItemLighting();

		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glDisable(GL11.GL_DEPTH_TEST);

		String[] lines = message.split("\n");

		if (lines.length > 0) {
			int var5 = 0;
			int var6;
			int var7;

			for (var6 = 0; var6 < lines.length; ++var6) {
				var7 = this.fontRendererObj.getStringWidth(lines[var6]);

				if (var7 > var5) {
					var5 = var7;
				}
			}

			var6 = x + 12;
			var7 = y - 12;
			int var9 = 8;

			if (lines.length > 1) {
				var9 += 2 + (lines.length - 1) * 10;
			}

			if (this.guiTop + var7 + var9 + 6 > this.height) {
				var7 = this.height - var9 - this.guiTop - 6;
			}

			zLevel = 300.0F;
			itemRender.zLevel = 300.0F;

			int var10 = -267386864;

			drawGradientRect(var6 - 3, var7 - 4, var6 + var5 + 3, var7 - 3, var10, var10);
			drawGradientRect(var6 - 3, var7 + var9 + 3, var6 + var5 + 3, var7 + var9 + 4, var10, var10);
			drawGradientRect(var6 - 3, var7 - 3, var6 + var5 + 3, var7 + var9 + 3, var10, var10);
			drawGradientRect(var6 - 4, var7 - 3, var6 - 3, var7 + var9 + 3, var10, var10);
			drawGradientRect(var6 + var5 + 3, var7 - 3, var6 + var5 + 4, var7 + var9 + 3, var10, var10);

			int var11 = 1347420415;
			int var12 = (var11 & 16711422) >> 1 | var11 & -16777216;

			drawGradientRect(var6 - 3, var7 - 3 + 1, var6 - 3 + 1, var7 + var9 + 3 - 1, var11, var12);
			drawGradientRect(var6 + var5 + 2, var7 - 3 + 1, var6 + var5 + 3, var7 + var9 + 3 - 1, var11, var12);
			drawGradientRect(var6 - 3, var7 - 3, var6 + var5 + 3, var7 - 3 + 1, var11, var11);
			drawGradientRect(var6 - 3, var7 + var9 + 2, var6 + var5 + 3, var7 + var9 + 3, var12, var12);

			for (int var13 = 0; var13 < lines.length; ++var13) {
				String var14 = lines[var13];

				if (var13 == 0) {
					var14 = '\u00a7' + Integer.toHexString(15) + var14;
				} else {
					var14 = "\u00a77" + var14;
				}

				fontRendererObj.drawStringWithShadow(var14, var6, var7, -1);

				if (var13 == 0) {
					var7 += 2;
				}

				var7 += 10;
			}

			zLevel = 0.0F;
			itemRender.zLevel = 0.0F;
		}

		GL11.glPopAttrib();
	}

	protected void drawTooltip(int x, int y, ItemStack stack) {
		List list = stack.getTooltip(mc.thePlayer, mc.gameSettings.advancedItemTooltips);

		for (int i = 0; i < list.size(); ++i) {
			if (i == 0) {
				list.set(i, stack.getRarity().rarityColor + (String) list.get(i));
			} else {
				list.set(i, EnumChatFormatting.GRAY + (String) list.get(i));
			}
		}

		drawTooltip(x, y, Joiner.on("\n").join(list));
	}

	protected String t(String name, Object... format) {
		return StatCollector.translateToLocalFormatted(name, format);
	}

	public abstract void init(int x, int y);

	public abstract void update(int x, int y);

	public abstract void drawBackground(int x, int y, int mouseX, int mouseY);

	public abstract void drawForeground(int mouseX, int mouseY);
}

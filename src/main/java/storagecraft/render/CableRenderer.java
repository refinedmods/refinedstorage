package storagecraft.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import storagecraft.render.model.CableModel;
import storagecraft.tile.TileCable;

public class CableRenderer extends TileEntitySpecialRenderer {
	public static final ResourceLocation CABLE_RESOURCE = new ResourceLocation("storagecraft:textures/blocks/cable.png");

	@Override
	public void renderTileEntityAt(TileEntity tile, double x, double y, double z, float scale) {
		CableModel model = new CableModel((TileCable) tile);

		GL11.glPushMatrix();
		GL11.glTranslatef((float) x, (float) y, (float) z);

		{
			GL11.glPushMatrix();
			Minecraft.getMinecraft().renderEngine.bindTexture(CABLE_RESOURCE);
			model.render(null, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);
			GL11.glPopMatrix();
		}

		GL11.glPopMatrix();
	}
}

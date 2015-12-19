package storagecraft.render;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import org.lwjgl.opengl.GL11;
import storagecraft.render.model.CableModel;
import storagecraft.tile.TileCable;

public class BlockCableRenderer extends TileEntitySpecialRenderer {
	public static final CableModel CABLE_MODEL = new CableModel();

	@Override
	public void renderTileEntityAt(TileEntity tile, double x, double y, double z, float scale) {
		GL11.glPushMatrix();
		GL11.glTranslatef((float) x, (float) y, (float) z);

		CABLE_MODEL.render((TileCable) tile, 0.0625F);

		GL11.glPopMatrix();
	}
}

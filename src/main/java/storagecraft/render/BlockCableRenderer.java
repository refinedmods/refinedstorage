package storagecraft.render;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import org.lwjgl.opengl.GL11;
import storagecraft.render.model.CableModel;
import storagecraft.tile.TileCable;

public class BlockCableRenderer extends TileEntitySpecialRenderer<TileCable>
{
	public static final BlockCableRenderer INSTANCE = new BlockCableRenderer(new CableModel());

	private CableModel model;

	public BlockCableRenderer(CableModel model)
	{
		this.model = model;
	}

	@Override
	public void renderTileEntityAt(TileCable tile, double x, double y, double z, float scale, int a)
	{
		GL11.glPushMatrix();
		GL11.glTranslated(x, y, z);

		model.render(tile);

		GL11.glPopMatrix();
	}
}

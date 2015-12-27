package storagecraft.render;

import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;
import org.lwjgl.opengl.GL11;
import storagecraft.render.model.CableModel;

public class ItemCableRenderer implements IItemRenderer
{
	public static final CableModel CABLE_MODEL = new CableModel();

	@Override
	public boolean handleRenderType(ItemStack item, ItemRenderType type)
	{
		return true;
	}

	@Override
	public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper)
	{
		return true;
	}

	@Override
	public void renderItem(ItemRenderType type, ItemStack item, Object... data)
	{
		GL11.glPushMatrix();

		CABLE_MODEL.render(item, 0.0625F);

		GL11.glPopMatrix();
	}
}

package storagecraft.render;

import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;

public class ItemCableRenderer implements IItemRenderer {
	public static final BlockCableRenderer CABLE_RENDERER = new BlockCableRenderer();

	@Override
	public boolean handleRenderType(ItemStack item, ItemRenderType type) {
		return true;
	}

	@Override
	public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
		return true;
	}

	@Override
	public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
		CABLE_RENDERER.renderTileEntityAt(null, 0, 0, 0, 0);
	}
}

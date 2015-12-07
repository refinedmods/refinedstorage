package storagecraft.proxy;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import storagecraft.render.CableRenderer;
import storagecraft.tile.TileCable;

public class ClientProxy extends CommonProxy {
	@Override
	public void preInit(FMLPreInitializationEvent e) {
		super.preInit(e);

		ClientRegistry.bindTileEntitySpecialRenderer(TileCable.class, new CableRenderer());
	}
}

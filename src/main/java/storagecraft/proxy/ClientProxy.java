package storagecraft.proxy;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import net.minecraft.item.Item;
import net.minecraftforge.client.MinecraftForgeClient;
import storagecraft.StorageCraftBlocks;
import storagecraft.render.BlockCableRenderer;
import storagecraft.render.ItemCableRenderer;
import storagecraft.tile.TileCable;

public class ClientProxy extends CommonProxy {
	@Override
	public void preInit(FMLPreInitializationEvent e) {
		super.preInit(e);

		ClientRegistry.bindTileEntitySpecialRenderer(TileCable.class, new BlockCableRenderer());

		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(StorageCraftBlocks.CABLE), new ItemCableRenderer());
	}
}

package storagecraft.proxy;

import net.minecraft.item.Item;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import storagecraft.StorageCraftBlocks;
import storagecraft.render.BlockCableRenderer;
import storagecraft.render.ItemCableRenderer;
import storagecraft.tile.TileCable;

public class ClientProxy extends CommonProxy
{
	@Override
	public void preInit(FMLPreInitializationEvent e)
	{
		super.preInit(e);

		ClientRegistry.bindTileEntitySpecialRenderer(TileCable.class, new BlockCableRenderer());

		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(StorageCraftBlocks.CABLE), new ItemCableRenderer());
	}
}

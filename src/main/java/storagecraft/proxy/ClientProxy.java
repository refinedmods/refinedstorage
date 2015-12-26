package storagecraft.proxy;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemModelMesher;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import storagecraft.StorageCraftBlocks;
import storagecraft.StorageCraftItems;
import storagecraft.item.ItemStorageCell;
import storagecraft.render.BlockCableRenderer;
import storagecraft.render.ItemCableRenderer;
import storagecraft.tile.TileCable;

public class ClientProxy extends CommonProxy
{
	@Override
	public void preInit(FMLPreInitializationEvent e)
	{
		super.preInit(e);

		ModelBakery.addVariantName(StorageCraftItems.STORAGE_CELL,
			"storagecraft:1k_storage_cell",
			"storagecraft:4k_storage_cell",
			"storagecraft:16k_storage_cell",
			"storagecraft:64k_storage_cell",
			"storagecraft:creative_storage_cell"
		);

		ClientRegistry.bindTileEntitySpecialRenderer(TileCable.class, new BlockCableRenderer());

		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(StorageCraftBlocks.CABLE), new ItemCableRenderer());
	}

	@Override
	public void init(FMLInitializationEvent e)
	{
		super.init(e);

		ItemModelMesher mesher = Minecraft.getMinecraft().getRenderItem().getItemModelMesher();

		mesher.register(StorageCraftItems.STORAGE_CELL, ItemStorageCell.TYPE_1K, new ModelResourceLocation("storagecraft:1k_storage_cell", "inventory"));
		mesher.register(StorageCraftItems.STORAGE_CELL, ItemStorageCell.TYPE_4K, new ModelResourceLocation("storagecraft:4k_storage_cell", "inventory"));
		mesher.register(StorageCraftItems.STORAGE_CELL, ItemStorageCell.TYPE_16K, new ModelResourceLocation("storagecraft:16k_storage_cell", "inventory"));
		mesher.register(StorageCraftItems.STORAGE_CELL, ItemStorageCell.TYPE_64K, new ModelResourceLocation("storagecraft:64k_storage_cell", "inventory"));
		mesher.register(StorageCraftItems.STORAGE_CELL, ItemStorageCell.TYPE_CREATIVE, new ModelResourceLocation("storagecraft:creative_storage_cell", "inventory"));
	}
}

package storagecraft;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import storagecraft.item.ItemStorageCell;
import storagecraft.proxy.CommonProxy;

@Mod(modid = StorageCraft.ID, version = StorageCraft.VERSION, dependencies = StorageCraft.DEPENDENCIES)
public final class StorageCraft
{
	public static final String ID = "storagecraft";
	public static final String VERSION = "0.1";
	public static final String DEPENDENCIES = "after:NotEnoughItems";

	public static final SimpleNetworkWrapper NETWORK = NetworkRegistry.INSTANCE.newSimpleChannel(ID);

	public static final CreativeTabs TAB = new CreativeTabs(ID)
	{
		@Override
		public ItemStack getIconItemStack()
		{
			return new ItemStack(StorageCraftItems.STORAGE_CELL, 1, ItemStorageCell.TYPE_1K);
		}

		@Override
		public Item getTabIconItem()
		{
			return null;
		}
	};

	@SidedProxy(clientSide = "storagecraft.proxy.ClientProxy", serverSide = "storagecraft.proxy.ServerProxy")
	public static CommonProxy PROXY;

	@Instance
	public static StorageCraft INSTANCE;

	@EventHandler
	public void preInit(FMLPreInitializationEvent e)
	{
		PROXY.preInit(e);
	}

	@EventHandler
	public void init(FMLInitializationEvent e)
	{
		PROXY.init(e);
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent e)
	{
		PROXY.postInit(e);
	}
}

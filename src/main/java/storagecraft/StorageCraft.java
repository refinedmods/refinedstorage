package storagecraft;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import storagecraft.item.ItemStorageCell;
import storagecraft.proxy.CommonProxy;

@Mod(modid = StorageCraft.ID, version = StorageCraft.VERSION, dependencies = "required-after:JEI@[2.18,);")
public final class StorageCraft
{
	public static final String ID = "storagecraft";
	public static final String VERSION = "0.1.1";

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

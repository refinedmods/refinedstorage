package storagecraft;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import storagecraft.network.MessageTileUpdate;
import storagecraft.render.CableRenderer;
import storagecraft.tile.TileCable;
import storagecraft.tile.TileController;

@Mod(modid = SC.ID, version = SC.VERSION)
public class SC {
	public static final String ID = "storagecraft";
	public static final String VERSION = "1.0";
	public static final SimpleNetworkWrapper NETWORK = NetworkRegistry.INSTANCE.newSimpleChannel(ID);
	public static final CreativeTabs TAB = new CreativeTabs(ID) {
		@Override
		public Item getTabIconItem() {
			return Items.emerald;
		}
	};

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		NETWORK.registerMessage(MessageTileUpdate.class, MessageTileUpdate.class, 0, Side.CLIENT);

		if (event.getSide() == Side.CLIENT) {
			ClientRegistry.bindTileEntitySpecialRenderer(TileCable.class, new CableRenderer());
		}

		GameRegistry.registerTileEntity(TileController.class, "controller");
		GameRegistry.registerTileEntity(TileCable.class, "cable");

		GameRegistry.registerBlock(SCBlocks.CONTROLLER, "controller");
		GameRegistry.registerBlock(SCBlocks.CABLE, "cable");
	}

	@EventHandler
	public void init(FMLInitializationEvent event) {
	}
}

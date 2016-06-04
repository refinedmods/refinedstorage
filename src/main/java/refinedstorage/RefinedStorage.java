package refinedstorage;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import refinedstorage.item.ItemStorageDisk;
import refinedstorage.proxy.CommonProxy;

@Mod(modid = RefinedStorage.ID, version = RefinedStorage.VERSION)
public final class RefinedStorage {
    public static final String ID = "refinedstorage";
    public static final String VERSION = "0.7.8";

    public static final SimpleNetworkWrapper NETWORK = NetworkRegistry.INSTANCE.newSimpleChannel(ID);

    public static final CreativeTabs TAB = new CreativeTabs(ID) {
        @Override
        public ItemStack getIconItemStack() {
            return new ItemStack(RefinedStorageItems.STORAGE_DISK, 1, ItemStorageDisk.TYPE_1K);
        }

        @Override
        public Item getTabIconItem() {
            return null;
        }
    };

    @SidedProxy(clientSide = "refinedstorage.proxy.ClientProxy", serverSide = "refinedstorage.proxy.ServerProxy")
    public static CommonProxy PROXY;

    @Instance
    public static RefinedStorage INSTANCE;

    @EventHandler
    public void preInit(FMLPreInitializationEvent e) {
        PROXY.preInit(e);
    }

    @EventHandler
    public void init(FMLInitializationEvent e) {
        PROXY.init(e);
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent e) {
        PROXY.postInit(e);
    }

    public static boolean hasJei() {
        return Loader.isModLoaded("JEI");
    }
}

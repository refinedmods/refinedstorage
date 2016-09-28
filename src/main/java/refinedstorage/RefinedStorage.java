package refinedstorage;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import refinedstorage.proxy.CommonProxy;

@Mod(modid = RefinedStorage.ID, version = RefinedStorage.VERSION, guiFactory = RefinedStorage.GUI_FACTORY, dependencies = RefinedStorage.DEPENDENCIES)
public final class RefinedStorage {
    public static final String ID = "refinedstorage";
    public static final String VERSION = "1.1.1";
    public static final String DEPENDENCIES = "required-after:Forge@[12.18.1.2088,);required-after:mcmultipart@[1.2.1,);after:JEI@[3.11.0,);";
    public static final String GUI_FACTORY = "refinedstorage.gui.config.ModGuiFactory";

    @SidedProxy(clientSide = "refinedstorage.proxy.ClientProxy", serverSide = "refinedstorage.proxy.ServerProxy")
    public static CommonProxy PROXY;

    @Instance
    public static RefinedStorage INSTANCE;

    public RefinedStorageConfig config;

    public final SimpleNetworkWrapper network = NetworkRegistry.INSTANCE.newSimpleChannel(ID);

    public final CreativeTabs tab = new CreativeTabs(ID) {
        @Override
        public ItemStack getIconItemStack() {
            return new ItemStack(RefinedStorageItems.STORAGE_HOUSING);
        }

        @Override
        public Item getTabIconItem() {
            return null;
        }
    };

    static {
        FluidRegistry.enableUniversalBucket();
    }

    @EventHandler
    public void preInit(FMLPreInitializationEvent e) {
        config = new RefinedStorageConfig(e.getSuggestedConfigurationFile());

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
}

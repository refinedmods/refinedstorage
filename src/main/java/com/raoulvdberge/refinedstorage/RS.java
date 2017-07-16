package com.raoulvdberge.refinedstorage;

import com.raoulvdberge.refinedstorage.proxy.ProxyCommon;
import net.minecraft.creativetab.CreativeTabs;
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

@Mod(modid = RS.ID, version = RS.VERSION, dependencies = RS.DEPENDENCIES, guiFactory = RS.GUI_FACTORY, updateJSON = RS.UPDATE_JSON)
public final class RS {
    static {
        FluidRegistry.enableUniversalBucket();
    }

    public static final String ID = "refinedstorage";
    public static final String VERSION = "1.5.12";
    public static final String DEPENDENCIES = "required-after:forge@[14.21.0.2363,);after:mcmultipart@[2.2.1,);after:storagedrawers@[1.12-5.2.2,);";
    public static final String GUI_FACTORY = "com.raoulvdberge.refinedstorage.gui.config.ModGuiFactory";
    public static final String UPDATE_JSON = "https://refinedstorage.raoulvdberge.com/update";

    @SidedProxy(clientSide = "com.raoulvdberge.refinedstorage.proxy.ProxyClient", serverSide = "com.raoulvdberge.refinedstorage.proxy.ProxyCommon")
    public static ProxyCommon PROXY;

    @Instance
    public static RS INSTANCE;

    public RSConfig config;
    public final SimpleNetworkWrapper network = NetworkRegistry.INSTANCE.newSimpleChannel(ID);
    public final CreativeTabs tab = new CreativeTabs(ID) {
        @Override
        public ItemStack getTabIconItem() {
            return new ItemStack(RSItems.STORAGE_HOUSING);
        }
    };

    @EventHandler
    public void preInit(FMLPreInitializationEvent e) {
        config = new RSConfig(e.getSuggestedConfigurationFile());

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

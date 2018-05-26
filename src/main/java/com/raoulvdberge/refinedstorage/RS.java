package com.raoulvdberge.refinedstorage;

import com.raoulvdberge.refinedstorage.proxy.ProxyCommon;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLFingerprintViolationEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;

@Mod(modid = RS.ID, version = RS.VERSION, acceptedMinecraftVersions = "[1.12.2,1.13)", guiFactory = RS.GUI_FACTORY, updateJSON = RS.UPDATE_JSON, certificateFingerprint = RS.FINGERPRINT)
public final class RS {
    static {
        FluidRegistry.enableUniversalBucket();
    }

    public static final String ID = "refinedstorage";
    public static final String VERSION = "1.6";
    public static final String GUI_FACTORY = "com.raoulvdberge.refinedstorage.gui.config.ModGuiFactory";
    public static final String UPDATE_JSON = "https://refinedstorage.raoulvdberge.com/update";
    public static final String FINGERPRINT = "57893d5b90a7336e8c63fe1c1e1ce472c3d59578";

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

    @EventHandler
    public void onFingerprintViolation(FMLFingerprintViolationEvent e) {
        FMLLog.bigWarning("Invalid fingerprint detected for the Refined Storage jar file! The file " + e.getSource().getName() + " may have been tampered with. This version will NOT be supported!");
    }
}

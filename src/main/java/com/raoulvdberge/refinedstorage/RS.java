package com.raoulvdberge.refinedstorage;

import com.raoulvdberge.refinedstorage.proxy.ProxyCommon;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Mod;

@Mod(RS.ID)
public final class RS {
    public static final String ID = "refinedstorage";
    public static final String VERSION = "1.7"; // TODO keep in sync with build.gradle

    public static ProxyCommon PROXY;

    public static RS INSTANCE;

    public RSConfig config;
    public final ItemGroup tab = new ItemGroup(ID) {
        @Override
        public ItemStack createIcon() {
            return new ItemStack(RSItems.STORAGE_HOUSING);
        }
    };
    public final ItemGroup coversTab = new ItemGroup(ID + ".covers") {
        @Override
        public ItemStack createIcon() {
            ItemStack stack = new ItemStack(RSItems.COVER);

            // TODO ItemCover.setItem(stack, new ItemStack(Blocks.STONE));

            return stack;
        }
    };

    /* TODO
    @EventHandler
    public void preInit(FMLPreInitializationEvent e) {
        config = new RSConfig(null, e.getSuggestedConfigurationFile());

        PROXY.preInit(e);

        NetworkHooks.openGui();
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
    public void onServerStarting(FMLServerStartingEvent e) {
        e.registerServerCommand(new CommandCreateDisk());
    }

    @EventHandler
    public void onFingerprintViolation(FMLFingerprintViolationEvent e) {
        FMLLog.bigWarning("Invalid fingerprint detected for the Refined Storage jar file! The file " + e.getSource().getName() + " may have been tampered with. This version will NOT be supported!");
    }*/
}

package com.raoulvdberge.refinedstorage.integration.mcmp;

import com.raoulvdberge.refinedstorage.RSBlocks;
import mcmultipart.api.addon.IMCMPAddon;
import mcmultipart.api.addon.MCMPAddon;
import mcmultipart.api.multipart.IMultipartRegistry;
import net.minecraft.item.Item;

@MCMPAddon
public class IntegrationMCMP implements IMCMPAddon {
    @Override
    public void registerParts(IMultipartRegistry registry) {
        registry.registerPartWrapper(RSBlocks.CABLE, new PartCable());
        registry.registerStackWrapper(Item.getItemFromBlock(RSBlocks.CABLE), s -> true, RSBlocks.CABLE);
    }
}

package com.raoulvdberge.refinedstorage.render;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraftforge.fml.common.Loader;

public class StateMapperCTM extends StateMapperBase {
    private static final String CTM_MOD_ID = "ctm";

    private String location;

    public StateMapperCTM(String location) {
        this.location = location;
    }

    @Override
    protected ModelResourceLocation getModelResourceLocation(IBlockState state) {
        return new ModelResourceLocation(location + (Loader.isModLoaded(CTM_MOD_ID) ? "_glow" : ""), getPropertyString(state.getProperties()));
    }
}

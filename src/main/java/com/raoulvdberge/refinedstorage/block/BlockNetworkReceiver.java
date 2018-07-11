package com.raoulvdberge.refinedstorage.block;

import com.raoulvdberge.refinedstorage.block.info.BlockInfoBuilder;
import com.raoulvdberge.refinedstorage.render.IModelRegistration;
import com.raoulvdberge.refinedstorage.tile.TileNetworkReceiver;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockNetworkReceiver extends BlockNode {
    public BlockNetworkReceiver() {
        super(BlockInfoBuilder.forId("network_receiver").tileEntity(TileNetworkReceiver::new).create());
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerModels(IModelRegistration modelRegistration) {
        modelRegistration.setModel(this, 0, new ModelResourceLocation(info.getId(), "inventory"));
    }

    @Override
    public boolean hasConnectedState() {
        return true;
    }
}

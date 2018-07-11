package com.raoulvdberge.refinedstorage.block;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.RSGui;
import com.raoulvdberge.refinedstorage.api.network.security.Permission;
import com.raoulvdberge.refinedstorage.block.info.BlockDirection;
import com.raoulvdberge.refinedstorage.block.info.BlockInfoBuilder;
import com.raoulvdberge.refinedstorage.render.IModelRegistration;
import com.raoulvdberge.refinedstorage.render.model.baked.BakedModelFullbright;
import com.raoulvdberge.refinedstorage.tile.TileSecurityManager;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

public class BlockSecurityManager extends BlockNode {
    public BlockSecurityManager() {
        super(BlockInfoBuilder.forId("security_manager").tileEntity(TileSecurityManager::new).create());
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerModels(IModelRegistration modelRegistration) {
        modelRegistration.setModel(this, 0, new ModelResourceLocation(info.getId(), "inventory"));

        modelRegistration.addBakedModelOverride(info.getId(), base -> new BakedModelFullbright(
            base,
            "refinedstorage:blocks/security_manager/cutouts/top_connected",
            "refinedstorage:blocks/security_manager/cutouts/front_connected",
            "refinedstorage:blocks/security_manager/cutouts/left_connected",
            "refinedstorage:blocks/security_manager/cutouts/back_connected",
            "refinedstorage:blocks/security_manager/cutouts/right_connected"
        ));
    }

    @Override
    public BlockRenderLayer getBlockLayer() {
        return BlockRenderLayer.CUTOUT;
    }

    @Override
    @Nullable
    public BlockDirection getDirection() {
        return BlockDirection.HORIZONTAL;
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (!world.isRemote) {
            if (player.getGameProfile().getId().equals(((TileSecurityManager) world.getTileEntity(pos)).getNode().getOwner())) {
                player.openGui(RS.INSTANCE, RSGui.SECURITY_MANAGER, world, pos.getX(), pos.getY(), pos.getZ());
            } else {
                openNetworkGui(RSGui.SECURITY_MANAGER, player, world, pos, side, Permission.MODIFY, Permission.SECURITY);
            }
        }

        return true;
    }

    @Override
    public boolean hasConnectedState() {
        return true;
    }
}

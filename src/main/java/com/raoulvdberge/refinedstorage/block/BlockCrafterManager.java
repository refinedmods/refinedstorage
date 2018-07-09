package com.raoulvdberge.refinedstorage.block;

import com.raoulvdberge.refinedstorage.RSGui;
import com.raoulvdberge.refinedstorage.api.network.security.Permission;
import com.raoulvdberge.refinedstorage.block.info.BlockDirection;
import com.raoulvdberge.refinedstorage.block.info.BlockInfoBuilder;
import com.raoulvdberge.refinedstorage.render.IModelRegistration;
import com.raoulvdberge.refinedstorage.render.statemapper.StateMapperCTM;
import com.raoulvdberge.refinedstorage.tile.TileCrafterManager;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

public class BlockCrafterManager extends BlockNode {
    public BlockCrafterManager() {
        super(BlockInfoBuilder.forId("crafter_manager").tileEntity(TileCrafterManager::new).create());
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerModels(IModelRegistration modelRegistration) {
        modelRegistration.setModel(this, 0, new ModelResourceLocation(info.getId(), "connected=false,direction=north"));
        modelRegistration.setStateMapper(this, new StateMapperCTM(info.getId()));
    }

    @Override
    @Nullable
    public BlockDirection getDirection() {
        return BlockDirection.HORIZONTAL;
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (!world.isRemote && openNetworkGui(RSGui.CRAFTER_MANAGER, player, world, pos, side, Permission.MODIFY, Permission.AUTOCRAFTING)) {
            ((TileCrafterManager) world.getTileEntity(pos)).getNode().sendTo((EntityPlayerMP) player);
        }

        return true;
    }

    @Override
    public boolean hasConnectedState() {
        return true;
    }
}

package com.raoulvdberge.refinedstorage.block;

import com.raoulvdberge.refinedstorage.RSGui;
import com.raoulvdberge.refinedstorage.apiimpl.network.node.NetworkNodeStorageMonitor;
import com.raoulvdberge.refinedstorage.block.info.BlockDirection;
import com.raoulvdberge.refinedstorage.block.info.BlockInfoBuilder;
import com.raoulvdberge.refinedstorage.render.IModelRegistration;
import com.raoulvdberge.refinedstorage.render.tesr.TileEntitySpecialRendererStorageMonitor;
import com.raoulvdberge.refinedstorage.tile.TileStorageMonitor;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

public class BlockStorageMonitor extends BlockNode {
    public BlockStorageMonitor() {
        super(BlockInfoBuilder.forId("storage_monitor").tileEntity(TileStorageMonitor::new).create());
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerModels(IModelRegistration modelRegistration) {
        modelRegistration.setModel(this, 0, new ModelResourceLocation(info.getId(), "connected=false,direction=north"));

        modelRegistration.setTesr(TileStorageMonitor.class, new TileEntitySpecialRendererStorageMonitor());
    }

    @Override
    @Nullable
    public BlockDirection getDirection() {
        return BlockDirection.HORIZONTAL;
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, PlayerEntity player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (!world.isRemote) {
            ItemStack held = player.inventory.getCurrentItem();

            if (player.isSneaking()) {
                openNetworkGui(RSGui.STORAGE_MONITOR, player, world, pos, side);
            } else {
                NetworkNodeStorageMonitor storageMonitor = ((TileStorageMonitor) world.getTileEntity(pos)).getNode();

                if (!held.isEmpty()) {
                    return storageMonitor.deposit(player, held);
                } else {
                    return storageMonitor.depositAll(player);
                }
            }
        }

        return true;
    }

    @Override
    public void onBlockClicked(World world, BlockPos pos, PlayerEntity player) {
        super.onBlockClicked(world, pos, player);

        if (!world.isRemote) {
            RayTraceResult rayResult = ForgeHooks.rayTraceEyes(player, player.getEntityAttribute(PlayerEntity.REACH_DISTANCE).getAttributeValue() + 1);

            if (rayResult == null) {
                return;
            }

            ((TileStorageMonitor) world.getTileEntity(pos)).getNode().extract(player, rayResult.sideHit);
        }
    }

    @Override
    public boolean hasConnectedState() {
        return true;
    }
}

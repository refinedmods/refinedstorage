package com.raoulvdberge.refinedstorage.block;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.RSGui;
import com.raoulvdberge.refinedstorage.api.network.node.INetworkNodeCable;
import com.raoulvdberge.refinedstorage.api.network.node.INetworkNodeProxy;
import com.raoulvdberge.refinedstorage.block.info.BlockInfoBuilder;
import com.raoulvdberge.refinedstorage.capability.CapabilityNetworkNodeProxy;
import com.raoulvdberge.refinedstorage.render.IModelRegistration;
import com.raoulvdberge.refinedstorage.render.collision.CollisionGroup;
import com.raoulvdberge.refinedstorage.render.constants.ConstantsWirelessTransmitter;
import com.raoulvdberge.refinedstorage.render.model.baked.BakedModelFullbright;
import com.raoulvdberge.refinedstorage.tile.TileWirelessTransmitter;
import net.minecraft.block.Block;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

public class BlockWirelessTransmitter extends BlockNode {
    public BlockWirelessTransmitter() {
        super(BlockInfoBuilder.forId("wireless_transmitter").tileEntity(TileWirelessTransmitter::new).create());
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerModels(IModelRegistration modelRegistration) {
        modelRegistration.setModel(this, 0, new ModelResourceLocation(info.getId(), "inventory"));

        modelRegistration.addBakedModelOverride(info.getId(), base -> new BakedModelFullbright(base, RS.ID + ":blocks/wireless_transmitter/cutouts/connected"));
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, PlayerEntity player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        return openNetworkGui(RSGui.WIRELESS_TRANSMITTER, player, world, pos, side);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void neighborChanged(IBlockState state, World world, BlockPos pos, Block blockIn, BlockPos fromPos) {
        if (!canPlaceBlockAt(world, pos) && world.getBlockState(pos).getBlock() == this) {
            dropBlockAsItem(world, pos, state, 0);

            world.setBlockToAir(pos);
        }
    }

    @Override
    public List<CollisionGroup> getCollisions(TileEntity tile, IBlockState state) {
        return Collections.singletonList(ConstantsWirelessTransmitter.COLLISION);
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean canPlaceBlockAt(World world, BlockPos pos) {
        TileEntity tile = world.getTileEntity(pos.offset(EnumFacing.DOWN));

        if (tile != null && tile.hasCapability(CapabilityNetworkNodeProxy.NETWORK_NODE_PROXY_CAPABILITY, EnumFacing.UP)) {
            INetworkNodeProxy proxy = tile.getCapability(CapabilityNetworkNodeProxy.NETWORK_NODE_PROXY_CAPABILITY, EnumFacing.UP);

            if (proxy != null && proxy.getNode() instanceof INetworkNodeCable) {
                return true;
            }
        }

        return world.getBlockState(pos.offset(EnumFacing.DOWN)).getBlock() instanceof BlockCable; // Make sure we still detect stuff like importers/exporters/etc.
    }

    @Override
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT;
    }

    @Override
    public boolean hasConnectedState() {
        return true;
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag flag) {
        super.addInformation(stack, world, tooltip, flag);

        tooltip.add(I18n.format("block.refinedstorage:wireless_transmitter.tooltip", TextFormatting.WHITE + I18n.format("block.refinedstorage:cable.name") + TextFormatting.GRAY));
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
        return BlockFaceShape.UNDEFINED;
    }
}

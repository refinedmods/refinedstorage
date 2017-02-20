package com.raoulvdberge.refinedstorage.integration.mcmp;

import com.raoulvdberge.refinedstorage.RSBlocks;
import com.raoulvdberge.refinedstorage.apiimpl.API;
import mcmultipart.api.container.IPartInfo;
import mcmultipart.api.multipart.IMultipart;
import mcmultipart.api.slot.EnumCenterSlot;
import mcmultipart.api.slot.IPartSlot;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class PartCable implements IMultipart {
    @Override
    public IPartSlot getSlotForPlacement(World world, BlockPos pos, IBlockState state, EnumFacing facing, float hitX, float hitY, float hitZ, EntityLivingBase placer) {
        return EnumCenterSlot.CENTER;
    }

    @Override
    public IPartSlot getSlotFromWorld(IBlockAccess world, BlockPos pos, IBlockState state) {
        return EnumCenterSlot.CENTER;
    }

    @Override
    public Block getBlock() {
        return RSBlocks.CABLE;
    }

    @Override
    public void onPartChanged(IPartInfo part, IPartInfo otherPart) {
        API.instance().discoverNode(part.getWorld(), part.getContainer().getPos());
    }
}

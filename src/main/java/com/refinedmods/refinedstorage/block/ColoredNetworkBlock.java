package com.refinedmods.refinedstorage.block;

import com.refinedmods.refinedstorage.util.BlockUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.DyeColor;
import net.minecraft.item.DyeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateContainer;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;

public class ColoredNetworkBlock extends NetworkNodeBlock {

    public ColoredNetworkBlock(Properties props) {
        super(props);
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        super.fillStateContainer(builder);
        builder.add(BlockUtils.COLOR_PROPERTY);
    }

    @Override
    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        ActionResultType result = super.onBlockActivated(state, worldIn, pos, player, handIn, hit);
        if (result != ActionResultType.PASS) {
            return result;
        }
        ItemStack stack = player.getHeldItem(handIn);
        if (stack.getItem() instanceof DyeItem) {
            DyeColor color = DyeColor.getColor(stack);
            if (color == null) {
                return ActionResultType.PASS;
            }
            if (state.get(BlockUtils.COLOR_PROPERTY) == color) {
                return ActionResultType.PASS;
            } else {
                if (!worldIn.isRemote) {
                    worldIn.setBlockState(pos, state.with(BlockUtils.COLOR_PROPERTY, color));
                }
                return ActionResultType.SUCCESS;
            }
        }
        return ActionResultType.PASS;
    }
}


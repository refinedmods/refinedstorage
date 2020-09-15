package com.refinedmods.refinedstorage.block;

import com.refinedmods.refinedstorage.util.BlockUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.StateContainer;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;

import java.util.List;

public class ColoredNetworkBlock extends NetworkNodeBlock {

    public static final String COLOR_NBT = "color";

    public ColoredNetworkBlock(Properties props) {
        super(props);
        setDefaultState(this.getDefaultState().with(BlockUtils.COLOR_PROPERTY, DyeColor.LIGHT_BLUE));
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
        return BlockUtils.changeBlockColor(state, player.getHeldItem(handIn), worldIn, pos, player);
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder) {
        List<ItemStack> stacks = super.getDrops(state, builder);
        stacks.forEach(stack -> {
            if (state.getBlock() instanceof ColoredNetworkBlock || state.getBlock() instanceof ControllerBlock) {
                CompoundNBT tag = stack.getOrCreateTag();
                tag.putInt(COLOR_NBT, state.get(BlockUtils.COLOR_PROPERTY).getId());
                stack.setTag(tag);
            }
        });
        return stacks;
    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        BlockState state = super.getStateForPlacement(context);
        if (state != null && context.getItem().hasTag() && context.getItem().getTag().contains(COLOR_NBT)) {
            DyeColor color = DyeColor.byId(context.getItem().getTag().getInt(COLOR_NBT));
            state = state.with(BlockUtils.COLOR_PROPERTY, color);
        }
        return state;
    }
}


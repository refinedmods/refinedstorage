package com.raoulvdberge.refinedstorage.block;

import com.raoulvdberge.refinedstorage.RSGui;
import com.raoulvdberge.refinedstorage.item.ItemBlockFluidStorage;
import com.raoulvdberge.refinedstorage.tile.TileFluidStorage;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class BlockFluidStorage extends BlockNode {
    public static final PropertyEnum TYPE = PropertyEnum.create("type", FluidStorageType.class);

    public BlockFluidStorage() {
        super("fluid_storage");

        setHardness(5.8F);
    }

    @Override
    public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> items) {
        for (int i = 0; i <= 4; ++i) {
            items.add(new ItemStack(this, 1, i));
        }
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return createBlockStateBuilder()
            .add(TYPE)
            .build();
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(TYPE, FluidStorageType.getById(meta));
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return ((FluidStorageType) state.getValue(TYPE)).getId();
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileFluidStorage();
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (!world.isRemote) {
            tryOpenNetworkGui(RSGui.FLUID_STORAGE, player, world, pos, side);
        }

        return true;
    }

    @Override
    public Item createItem() {
        return new ItemBlockFluidStorage();
    }

    @Override
    @Nullable
    public Direction getDirection() {
        return null;
    }
}

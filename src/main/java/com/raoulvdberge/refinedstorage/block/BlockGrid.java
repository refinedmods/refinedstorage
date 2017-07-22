package com.raoulvdberge.refinedstorage.block;

import com.raoulvdberge.refinedstorage.RSGui;
import com.raoulvdberge.refinedstorage.api.network.grid.GridType;
import com.raoulvdberge.refinedstorage.item.ItemBlockBase;
import com.raoulvdberge.refinedstorage.tile.grid.TileGrid;
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

public class BlockGrid extends BlockNode {
    public static final PropertyEnum TYPE = PropertyEnum.create("type", GridType.class);

    public BlockGrid() {
        super("grid");
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileGrid();
    }

    @Override
    public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> items) {
        for (int i = 0; i <= 3; i++) {
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
        return getDefaultState().withProperty(TYPE, meta == 0 ? GridType.NORMAL : (meta == 1 ? GridType.CRAFTING : (meta == 2 ? GridType.PATTERN : GridType.FLUID)));
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(TYPE) == GridType.NORMAL ? 0 : (state.getValue(TYPE) == GridType.CRAFTING ? 1 : (state.getValue(TYPE) == GridType.PATTERN ? 2 : 3));
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (!world.isRemote && tryOpenNetworkGui(RSGui.GRID, player, world, pos, side)) {
            ((TileGrid) world.getTileEntity(pos)).getNode().onOpened(player);
        }

        return true;
    }

    @Override
    public boolean hasConnectivityState() {
        return true;
    }

    @Override
    public Item createItem() {
        return new ItemBlockBase(this, getDirection(), true);
    }
}

package com.raoulvdberge.refinedstorage.block;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.RSGui;
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
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;

public class BlockGrid extends BlockNode {
    public static final PropertyEnum TYPE = PropertyEnum.create("type", EnumGridType.class);

    public BlockGrid() {
        super("grid");
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileGrid();
    }

    @Override
    public void getSubBlocks(Item item, CreativeTabs tab, List<ItemStack> subItems) {
        for (int i = 0; i <= 3; i++) {
            subItems.add(new ItemStack(item, 1, i));
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
        return getDefaultState().withProperty(TYPE, meta == 0 ? EnumGridType.NORMAL : (meta == 1 ? EnumGridType.CRAFTING : (meta == 2 ? EnumGridType.PATTERN : EnumGridType.FLUID)));
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(TYPE) == EnumGridType.NORMAL ? 0 : (state.getValue(TYPE) == EnumGridType.CRAFTING ? 1 : (state.getValue(TYPE) == EnumGridType.PATTERN ? 2 : 3));
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (!world.isRemote) {
            player.openGui(RS.INSTANCE, RSGui.GRID, world, pos.getX(), pos.getY(), pos.getZ());

            ((TileGrid) world.getTileEntity(pos)).onGridOpened(player);
        }

        return true;
    }

    @Override
    public boolean hasConnectivityState() {
        return true;
    }

    @Override
    public Item createItem() {
        return new ItemBlockBase(this, getPlacementType(), true);
    }
}

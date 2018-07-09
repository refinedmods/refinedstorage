package com.raoulvdberge.refinedstorage.block;

import com.raoulvdberge.refinedstorage.RSBlocks;
import com.raoulvdberge.refinedstorage.RSGui;
import com.raoulvdberge.refinedstorage.apiimpl.network.node.storage.NetworkNodeStorage;
import com.raoulvdberge.refinedstorage.block.enums.ItemStorageType;
import com.raoulvdberge.refinedstorage.block.info.BlockInfoBuilder;
import com.raoulvdberge.refinedstorage.item.ItemBlockStorage;
import com.raoulvdberge.refinedstorage.tile.TileStorage;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockStorage extends BlockNode {
    public static final PropertyEnum TYPE = PropertyEnum.create("type", ItemStorageType.class);

    public BlockStorage() {
        super(BlockInfoBuilder.forId("storage").tileEntity(TileStorage::new).hardness(5.8F).create());
    }

    @Override
    public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> items) {
        for (int i = 0; i <= 4; ++i) {
            items.add(new ItemStack(this, 1, i));
        }
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return createStateBuilder()
            .add(TYPE)
            .build();
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(TYPE, ItemStorageType.getById(meta));
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return ((ItemStorageType) state.getValue(TYPE)).getId();
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        return openNetworkGui(RSGui.STORAGE, player, world, pos, side);
    }

    @Override
    public Item createItem() {
        return new ItemBlockStorage(this, getDirection());
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase player, ItemStack stack) {
        if (!world.isRemote) {
            NetworkNodeStorage storage = ((TileStorage) world.getTileEntity(pos)).getNode();

            if (stack.hasTagCompound() && stack.getTagCompound().hasUniqueId(NetworkNodeStorage.NBT_ID)) {
                storage.setStorageId(stack.getTagCompound().getUniqueId(NetworkNodeStorage.NBT_ID));
            }

            storage.loadStorage();
        }

        // Call this after loading the storage, so the network discovery can use the loaded storage.
        super.onBlockPlacedBy(world, pos, state, player, stack);
    }

    @Override
    public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
        TileStorage storage = (TileStorage) world.getTileEntity(pos);

        ItemStack stack = new ItemStack(RSBlocks.STORAGE, 1, getMetaFromState(state));

        stack.setTagCompound(new NBTTagCompound());
        stack.getTagCompound().setUniqueId(NetworkNodeStorage.NBT_ID, storage.getNode().getStorageId());

        drops.add(stack);
    }
}

package com.raoulvdberge.refinedstorage.block;

import com.raoulvdberge.refinedstorage.block.enums.FluidStorageType;
import com.raoulvdberge.refinedstorage.block.info.BlockInfoBuilder;
import com.raoulvdberge.refinedstorage.tile.TileFluidStorage;
import net.minecraft.state.EnumProperty;

public class BlockFluidStorage extends BlockNode {
    public static final EnumProperty TYPE = EnumProperty.create("type", FluidStorageType.class);

    public BlockFluidStorage() {
        super(BlockInfoBuilder.forId("fluid_storage").hardness(5.8F).tileEntity(TileFluidStorage::new).create());
    }

    /*
    @Override
    @OnlyIn(Dist.CLIENT)
    public void registerModels(IModelRegistration modelRegistration) {
        modelRegistration.setModel(this, FluidStorageType.TYPE_64K.getId(), new ModelResourceLocation(info.getId(), "type=64k"));
        modelRegistration.setModel(this, FluidStorageType.TYPE_256K.getId(), new ModelResourceLocation(info.getId(), "type=256k"));
        modelRegistration.setModel(this, FluidStorageType.TYPE_1024K.getId(), new ModelResourceLocation(info.getId(), "type=1024k"));
        modelRegistration.setModel(this, FluidStorageType.TYPE_4096K.getId(), new ModelResourceLocation(info.getId(), "type=4096k"));
        modelRegistration.setModel(this, FluidStorageType.TYPE_CREATIVE.getId(), new ModelResourceLocation(info.getId(), "type=creative"));
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
    public BlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(TYPE, FluidStorageType.getById(meta));
    }

    @Override
    public int getMetaFromState(BlockState state) {
        return ((FluidStorageType) state.getValue(TYPE)).getId();
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, BlockState state, PlayerEntity player, EnumHand hand, Direction side, float hitX, float hitY, float hitZ) {
        return openNetworkGui(RSGui.FLUID_STORAGE, player, world, pos, side);
    }

    @Override
    public Item createItem() {
        return new ItemBlockFluidStorage(this);
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, BlockState state, EntityLivingBase player, ItemStack stack) {
        if (!world.isRemote) {
            NetworkNodeFluidStorage storage = ((TileFluidStorage) world.getTileEntity(pos)).getNode();

            if (stack.hasTagCompound() && stack.getTagCompound().hasUniqueId(NetworkNodeFluidStorage.NBT_ID)) {
                storage.setStorageId(stack.getTagCompound().getUniqueId(NetworkNodeFluidStorage.NBT_ID));
            }

            storage.loadStorage();
        }

        // Call this after loading the storage, so the network discovery can use the loaded storage.
        super.onBlockPlacedBy(world, pos, state, player, stack);
    }

    @Override
    public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, BlockState state, int fortune) {
        TileFluidStorage storage = (TileFluidStorage) world.getTileEntity(pos);

        ItemStack stack = new ItemStack(this, 1, getMetaFromState(state));

        stack.setTagCompound(new CompoundNBT());
        stack.getTagCompound().setUniqueId(NetworkNodeFluidStorage.NBT_ID, storage.getNode().getStorageId());

        drops.add(stack);
    }*/
}

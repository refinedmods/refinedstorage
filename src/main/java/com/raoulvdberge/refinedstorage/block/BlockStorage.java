package com.raoulvdberge.refinedstorage.block;

import com.raoulvdberge.refinedstorage.block.enums.ItemStorageType;
import com.raoulvdberge.refinedstorage.block.info.BlockInfoBuilder;
import com.raoulvdberge.refinedstorage.tile.TileStorage;
import net.minecraft.state.EnumProperty;

public class BlockStorage extends BlockNode {
    public static final EnumProperty TYPE = EnumProperty.create("type", ItemStorageType.class);

    public BlockStorage() {
        super(BlockInfoBuilder.forId("storage").tileEntity(TileStorage::new).hardness(5.8F).create());
    }

    /*
    @Override
    @OnlyIn(Dist.CLIENT)
    public void registerModels(IModelRegistration modelRegistration) {
        modelRegistration.setModel(this, ItemStorageType.TYPE_1K.getId(), new ModelResourceLocation(info.getId(), "type=1k"));
        modelRegistration.setModel(this, ItemStorageType.TYPE_4K.getId(), new ModelResourceLocation(info.getId(), "type=4k"));
        modelRegistration.setModel(this, ItemStorageType.TYPE_16K.getId(), new ModelResourceLocation(info.getId(), "type=16k"));
        modelRegistration.setModel(this, ItemStorageType.TYPE_64K.getId(), new ModelResourceLocation(info.getId(), "type=64k"));
        modelRegistration.setModel(this, ItemStorageType.TYPE_CREATIVE.getId(), new ModelResourceLocation(info.getId(), "type=creative"));
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
        return getDefaultState().withProperty(TYPE, ItemStorageType.getById(meta));
    }

    @Override
    public int getMetaFromState(BlockState state) {
        return ((ItemStorageType) state.getValue(TYPE)).getId();
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, BlockState state, PlayerEntity player, EnumHand hand, Direction side, float hitX, float hitY, float hitZ) {
        return openNetworkGui(RSGui.STORAGE, player, world, pos, side);
    }

    @Override
    public Item createItem() {
        return new ItemBlockStorage(this);
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, BlockState state, EntityLivingBase player, ItemStack stack) {
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
    public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, BlockState state, int fortune) {
        TileStorage storage = (TileStorage) world.getTileEntity(pos);

        ItemStack stack = new ItemStack(this, 1, getMetaFromState(state));

        stack.setTagCompound(new CompoundNBT());
        stack.getTagCompound().setUniqueId(NetworkNodeStorage.NBT_ID, storage.getNode().getStorageId());

        drops.add(stack);
    }*/
}

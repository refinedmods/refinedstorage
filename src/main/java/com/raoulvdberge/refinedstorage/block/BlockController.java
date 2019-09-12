package com.raoulvdberge.refinedstorage.block;

import com.raoulvdberge.refinedstorage.block.enums.ControllerEnergyType;
import com.raoulvdberge.refinedstorage.block.enums.ControllerType;
import com.raoulvdberge.refinedstorage.block.info.BlockInfoBuilder;
import com.raoulvdberge.refinedstorage.item.itemblock.ItemBlockController;
import com.raoulvdberge.refinedstorage.tile.TileController;
import net.minecraft.item.Item;
import net.minecraft.state.EnumProperty;
import net.minecraft.util.BlockRenderLayer;

public class BlockController extends BlockNodeProxy {
    public static final EnumProperty<ControllerType> TYPE = EnumProperty.create("type", ControllerType.class);
    public static final EnumProperty<ControllerEnergyType> ENERGY_TYPE = EnumProperty.create("energy_type", ControllerEnergyType.class);

    public BlockController() {
        super(BlockInfoBuilder.forId("controller").tileEntity(TileController::new).create());
    }

    @Override
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT;
    }

    /*
    @Override
    @OnlyIn(Dist.CLIENT)
    public void registerModels(IModelRegistration modelRegistration) {
        modelRegistration.setModelMeshDefinition(this, new ItemMeshDefinitionController());

        modelRegistration.setStateMapper(this, new StateMap.Builder().ignore(TYPE).build());

        modelRegistration.addBakedModelOverride(info.getId(), base -> new BakedModelFullbright(
            base,
            RS.ID + ":blocks/controller/cutouts/nearly_off",
            RS.ID + ":blocks/controller/cutouts/nearly_on",
            RS.ID + ":blocks/controller/cutouts/on"
        ));
    }

    @Override
    public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> items) {
        items.add(ItemBlockController.createStack(new ItemStack(this, 1, 0), 0));
        items.add(ItemBlockController.createStack(new ItemStack(this, 1, 0), RS.INSTANCE.config.controllerCapacity));
        items.add(ItemBlockController.createStack(new ItemStack(this, 1, 1), 0));
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return createBlockStateBuilder()
            .add(TYPE)
            .add(ENERGY_TYPE)
            .build();
    }

    @Override
    public BlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(TYPE, meta == 0 ? ControllerType.NORMAL : ControllerType.CREATIVE);
    }

    @Override
    public int getMetaFromState(BlockState state) {
        return state.getValue(TYPE) == ControllerType.NORMAL ? 0 : 1;
    }

    @Override
    public BlockState getActualState(BlockState state, IBlockAccess world, BlockPos pos) {
        return super.getActualState(state, world, pos)
            .withProperty(ENERGY_TYPE, ((TileController) world.getTileEntity(pos)).getEnergyType());
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, BlockState state, PlayerEntity player, EnumHand hand, Direction side, float hitX, float hitY, float hitZ) {
        return openNetworkGui(RSGui.CONTROLLER, player, world, pos, side);
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, BlockState state, EntityLivingBase player, ItemStack stack) {
        if (!world.isRemote) {
            TileController controller = (TileController) world.getTileEntity(pos);

            CompoundNBT tag = stack.getTagCompound();

            if (tag != null && tag.hasKey(TileController.NBT_ENERGY)) {
                controller.getEnergy().setStored(tag.getInteger(TileController.NBT_ENERGY));
            }
        }

        super.onBlockPlacedBy(world, pos, state, player, stack);
    }

    @Override
    public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, BlockState state, int fortune) {
        ItemStack stack = new ItemStack(this, 1, getMetaFromState(state));

        stack.setTagCompound(new CompoundNBT());
        stack.getTagCompound().putInt(TileController.NBT_ENERGY, ((TileController) world.getTileEntity(pos)).getEnergy().getStored());

        drops.add(stack);
    }*/

    @Override
    public Item createItem() {
        return new ItemBlockController(this);
    }
}

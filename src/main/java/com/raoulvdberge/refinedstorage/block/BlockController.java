package com.raoulvdberge.refinedstorage.block;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.RSGui;
import com.raoulvdberge.refinedstorage.block.enums.ControllerEnergyType;
import com.raoulvdberge.refinedstorage.block.enums.ControllerType;
import com.raoulvdberge.refinedstorage.block.info.BlockInfoBuilder;
import com.raoulvdberge.refinedstorage.item.itemblock.ItemBlockController;
import com.raoulvdberge.refinedstorage.render.IModelRegistration;
import com.raoulvdberge.refinedstorage.tile.TileController;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
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
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockController extends BlockNodeProxy {
    public static final PropertyEnum TYPE = PropertyEnum.create("type", ControllerType.class);
    public static final PropertyEnum ENERGY_TYPE = PropertyEnum.create("energy_type", ControllerEnergyType.class);

    public BlockController() {
        super(BlockInfoBuilder.forId("controller").tileEntity(TileController::new).create());
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerModels(IModelRegistration modelRegistration) {
        modelRegistration.setModelMeshDefinition(this, stack -> {
            ControllerEnergyType energyType = stack.getItemDamage() == ControllerType.CREATIVE.getId() ? ControllerEnergyType.ON : TileController.getEnergyType(ItemBlockController.getEnergyStored(stack), RS.INSTANCE.config.controllerCapacity);

            return new ModelResourceLocation(RS.ID + ":controller" + (Loader.isModLoaded("ctm") ? "_glow" : ""), "energy_type=" + energyType);
        });

        modelRegistration.setStateMapper(this, new StateMapperBase() {
            @Override
            protected ModelResourceLocation getModelResourceLocation(IBlockState state) {
                return new ModelResourceLocation(RS.ID + ":controller" + (Loader.isModLoaded("ctm") ? "_glow" : ""), "energy_type=" + state.getValue(ENERGY_TYPE));
            }
        });
    }

    @Override
    public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> items) {
        for (int i = 0; i <= 1; i++) {
            items.add(ItemBlockController.createStack(new ItemStack(this, 1, i)));
        }
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return createBlockStateBuilder()
            .add(TYPE)
            .add(ENERGY_TYPE)
            .build();
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(TYPE, meta == 0 ? ControllerType.NORMAL : ControllerType.CREATIVE);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(TYPE) == ControllerType.NORMAL ? 0 : 1;
    }

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos) {
        return super.getActualState(state, world, pos)
            .withProperty(ENERGY_TYPE, ((TileController) world.getTileEntity(pos)).getEnergyType());
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        return openNetworkGui(RSGui.CONTROLLER, player, world, pos, side);
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase player, ItemStack stack) {
        if (!world.isRemote) {
            TileController controller = (TileController) world.getTileEntity(pos);

            NBTTagCompound tag = stack.getTagCompound();

            if (tag != null && tag.hasKey(TileController.NBT_ENERGY)) {
                controller.getEnergy().setStored(tag.getInteger(TileController.NBT_ENERGY));
            }
        }

        super.onBlockPlacedBy(world, pos, state, player, stack);
    }

    @Override
    public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
        ItemStack stack = new ItemStack(this, 1, getMetaFromState(state));

        stack.setTagCompound(new NBTTagCompound());
        stack.getTagCompound().setInteger(TileController.NBT_ENERGY, ((TileController) world.getTileEntity(pos)).getEnergy().getStored());

        drops.add(stack);
    }

    @Override
    public Item createItem() {
        return new ItemBlockController(this);
    }
}

package com.raoulvdberge.refinedstorage.block;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.api.network.NetworkType;
import com.raoulvdberge.refinedstorage.container.ControllerContainer;
import com.raoulvdberge.refinedstorage.tile.ControllerTile;
import com.raoulvdberge.refinedstorage.util.BlockUtils;
import com.raoulvdberge.refinedstorage.util.NetworkUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;

public class ControllerBlock extends BaseBlock {
    public enum EnergyType implements IStringSerializable {
        OFF("off"),
        NEARLY_OFF("nearly_off"),
        NEARLY_ON("nearly_on"),
        ON("on");

        private String name;

        EnergyType(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    public static final EnumProperty<EnergyType> ENERGY_TYPE = EnumProperty.create("energy_type", EnergyType.class);

    private NetworkType type;

    public ControllerBlock(NetworkType type) {
        super(BlockUtils.DEFAULT_ROCK_PROPERTIES);

        this.type = type;
        this.setRegistryName(RS.ID, type == NetworkType.CREATIVE ? "creative_controller" : "controller");
        this.setDefaultState(getStateContainer().getBaseState().with(ENERGY_TYPE, EnergyType.OFF));
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        super.fillStateContainer(builder);

        builder.add(ENERGY_TYPE);
    }

    public NetworkType getType() {
        return type;
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new ControllerTile(type);
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, BlockState state, @Nullable LivingEntity entity, ItemStack stack) {
        super.onBlockPlacedBy(world, pos, state, entity, stack);

        if (!world.isRemote) {
            stack.getCapability(CapabilityEnergy.ENERGY).ifPresent(energyFromStack -> {
                TileEntity tile = world.getTileEntity(pos);

                if (tile != null) {
                    tile.getCapability(CapabilityEnergy.ENERGY).ifPresent(energyFromTile -> energyFromTile.receiveEnergy(energyFromStack.getEnergyStored(), false));
                }
            });
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
        if (!world.isRemote) {
            return NetworkUtils.attemptModify(world, pos, hit.getFace(), player, () -> NetworkHooks.openGui(
                (ServerPlayerEntity) player,
                new INamedContainerProvider() {
                    @Override
                    public ITextComponent getDisplayName() {
                        return new TranslationTextComponent("gui.refinedstorage." + (ControllerBlock.this.getType() == NetworkType.CREATIVE ? "creative_" : "") + "controller");
                    }

                    @Override
                    public Container createMenu(int i, PlayerInventory playerInventory, PlayerEntity player) {
                        return new ControllerContainer((ControllerTile) world.getTileEntity(pos), player, i);
                    }
                },
                pos
            ));
        }

        return true;
    }
}

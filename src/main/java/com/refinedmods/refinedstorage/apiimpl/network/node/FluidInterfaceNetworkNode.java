package com.refinedmods.refinedstorage.apiimpl.network.node;

import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.api.network.node.INetworkNode;
import com.refinedmods.refinedstorage.api.util.Action;
import com.refinedmods.refinedstorage.api.util.IComparer;
import com.refinedmods.refinedstorage.apiimpl.API;
import com.refinedmods.refinedstorage.apiimpl.storage.externalstorage.FluidExternalStorage;
import com.refinedmods.refinedstorage.inventory.fluid.FluidInventory;
import com.refinedmods.refinedstorage.inventory.fluid.ProxyFluidHandler;
import com.refinedmods.refinedstorage.inventory.item.BaseItemHandler;
import com.refinedmods.refinedstorage.inventory.item.UpgradeItemHandler;
import com.refinedmods.refinedstorage.inventory.listener.NetworkNodeFluidInventoryListener;
import com.refinedmods.refinedstorage.inventory.listener.NetworkNodeInventoryListener;
import com.refinedmods.refinedstorage.item.UpgradeItem;
import com.refinedmods.refinedstorage.tile.FluidInterfaceTile;
import com.refinedmods.refinedstorage.tile.config.IType;
import com.refinedmods.refinedstorage.util.StackUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;
import org.apache.commons.lang3.tuple.Pair;

public class FluidInterfaceNetworkNode extends NetworkNode {
    public static final ResourceLocation ID = new ResourceLocation(RS.ID, "fluid_interface");

    public static final int TANK_CAPACITY = 16_000;

    private static final String NBT_TANK_IN = "TankIn";
    private static final String NBT_TANK_OUT = "TankOut";
    private static final String NBT_OUT = "Out";

    private final FluidTank tankIn = new FluidTank(TANK_CAPACITY) {
        @Override
        protected void onContentsChanged() {
            super.onContentsChanged();

            if (!world.isClientSide) {
                ((FluidInterfaceTile) world.getBlockEntity(pos)).getDataManager().sendParameterToWatchers(FluidInterfaceTile.TANK_IN);
            }

            markDirty();
        }
    };
    private final FluidTank tankOut = new FluidTank(TANK_CAPACITY);

    private final ProxyFluidHandler tank = new ProxyFluidHandler(tankIn, tankOut);

    private final BaseItemHandler in = new BaseItemHandler(1).addListener(new NetworkNodeInventoryListener(this)).addValidator(stack -> !StackUtils.getFluid(stack, true).getValue().isEmpty());
    private final FluidInventory out = new FluidInventory(1, TANK_CAPACITY).addListener(new NetworkNodeFluidInventoryListener(this));

    private final UpgradeItemHandler upgrades = (UpgradeItemHandler) new UpgradeItemHandler(4, UpgradeItem.Type.SPEED, UpgradeItem.Type.STACK, UpgradeItem.Type.CRAFTING).addListener(new NetworkNodeInventoryListener(this));

    public FluidInterfaceNetworkNode(World world, BlockPos pos) {
        super(world, pos);
    }

    @Override
    public void update() {
        super.update();

        if (canUpdate()) {
            ItemStack container = in.getStackInSlot(0);

            if (!container.isEmpty()) {
                Pair<ItemStack, FluidStack> result = StackUtils.getFluid(container, true);

                if (!result.getValue().isEmpty() && tankIn.fill(result.getValue(), IFluidHandler.FluidAction.SIMULATE) == result.getValue().getAmount()) {
                    result = StackUtils.getFluid(container, false);

                    tankIn.fill(result.getValue(), IFluidHandler.FluidAction.EXECUTE);

                    in.setStackInSlot(0, result.getLeft());
                }
            }

            if (ticks % upgrades.getSpeed() == 0) {
                FluidStack drained = tankIn.drain(FluidAttributes.BUCKET_VOLUME * upgrades.getStackInteractCount(), IFluidHandler.FluidAction.EXECUTE);

                // Drain in tank
                if (!drained.isEmpty()) {
                    FluidStack remainder = network.insertFluidTracked(drained, drained.getAmount());

                    tankIn.fill(remainder, IFluidHandler.FluidAction.EXECUTE);
                }
            }

            FluidStack wanted = out.getFluid(0);
            FluidStack got = tankOut.getFluid();

            if (wanted.isEmpty()) {
                if (!got.isEmpty()) {
                    tankOut.setFluid(network.insertFluidTracked(got, got.getAmount()));

                    onTankOutChanged();
                }
            } else if (!got.isEmpty() && !API.instance().getComparer().isEqual(wanted, got, IComparer.COMPARE_NBT)) {
                tankOut.setFluid(network.insertFluidTracked(got, got.getAmount()));

                onTankOutChanged();
            } else {
                int delta = got.isEmpty() ? wanted.getAmount() : (wanted.getAmount() - got.getAmount());

                if (delta > 0) {
                    final boolean actingAsStorage = isActingAsStorage();

                    FluidStack result = network.extractFluid(wanted, delta, IComparer.COMPARE_NBT, Action.PERFORM, s -> {
                        // If we are not an interface acting as a storage, we can extract from anywhere.
                        if (!actingAsStorage) {
                            return true;
                        }

                        // If we are an interface acting as a storage, we don't want to extract from other interfaces to
                        // avoid stealing from each other.
                        return !(s instanceof FluidExternalStorage) || !((FluidExternalStorage) s).isConnectedToInterface();
                    });

                    if (!result.isEmpty()) {
                        if (tankOut.getFluid().isEmpty()) {
                            tankOut.setFluid(result);
                        } else {
                            tankOut.getFluid().grow(result.getAmount());
                        }

                        onTankOutChanged();
                    }

                    // Example: our delta is 5, we extracted 3 fluids.
                    // That means we still have to autocraft 2 fluids.
                    delta -= result.getAmount();

                    if (delta > 0 && upgrades.hasUpgrade(UpgradeItem.Type.CRAFTING)) {
                        network.getCraftingManager().request(this, wanted, delta);
                    }
                } else if (delta < 0) {
                    FluidStack remainder = network.insertFluidTracked(got, Math.abs(delta));

                    tankOut.getFluid().shrink(Math.abs(delta) - remainder.getAmount());

                    onTankOutChanged();
                }
            }
        }
    }

    private boolean isActingAsStorage() {
        for (Direction facing : Direction.values()) {
            INetworkNode facingNode = API.instance().getNetworkNodeManager((ServerWorld) world).getNode(pos.relative(facing));

            if (facingNode instanceof ExternalStorageNetworkNode &&
                facingNode.isActive() &&
                ((ExternalStorageNetworkNode) facingNode).getDirection() == facing.getOpposite() &&
                ((ExternalStorageNetworkNode) facingNode).getType() == IType.FLUIDS) {
                return true;
            }
        }

        return false;
    }

    @Override
    public int getEnergyUsage() {
        return RS.SERVER_CONFIG.getFluidInterface().getUsage();
    }

    @Override
    public CompoundNBT write(CompoundNBT tag) {
        super.write(tag);

        StackUtils.writeItems(upgrades, 0, tag);
        StackUtils.writeItems(in, 1, tag);

        tag.put(NBT_TANK_IN, tankIn.writeToNBT(new CompoundNBT()));
        tag.put(NBT_TANK_OUT, tankOut.writeToNBT(new CompoundNBT()));

        return tag;
    }

    @Override
    public void read(CompoundNBT tag) {
        super.read(tag);

        StackUtils.readItems(upgrades, 0, tag);
        StackUtils.readItems(in, 1, tag);

        if (tag.contains(NBT_TANK_IN)) {
            tankIn.readFromNBT(tag.getCompound(NBT_TANK_IN));
        }

        if (tag.contains(NBT_TANK_OUT)) {
            tankOut.readFromNBT(tag.getCompound(NBT_TANK_OUT));
        }
    }

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public CompoundNBT writeConfiguration(CompoundNBT tag) {
        super.writeConfiguration(tag);

        tag.put(NBT_OUT, out.writeToNbt());

        return tag;
    }

    @Override
    public void readConfiguration(CompoundNBT tag) {
        super.readConfiguration(tag);

        if (tag.contains(NBT_OUT)) {
            out.readFromNbt(tag.getCompound(NBT_OUT));
        }
    }

    public UpgradeItemHandler getUpgrades() {
        return upgrades;
    }

    public BaseItemHandler getIn() {
        return in;
    }

    public FluidInventory getOut() {
        return out;
    }

    public ProxyFluidHandler getTank() {
        return tank;
    }

    public FluidTank getTankIn() {
        return tankIn;
    }

    public FluidTank getTankOut() {
        return tankOut;
    }

    private void onTankOutChanged() {
        if (!world.isClientSide && world.isLoaded(pos)) {
            ((FluidInterfaceTile) world.getBlockEntity(pos)).getDataManager().sendParameterToWatchers(FluidInterfaceTile.TANK_OUT);
        }

        markDirty();
    }

    @Override
    public IItemHandler getDrops() {
        return new CombinedInvWrapper(in, upgrades);
    }
}

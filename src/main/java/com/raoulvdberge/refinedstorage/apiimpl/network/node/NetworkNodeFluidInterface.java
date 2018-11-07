package com.raoulvdberge.refinedstorage.apiimpl.network.node;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.api.network.node.INetworkNode;
import com.raoulvdberge.refinedstorage.api.util.Action;
import com.raoulvdberge.refinedstorage.api.util.IComparer;
import com.raoulvdberge.refinedstorage.apiimpl.API;
import com.raoulvdberge.refinedstorage.apiimpl.storage.externalstorage.StorageExternalFluid;
import com.raoulvdberge.refinedstorage.inventory.fluid.FluidHandlerProxy;
import com.raoulvdberge.refinedstorage.inventory.fluid.FluidInventory;
import com.raoulvdberge.refinedstorage.inventory.item.ItemHandlerBase;
import com.raoulvdberge.refinedstorage.inventory.item.ItemHandlerUpgrade;
import com.raoulvdberge.refinedstorage.inventory.listener.ListenerNetworkNode;
import com.raoulvdberge.refinedstorage.item.ItemUpgrade;
import com.raoulvdberge.refinedstorage.tile.TileFluidInterface;
import com.raoulvdberge.refinedstorage.tile.config.IType;
import com.raoulvdberge.refinedstorage.util.StackUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;
import org.apache.commons.lang3.tuple.Pair;

public class NetworkNodeFluidInterface extends NetworkNode {
    public static final String ID = "fluid_interface";

    public static final int TANK_CAPACITY = 16_000;

    private static final String NBT_TANK_IN = "TankIn";
    private static final String NBT_TANK_OUT = "TankOut";
    private static final String NBT_OUT = "Out";

    private FluidTank tankIn = new FluidTank(TANK_CAPACITY) {
        @Override
        protected void onContentsChanged() {
            super.onContentsChanged();

            if (!world.isRemote) {
                ((TileFluidInterface) world.getTileEntity(pos)).getDataManager().sendParameterToWatchers(TileFluidInterface.TANK_IN);
            }

            markDirty();
        }
    };
    private FluidTank tankOut = new FluidTank(TANK_CAPACITY);

    private FluidHandlerProxy tank = new FluidHandlerProxy(tankIn, tankOut);

    private ItemHandlerBase in = new ItemHandlerBase(1, new ListenerNetworkNode(this), stack -> StackUtils.getFluid(stack, true).getRight() != null);
    private FluidInventory out = new FluidInventory(1, TANK_CAPACITY, new ListenerNetworkNode(this));

    private ItemHandlerUpgrade upgrades = new ItemHandlerUpgrade(4, new ListenerNetworkNode(this), ItemUpgrade.TYPE_SPEED, ItemUpgrade.TYPE_STACK, ItemUpgrade.TYPE_CRAFTING);

    public NetworkNodeFluidInterface(World world, BlockPos pos) {
        super(world, pos);

        tankIn.setCanDrain(false);
        tankIn.setCanFill(true);

        tankOut.setCanDrain(true);
        tankOut.setCanFill(false);
    }

    @Override
    public void update() {
        super.update();

        if (canUpdate()) {
            ItemStack container = in.getStackInSlot(0);

            if (!container.isEmpty()) {
                Pair<ItemStack, FluidStack> result = StackUtils.getFluid(container, true);

                if (result.getValue() != null && tankIn.fillInternal(result.getValue(), false) == result.getValue().amount) {
                    result = StackUtils.getFluid(container, false);

                    tankIn.fillInternal(result.getValue(), true);

                    in.setStackInSlot(0, result.getLeft());
                }
            }

            if (ticks % upgrades.getSpeed() == 0) {
                FluidStack drained = tankIn.drainInternal(Fluid.BUCKET_VOLUME * upgrades.getItemInteractCount(), true);

                // Drain in tank
                if (drained != null) {
                    FluidStack remainder = network.insertFluidTracked(drained, drained.amount);

                    if (remainder != null) {
                        tankIn.fillInternal(remainder, true);
                    }
                }
            }

            FluidStack wanted = out.getFluid(0);
            FluidStack got = tankOut.getFluid();

            if (wanted == null) {
                if (got != null) {
                    tankOut.setFluid(network.insertFluidTracked(got, got.amount));

                    onTankOutChanged();
                }
            } else if (got != null && !API.instance().getComparer().isEqual(wanted, got, IComparer.COMPARE_NBT)) {
                tankOut.setFluid(network.insertFluidTracked(got, got.amount));

                onTankOutChanged();
            } else {
                int delta = got == null ? wanted.amount : (wanted.amount - got.amount);

                if (delta > 0) {
                    final boolean actingAsStorage = isActingAsStorage();

                    FluidStack result = network.extractFluid(wanted, delta, IComparer.COMPARE_NBT, Action.PERFORM, s -> {
                        // If we are not an interface acting as a storage, we can extract from anywhere.
                        if (!actingAsStorage) {
                            return true;
                        }

                        // If we are an interface acting as a storage, we don't want to extract from other interfaces to
                        // avoid stealing from each other.
                        return !(s instanceof StorageExternalFluid) || !((StorageExternalFluid) s).isConnectedToInterface();
                    });

                    if (result != null) {
                        if (tankOut.getFluid() == null) {
                            tankOut.setFluid(result);
                        } else {
                            tankOut.getFluid().amount += result.amount;
                        }

                        onTankOutChanged();
                    }

                    // Example: our delta is 5, we extracted 3 fluids.
                    // That means we still have to autocraft 2 fluids.
                    delta -= result == null ? 0 : result.amount;

                    if (delta > 0 && upgrades.hasUpgrade(ItemUpgrade.TYPE_CRAFTING)) {
                        network.getCraftingManager().request(this, wanted, delta);
                    }
                } else if (delta < 0) {
                    FluidStack remainder = network.insertFluidTracked(got, Math.abs(delta));

                    if (remainder == null) {
                        tankOut.getFluid().amount -= Math.abs(delta);
                    } else {
                        tankOut.getFluid().amount -= Math.abs(delta) - remainder.amount;
                    }

                    onTankOutChanged();
                }
            }
        }
    }

    private boolean isActingAsStorage() {
        for (EnumFacing facing : EnumFacing.VALUES) {
            INetworkNode facingNode = API.instance().getNetworkNodeManager(world).getNode(pos.offset(facing));

            if (facingNode instanceof NetworkNodeExternalStorage &&
                facingNode.canUpdate() &&
                ((NetworkNodeExternalStorage) facingNode).getDirection() == facing.getOpposite() &&
                ((NetworkNodeExternalStorage) facingNode).getType() == IType.FLUIDS) {
                return true;
            }
        }

        return false;
    }

    @Override
    public int getEnergyUsage() {
        return RS.INSTANCE.config.fluidInterfaceUsage;
    }

    @Override
    public NBTTagCompound write(NBTTagCompound tag) {
        super.write(tag);

        StackUtils.writeItems(upgrades, 0, tag);
        StackUtils.writeItems(in, 1, tag);

        tag.setTag(NBT_TANK_IN, tankIn.writeToNBT(new NBTTagCompound()));
        tag.setTag(NBT_TANK_OUT, tankOut.writeToNBT(new NBTTagCompound()));

        return tag;
    }

    @Override
    public void read(NBTTagCompound tag) {
        super.read(tag);

        StackUtils.readItems(upgrades, 0, tag);
        StackUtils.readItems(in, 1, tag);

        if (tag.hasKey(NBT_TANK_IN)) {
            tankIn.readFromNBT(tag.getCompoundTag(NBT_TANK_IN));
        }

        if (tag.hasKey(NBT_TANK_OUT)) {
            tankOut.readFromNBT(tag.getCompoundTag(NBT_TANK_OUT));
        }
    }

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public NBTTagCompound writeConfiguration(NBTTagCompound tag) {
        super.writeConfiguration(tag);

        tag.setTag(NBT_OUT, out.writeToNbt());

        return tag;
    }

    @Override
    public void readConfiguration(NBTTagCompound tag) {
        super.readConfiguration(tag);

        if (tag.hasKey(NBT_OUT)) {
            out.readFromNbt(tag.getCompoundTag(NBT_OUT));
        }
    }

    public ItemHandlerUpgrade getUpgrades() {
        return upgrades;
    }

    public ItemHandlerBase getIn() {
        return in;
    }

    public FluidInventory getOut() {
        return out;
    }

    public FluidHandlerProxy getTank() {
        return tank;
    }

    public FluidTank getTankIn() {
        return tankIn;
    }

    public FluidTank getTankOut() {
        return tankOut;
    }

    private void onTankOutChanged() {
        if (!world.isRemote) {
            ((TileFluidInterface) world.getTileEntity(pos)).getDataManager().sendParameterToWatchers(TileFluidInterface.TANK_OUT);
        }

        markDirty();
    }

    @Override
    public IItemHandler getDrops() {
        return new CombinedInvWrapper(in, upgrades);
    }

    @Override
    public boolean hasConnectivityState() {
        return true;
    }
}

package refinedstorage.tile;

import mcmultipart.microblock.IMicroblock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidBlock;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.wrappers.BlockLiquidWrapper;
import net.minecraftforge.fluids.capability.wrappers.FluidBlockWrapper;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import refinedstorage.RefinedStorage;
import refinedstorage.inventory.ItemHandlerFluid;
import refinedstorage.inventory.ItemHandlerUpgrade;
import refinedstorage.item.ItemUpgrade;
import refinedstorage.tile.config.IComparable;
import refinedstorage.tile.config.IFilterable;
import refinedstorage.tile.data.TileDataParameter;

public class TileFluidDestructor extends TileMultipartNode implements IComparable, IFilterable {
    public static final TileDataParameter<Integer> COMPARE = IComparable.createParameter();
    public static final TileDataParameter<Integer> MODE = IFilterable.createParameter();

    private static final String NBT_COMPARE = "Compare";
    private static final String NBT_MODE = "Mode";

    private static final int BASE_SPEED = 20;

    private ItemHandlerFluid filters = new ItemHandlerFluid(9, this);
    private ItemHandlerUpgrade upgrades = new ItemHandlerUpgrade(4, this, ItemUpgrade.TYPE_SPEED);

    private int compare = 0;
    private int mode = IFilterable.WHITELIST;

    public TileFluidDestructor() {
        dataManager.addWatchedParameter(COMPARE);
        dataManager.addWatchedParameter(MODE);
    }

    @Override
    public boolean canAddMicroblock(IMicroblock microblock) {
        return !isBlockingMicroblock(microblock, getDirection());
    }

    @Override
    public int getEnergyUsage() {
        return RefinedStorage.INSTANCE.fluidDestructorUsage + upgrades.getEnergyUsage();
    }

    @Override
    public void updateNode() {
        if (ticks % upgrades.getSpeed(BASE_SPEED, 4) == 0) {
            BlockPos front = pos.offset(getDirection());

            Block frontBlock = worldObj.getBlockState(front).getBlock();

            IFluidHandler handler = null;

            if (frontBlock instanceof BlockLiquid) {
                handler = new BlockLiquidWrapper((BlockLiquid) frontBlock, worldObj, front);
            } else if (frontBlock instanceof IFluidBlock) {
                handler = new FluidBlockWrapper((IFluidBlock) frontBlock, worldObj, front);
            }

            if (handler != null) {
                FluidStack stack = handler.drain(Fluid.BUCKET_VOLUME, false);

                if (IFilterable.canTakeFluids(filters, mode, compare, stack) && network.insertFluid(stack, stack.amount, true) == null) {
                    FluidStack drained = handler.drain(Fluid.BUCKET_VOLUME, true);

                    network.insertFluid(drained, drained.amount, false);
                }
            }
        }
    }

    @Override
    public int getCompare() {
        return compare;
    }

    @Override
    public void setCompare(int compare) {
        this.compare = compare;

        markDirty();
    }

    @Override
    public int getMode() {
        return mode;
    }

    @Override
    public void setMode(int mode) {
        this.mode = mode;

        markDirty();
    }

    @Override
    public void read(NBTTagCompound tag) {
        super.read(tag);

        if (tag.hasKey(NBT_COMPARE)) {
            compare = tag.getInteger(NBT_COMPARE);
        }

        if (tag.hasKey(NBT_MODE)) {
            mode = tag.getInteger(NBT_MODE);
        }

        readItems(filters, 0, tag);
        readItems(upgrades, 1, tag);
    }

    @Override
    public NBTTagCompound write(NBTTagCompound tag) {
        super.write(tag);

        tag.setInteger(NBT_COMPARE, compare);
        tag.setInteger(NBT_MODE, mode);

        writeItems(filters, 0, tag);
        writeItems(upgrades, 1, tag);

        return tag;
    }

    public IItemHandler getUpgrades() {
        return upgrades;
    }

    public IItemHandler getInventory() {
        return filters;
    }

    @Override
    public IItemHandler getDrops() {
        return upgrades;
    }

    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return (T) upgrades;
        }

        return super.getCapability(capability, facing);
    }

    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
        return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY || super.hasCapability(capability, facing);
    }
}

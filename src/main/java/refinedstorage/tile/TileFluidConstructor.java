package refinedstorage.tile;

import mcmultipart.microblock.IMicroblock;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import refinedstorage.RefinedStorage;
import refinedstorage.inventory.ItemHandlerFluid;
import refinedstorage.inventory.ItemHandlerUpgrade;
import refinedstorage.item.ItemUpgrade;
import refinedstorage.tile.config.IComparable;
import refinedstorage.tile.data.TileDataParameter;

public class TileFluidConstructor extends TileMultipartNode implements IComparable {
    public static final TileDataParameter<Integer> COMPARE = IComparable.createParameter();

    private static final String NBT_COMPARE = "Compare";

    private static final int BASE_SPEED = 20;

    private ItemHandlerFluid filter = new ItemHandlerFluid(1, this);

    private ItemHandlerUpgrade upgrades = new ItemHandlerUpgrade(4, this, ItemUpgrade.TYPE_SPEED);

    private int compare = 0;

    public TileFluidConstructor() {
        dataManager.addWatchedParameter(COMPARE);
    }

    @Override
    public boolean canAddMicroblock(IMicroblock microblock) {
        return !isBlockingMicroblock(microblock, getDirection());
    }

    @Override
    public int getEnergyUsage() {
        return RefinedStorage.INSTANCE.fluidConstructorUsage + upgrades.getEnergyUsage();
    }

    @Override
    public void updateNode() {
        FluidStack stack = filter.getFluids()[0];

        if (stack != null && ticks % upgrades.getSpeed(BASE_SPEED, 4) == 0) {
            BlockPos front = pos.offset(getDirection());

            Block block = stack.getFluid().getBlock();

            if (worldObj.isAirBlock(front) && block.canPlaceBlockAt(worldObj, front)) {
                FluidStack took = network.extractFluid(stack, Fluid.BUCKET_VOLUME, compare);

                if (took != null) {
                    IBlockState state = block.getDefaultState();

                    // @TODO: This doesn't cause the block to flow?
                    worldObj.setBlockState(front, state, 1 | 2);
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
    public void read(NBTTagCompound tag) {
        super.read(tag);

        if (tag.hasKey(NBT_COMPARE)) {
            compare = tag.getInteger(NBT_COMPARE);
        }

        readItems(filter, 0, tag);
        readItems(upgrades, 1, tag);
    }

    @Override
    public NBTTagCompound write(NBTTagCompound tag) {
        super.write(tag);

        tag.setInteger(NBT_COMPARE, compare);

        writeItems(filter, 0, tag);
        writeItems(upgrades, 1, tag);

        return tag;
    }

    public IItemHandler getUpgrades() {
        return upgrades;
    }

    public IItemHandler getFilter() {
        return filter;
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

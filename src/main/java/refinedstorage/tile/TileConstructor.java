package refinedstorage.tile;

import mcmultipart.microblock.IMicroblock;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import refinedstorage.RS;
import refinedstorage.api.network.NetworkUtils;
import refinedstorage.api.storage.CompareUtils;
import refinedstorage.container.slot.SlotSpecimen;
import refinedstorage.inventory.ItemHandlerBasic;
import refinedstorage.inventory.ItemHandlerFluid;
import refinedstorage.inventory.ItemHandlerUpgrade;
import refinedstorage.item.ItemUpgrade;
import refinedstorage.tile.config.IComparable;
import refinedstorage.tile.config.IType;
import refinedstorage.tile.data.TileDataParameter;

public class TileConstructor extends TileMultipartNode implements IComparable, IType {
    public static final TileDataParameter<Integer> COMPARE = IComparable.createParameter();
    public static final TileDataParameter<Integer> TYPE = IType.createParameter();

    private static final String NBT_COMPARE = "Compare";
    private static final String NBT_TYPE = "Type";

    private static final int BASE_SPEED = 20;

    private ItemHandlerBasic itemFilters = new ItemHandlerBasic(1, this) {
        @Override
        protected void onContentsChanged(int slot) {
            super.onContentsChanged(slot);

            block = SlotSpecimen.getBlockState(worldObj, pos.offset(getDirection()), getStackInSlot(0));
        }
    };

    private ItemHandlerFluid fluidFilters = new ItemHandlerFluid(1, this);

    private ItemHandlerUpgrade upgrades = new ItemHandlerUpgrade(4, this, ItemUpgrade.TYPE_SPEED, ItemUpgrade.TYPE_CRAFTING);

    private int compare = CompareUtils.COMPARE_NBT | CompareUtils.COMPARE_DAMAGE;
    private int type = IType.ITEMS;

    private IBlockState block;

    public TileConstructor() {
        dataManager.addWatchedParameter(COMPARE);
        dataManager.addWatchedParameter(TYPE);
    }

    @Override
    public boolean canAddMicroblock(IMicroblock microblock) {
        return !isBlockingMicroblock(microblock, getDirection());
    }

    @Override
    public int getEnergyUsage() {
        return RS.INSTANCE.config.constructorUsage + upgrades.getEnergyUsage();
    }

    @Override
    public void updateNode() {
        if (ticks % upgrades.getSpeed(BASE_SPEED, 4) == 0) {
            if (type == IType.ITEMS && block != null) {
                BlockPos front = pos.offset(getDirection());

                if (worldObj.isAirBlock(front) && block.getBlock().canPlaceBlockAt(worldObj, front)) {
                    ItemStack took = network.extractItem(itemFilters.getStackInSlot(0), 1, compare);

                    if (took != null) {
                        @SuppressWarnings("deprecation")
                        IBlockState state = block.getBlock().getStateFromMeta(took.getMetadata());

                        worldObj.setBlockState(front, state, 1 | 2);

                        // From ItemBlock.onItemUse
                        SoundType blockSound = block.getBlock().getSoundType(state, worldObj, pos, null);
                        worldObj.playSound(null, front, blockSound.getPlaceSound(), SoundCategory.BLOCKS, (blockSound.getVolume() + 1.0F) / 2.0F, blockSound.getPitch() * 0.8F);
                    } else if (upgrades.hasUpgrade(ItemUpgrade.TYPE_CRAFTING)) {
                        ItemStack craft = itemFilters.getStackInSlot(0);

                        NetworkUtils.scheduleCraftingTaskIfUnscheduled(network, craft, 1, compare);
                    }
                }
            } else if (type == IType.FLUIDS) {
                FluidStack stack = fluidFilters.getFluidStackInSlot(0);

                if (stack != null && stack.getFluid().canBePlacedInWorld()) {
                    BlockPos front = pos.offset(getDirection());

                    Block block = stack.getFluid().getBlock();

                    if (worldObj.isAirBlock(front) && block.canPlaceBlockAt(worldObj, front)) {
                        FluidStack took = network.extractFluid(stack, Fluid.BUCKET_VOLUME, compare);

                        if (took != null) {
                            IBlockState state = block.getDefaultState();

                            if (state.getBlock() == Blocks.WATER) {
                                state = Blocks.FLOWING_WATER.getDefaultState();
                            } else if (state.getBlock() == Blocks.LAVA) {
                                state = Blocks.FLOWING_LAVA.getDefaultState();
                            }

                            worldObj.setBlockState(front, state, 1 | 2);
                        }
                    }
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

        if (tag.hasKey(NBT_TYPE)) {
            type = tag.getInteger(NBT_TYPE);
        }

        readItems(itemFilters, 0, tag);
        readItems(upgrades, 1, tag);
        readItems(fluidFilters, 2, tag);
    }

    @Override
    public NBTTagCompound write(NBTTagCompound tag) {
        super.write(tag);

        tag.setInteger(NBT_COMPARE, compare);
        tag.setInteger(NBT_TYPE, type);

        writeItems(itemFilters, 0, tag);
        writeItems(upgrades, 1, tag);
        writeItems(fluidFilters, 2, tag);

        return tag;
    }

    public IItemHandler getUpgrades() {
        return upgrades;
    }

    @Override
    public IItemHandler getDrops() {
        return upgrades;
    }

    @Override
    public boolean hasConnectivityState() {
        return true;
    }

    @Override
    public int getType() {
        return worldObj.isRemote ? TYPE.getValue() : type;
    }

    @Override
    public void setType(int type) {
        this.type = type;

        markDirty();
    }

    @Override
    public IItemHandler getFilterInventory() {
        return getType() == IType.ITEMS ? itemFilters : fluidFilters;
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

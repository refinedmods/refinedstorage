package refinedstorage.tile;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import refinedstorage.RefinedStorage;
import refinedstorage.inventory.ItemHandlerBasic;
import refinedstorage.inventory.ItemHandlerUpgrade;
import refinedstorage.item.ItemUpgrade;
import refinedstorage.tile.config.IComparable;
import refinedstorage.tile.config.IFilterable;
import refinedstorage.tile.data.TileDataParameter;

import java.util.List;

public class TileDestructor extends TileNode implements IComparable, IFilterable {
    public static final TileDataParameter<Integer> COMPARE = IComparable.createParameter();
    public static final TileDataParameter<Integer> MODE = IFilterable.createParameter();

    private static final String NBT_COMPARE = "Compare";
    private static final String NBT_MODE = "Mode";

    private static final int BASE_SPEED = 20;

    private ItemHandlerBasic filters = new ItemHandlerBasic(9, this);
    private ItemHandlerUpgrade upgrades = new ItemHandlerUpgrade(4, this, ItemUpgrade.TYPE_SPEED);

    private int compare = 0;
    private int mode = IFilterable.WHITELIST;

    public TileDestructor() {
        dataManager.addWatchedParameter(COMPARE);
        dataManager.addWatchedParameter(MODE);
    }

    @Override
    public int getEnergyUsage() {
        return RefinedStorage.INSTANCE.destructorUsage + upgrades.getEnergyUsage();
    }

    @Override
    public void updateNode() {
        if (ticks % upgrades.getSpeed(BASE_SPEED, 4) == 0) {
            BlockPos front = pos.offset(getDirection());

            IBlockState frontBlockState = worldObj.getBlockState(front);
            ItemStack frontStack = frontBlockState.getBlock().getItem(worldObj, front, frontBlockState);

            if (frontStack != null) {
                if (IFilterable.canTake(filters, mode, compare, frontStack)) {
                    List<ItemStack> drops = frontBlockState.getBlock().getDrops(worldObj, front, frontBlockState, 0);

                    worldObj.playEvent(null, 2001, front, Block.getStateId(frontBlockState));
                    worldObj.setBlockToAir(front);

                    for (ItemStack drop : drops) {
                        // We check if the controller isn't null here because when a destructor faces a node and removes it
                        // it will essentially remove this block itself from the network without knowing
                        if (network == null) {
                            InventoryHelper.spawnItemStack(worldObj, front.getX(), front.getY(), front.getZ(), drop);
                        } else {
                            ItemStack remainder = network.insertItem(drop, drop.stackSize, false);

                            if (remainder != null) {
                                InventoryHelper.spawnItemStack(worldObj, front.getX(), front.getY(), front.getZ(), remainder);
                            }
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
    public IItemHandler getDroppedItems() {
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

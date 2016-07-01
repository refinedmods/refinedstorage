package refinedstorage.tile;

import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import refinedstorage.RefinedStorage;
import refinedstorage.RefinedStorageItems;
import refinedstorage.RefinedStorageUtils;
import refinedstorage.container.ContainerDestructor;
import refinedstorage.inventory.BasicItemHandler;
import refinedstorage.inventory.BasicItemValidator;
import refinedstorage.item.ItemUpgrade;
import refinedstorage.tile.config.ICompareConfig;
import refinedstorage.tile.config.IModeConfig;
import refinedstorage.tile.config.ModeConstants;
import refinedstorage.tile.config.ModeFilter;

import java.util.List;

public class TileDestructor extends TileSlave implements ICompareConfig, IModeConfig {
    public static final String NBT_COMPARE = "Compare";
    public static final String NBT_MODE = "Mode";

    public static final int BASE_SPEED = 20;

    private BasicItemHandler filters = new BasicItemHandler(9, this);
    private BasicItemHandler upgrades = new BasicItemHandler(
        4,
        this,
        new BasicItemValidator(RefinedStorageItems.UPGRADE, ItemUpgrade.TYPE_SPEED)
    );

    private int compare = 0;
    private int mode = ModeConstants.WHITELIST;

    @Override
    public int getEnergyUsage() {
        return RefinedStorage.INSTANCE.destructorRfUsage + RefinedStorageUtils.getUpgradeEnergyUsage(upgrades);
    }

    @Override
    public void updateSlave() {
        if (ticks % RefinedStorageUtils.getSpeed(upgrades, BASE_SPEED, 4) == 0) {
            BlockPos front = pos.offset(getDirection());

            IBlockState frontBlockState = worldObj.getBlockState(front);
            ItemStack frontStack = frontBlockState.getBlock().getItem(worldObj, front, frontBlockState);

            if (frontStack != null) {
                if (ModeFilter.respectsMode(filters, this, compare, frontStack)) {
                    List<ItemStack> drops = frontBlockState.getBlock().getDrops(worldObj, front, frontBlockState, 0);

                    worldObj.playEvent(null, 2001, front, Block.getStateId(frontBlockState));
                    worldObj.setBlockToAir(front);

                    for (ItemStack drop : drops) {
                        // We check if the controller isn't null here because when a destructor faces a slave block and removes it
                        // it will essentially remove this block itself from the network without knowing
                        if (network == null) {
                            InventoryHelper.spawnItemStack(worldObj, front.getX(), front.getY(), front.getZ(), drop);
                        } else {
                            ItemStack remainder = network.push(drop, drop.stackSize, false);

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
    public void read(NBTTagCompound nbt) {
        super.read(nbt);

        if (nbt.hasKey(NBT_COMPARE)) {
            compare = nbt.getInteger(NBT_COMPARE);
        }

        if (nbt.hasKey(NBT_MODE)) {
            mode = nbt.getInteger(NBT_MODE);
        }

        RefinedStorageUtils.readItems(filters, 0, nbt);
        RefinedStorageUtils.readItems(upgrades, 1, nbt);
    }

    @Override
    public NBTTagCompound write(NBTTagCompound tag) {
        super.write(tag);

        tag.setInteger(NBT_COMPARE, compare);
        tag.setInteger(NBT_MODE, mode);

        RefinedStorageUtils.writeItems(filters, 0, tag);
        RefinedStorageUtils.writeItems(upgrades, 1, tag);

        return tag;
    }

    @Override
    public void readContainerData(ByteBuf buf) {
        super.readContainerData(buf);

        compare = buf.readInt();
        mode = buf.readInt();
    }

    @Override
    public void writeContainerData(ByteBuf buf) {
        super.writeContainerData(buf);

        buf.writeInt(compare);
        buf.writeInt(mode);
    }

    public Class<? extends Container> getContainer() {
        return ContainerDestructor.class;
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

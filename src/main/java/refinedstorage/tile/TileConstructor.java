package refinedstorage.tile;

import io.netty.buffer.ByteBuf;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import refinedstorage.RefinedStorage;
import refinedstorage.RefinedStorageUtils;
import refinedstorage.apiimpl.autocrafting.CraftingTaskScheduler;
import refinedstorage.container.ContainerConstructor;
import refinedstorage.container.slot.SlotSpecimen;
import refinedstorage.inventory.BasicItemHandler;
import refinedstorage.inventory.UpgradeItemHandler;
import refinedstorage.item.ItemUpgrade;
import refinedstorage.tile.config.ICompareConfig;

public class TileConstructor extends TileNode implements ICompareConfig {
    private static final String NBT_COMPARE = "Compare";

    private static final int BASE_SPEED = 20;

    private BasicItemHandler filter = new BasicItemHandler(1, this) {
        @Override
        protected void onContentsChanged(int slot) {
            super.onContentsChanged(slot);

            block = SlotSpecimen.getBlockState(worldObj, pos.offset(getDirection()), getStackInSlot(0));
        }
    };
    private UpgradeItemHandler upgrades = new UpgradeItemHandler(4, this, ItemUpgrade.TYPE_SPEED, ItemUpgrade.TYPE_CRAFTING);

    private int compare = 0;
    private IBlockState block;

    private CraftingTaskScheduler scheduler = new CraftingTaskScheduler(this);

    @Override
    public int getEnergyUsage() {
        return RefinedStorage.INSTANCE.constructorUsage + upgrades.getEnergyUsage();
    }

    @Override
    public void updateNode() {
        if (block != null && ticks % upgrades.getSpeed(BASE_SPEED, 4) == 0) {
            BlockPos front = pos.offset(getDirection());

            if (worldObj.isAirBlock(front) && block.getBlock().canPlaceBlockAt(worldObj, front)) {
                ItemStack took = network.extractItem(filter.getStackInSlot(0), 1, compare);

                if (took != null) {
                    scheduler.resetSchedule();
                    worldObj.setBlockState(front, block.getBlock().getStateFromMeta(took.getMetadata()), 1 | 2);
                    // From ItemBlock.onItemUse
                    SoundType blockSound = block.getBlock().getSoundType();
                    worldObj.playSound(null, front, blockSound.getPlaceSound(), SoundCategory.BLOCKS, (blockSound.getVolume() + 1.0F) / 2.0F, blockSound.getPitch() * 0.8F);
                } else if (upgrades.hasUpgrade(ItemUpgrade.TYPE_CRAFTING)) {
                    ItemStack craft = filter.getStackInSlot(0);

                    if (scheduler.canSchedule(compare, craft)) {
                        scheduler.schedule(network, compare, craft);
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

        RefinedStorageUtils.readItems(filter, 0, tag);
        RefinedStorageUtils.readItems(upgrades, 1, tag);

        scheduler.read(tag);
    }

    @Override
    public NBTTagCompound write(NBTTagCompound tag) {
        super.write(tag);

        tag.setInteger(NBT_COMPARE, compare);

        RefinedStorageUtils.writeItems(filter, 0, tag);
        RefinedStorageUtils.writeItems(upgrades, 1, tag);

        scheduler.writeToNBT(tag);

        return tag;
    }

    @Override
    public void readContainerData(ByteBuf buf) {
        super.readContainerData(buf);

        compare = buf.readInt();
    }

    @Override
    public void writeContainerData(ByteBuf buf) {
        super.writeContainerData(buf);

        buf.writeInt(compare);
    }

    @Override
    public Class<? extends Container> getContainer() {
        return ContainerConstructor.class;
    }

    public IItemHandler getUpgrades() {
        return upgrades;
    }

    public IItemHandler getFilter() {
        return filter;
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

package refinedstorage.tile;

import mcmultipart.microblock.IMicroblock;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import refinedstorage.RefinedStorage;
import refinedstorage.apiimpl.autocrafting.CraftingTaskScheduler;
import refinedstorage.container.slot.SlotSpecimen;
import refinedstorage.inventory.ItemHandlerBasic;
import refinedstorage.inventory.ItemHandlerUpgrade;
import refinedstorage.item.ItemUpgrade;
import refinedstorage.tile.config.IComparable;
import refinedstorage.tile.data.TileDataParameter;

public class TileConstructor extends TileMultipartNode implements IComparable {
    public static final TileDataParameter<Integer> COMPARE = IComparable.createParameter();

    private static final String NBT_COMPARE = "Compare";

    private static final int BASE_SPEED = 20;

    private ItemHandlerBasic filter = new ItemHandlerBasic(1, this) {
        @Override
        protected void onContentsChanged(int slot) {
            super.onContentsChanged(slot);

            block = SlotSpecimen.getBlockState(worldObj, pos.offset(getDirection()), getStackInSlot(0));
        }
    };

    private ItemHandlerUpgrade upgrades = new ItemHandlerUpgrade(4, this, ItemUpgrade.TYPE_SPEED, ItemUpgrade.TYPE_CRAFTING);

    private int compare = 0;
    private IBlockState block;

    private CraftingTaskScheduler scheduler = new CraftingTaskScheduler(this);

    public TileConstructor() {
        dataManager.addWatchedParameter(COMPARE);
    }

    @Override
    public boolean canAddMicroblock(IMicroblock microblock) {
        return !isBlockingMicroblock(microblock, getDirection());
    }

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

        readItems(filter, 0, tag);
        readItems(upgrades, 1, tag);

        scheduler.read(tag);
    }

    @Override
    public NBTTagCompound write(NBTTagCompound tag) {
        super.write(tag);

        tag.setInteger(NBT_COMPARE, compare);

        writeItems(filter, 0, tag);
        writeItems(upgrades, 1, tag);

        scheduler.writeToNBT(tag);

        return tag;
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

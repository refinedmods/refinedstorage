package refinedstorage.tile;

import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.items.IItemHandler;
import refinedstorage.RefinedStorageItems;
import refinedstorage.RefinedStorageUtils;
import refinedstorage.container.ContainerConstructor;
import refinedstorage.inventory.BasicItemHandler;
import refinedstorage.inventory.BasicItemValidator;
import refinedstorage.item.ItemUpgrade;
import refinedstorage.tile.config.ICompareConfig;

public class TileConstructor extends TileMachine implements ICompareConfig {
    public static final String NBT_COMPARE = "Compare";

    public static final int BASE_SPEED = 20;

    private BasicItemHandler filter = new BasicItemHandler(1, this);
    private BasicItemHandler upgrades = new BasicItemHandler(
        4,
        this,
        new BasicItemValidator(RefinedStorageItems.UPGRADE, ItemUpgrade.TYPE_SPEED),
        new BasicItemValidator(RefinedStorageItems.UPGRADE, ItemUpgrade.TYPE_CRAFTING)
    );

    private int compare = 0;

    private CraftingTaskScheduler scheduler = new CraftingTaskScheduler();

    @Override
    public int getEnergyUsage() {
        return 1 + RefinedStorageUtils.getUpgradeEnergyUsage(upgrades);
    }

    @Override
    public void updateMachine() {
        if (ticks % RefinedStorageUtils.getSpeed(upgrades, BASE_SPEED, 4) == 0 && filter.getStackInSlot(0) != null) {
            BlockPos front = pos.offset(getDirection());

            Block block = ((ItemBlock) filter.getStackInSlot(0).getItem()).getBlock();

            if (block.canPlaceBlockAt(worldObj, front)) {
                ItemStack took = controller.take(filter.getStackInSlot(0).copy(), compare);

                if (took != null) {
                    scheduler.resetSchedule();
                    worldObj.setBlockState(front, block.getStateFromMeta(took.getItemDamage()), 1 | 2);
                    // From ItemBlock.onItemUse
                    SoundType blockSound = block.getStepSound();
                    worldObj.playSound(null, front, blockSound.getPlaceSound(), SoundCategory.BLOCKS, (blockSound.getVolume() + 1.0F) / 2.0F, blockSound.getPitch() * 0.8F);
                } else if (RefinedStorageUtils.hasUpgrade(upgrades, ItemUpgrade.TYPE_CRAFTING)) {
                    ItemStack craft = filter.getStackInSlot(0);

                    if (scheduler.canSchedule(compare, craft)) {
                        scheduler.schedule(controller, compare, craft);
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
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);

        if (nbt.hasKey(NBT_COMPARE)) {
            compare = nbt.getInteger(NBT_COMPARE);
        }

        RefinedStorageUtils.restoreItems(filter, 0, nbt);
        RefinedStorageUtils.restoreItems(upgrades, 1, nbt);

        scheduler.readFromNBT(nbt);
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);

        nbt.setInteger(NBT_COMPARE, compare);

        RefinedStorageUtils.saveItems(filter, 0, nbt);
        RefinedStorageUtils.saveItems(upgrades, 1, nbt);

        scheduler.writeToNBT(nbt);
    }

    @Override
    public void receiveContainerData(ByteBuf buf) {
        super.receiveContainerData(buf);

        compare = buf.readInt();
    }

    @Override
    public void sendContainerData(ByteBuf buf) {
        super.sendContainerData(buf);

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
}

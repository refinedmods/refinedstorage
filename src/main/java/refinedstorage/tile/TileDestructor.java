package refinedstorage.tile;

import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import refinedstorage.RefinedStorageUtils;
import refinedstorage.container.ContainerDestructor;
import refinedstorage.inventory.InventorySimple;
import refinedstorage.tile.config.ICompareConfig;
import refinedstorage.tile.config.IModeConfig;
import refinedstorage.tile.config.ModeConfigUtils;

import java.util.List;

public class TileDestructor extends TileMachine implements ICompareConfig, IModeConfig {
    public static final String NBT_COMPARE = "Compare";
    public static final String NBT_MODE = "Mode";

    public static final int BASE_SPEED = 20;

    private InventorySimple inventory = new InventorySimple("destructor", 9, this);
    private InventorySimple upgradesInventory = new InventorySimple("upgrades", 4, this);

    private int compare = 0;
    private int mode = 0;

    @Override
    public int getEnergyUsage() {
        return 1;
    }

    @Override
    public void updateMachine() {
        // We check if the controller isn't null here because
        // when a destructor faces a storage network block and removes it
        // it will essentially remove itself from the network without knowing.
        if (controller != null && ticks % RefinedStorageUtils.getSpeed(upgradesInventory, BASE_SPEED, 4) == 0) {
            BlockPos front = pos.offset(getDirection());

            IBlockState frontBlockState = worldObj.getBlockState(front);
            Block frontBlock = frontBlockState.getBlock();

            if (Item.getItemFromBlock(frontBlock) != null && !frontBlock.isAir(frontBlockState, worldObj, front)) {
                if (ModeConfigUtils.doesNotViolateMode(inventory, this, compare, new ItemStack(frontBlock, 1, frontBlock.getMetaFromState(frontBlockState)))) {
                    List<ItemStack> drops = frontBlock.getDrops(worldObj, front, frontBlockState, 0);

                    worldObj.playAuxSFXAtEntity(null, 2001, front, Block.getStateId(frontBlockState));
                    worldObj.setBlockToAir(front);

                    for (ItemStack drop : drops) {
                        if (!controller.push(drop)) {
                            RefinedStorageUtils.dropStack(worldObj, drop, front.getX(), front.getY(), front.getZ());
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
        markDirty();

        this.compare = compare;
    }

    @Override
    public boolean isWhitelist() {
        return mode == 0;
    }

    @Override
    public boolean isBlacklist() {
        return mode == 1;
    }

    @Override
    public void setToWhitelist() {
        markDirty();

        this.mode = 0;
    }

    @Override
    public void setToBlacklist() {
        markDirty();

        this.mode = 1;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);

        if (nbt.hasKey(NBT_COMPARE)) {
            compare = nbt.getInteger(NBT_COMPARE);
        }

        if (nbt.hasKey(NBT_MODE)) {
            mode = nbt.getInteger(NBT_MODE);
        }

        RefinedStorageUtils.restoreInventory(inventory, 0, nbt);
        RefinedStorageUtils.restoreInventory(upgradesInventory, 1, nbt);
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);

        nbt.setInteger(NBT_COMPARE, compare);
        nbt.setInteger(NBT_MODE, mode);

        RefinedStorageUtils.saveInventory(inventory, 0, nbt);
        RefinedStorageUtils.saveInventory(upgradesInventory, 1, nbt);
    }

    @Override
    public void receiveContainerData(ByteBuf buf) {
        super.receiveContainerData(buf);

        compare = buf.readInt();
        mode = buf.readInt();
    }

    @Override
    public void sendContainerData(ByteBuf buf) {
        super.sendContainerData(buf);

        buf.writeInt(compare);
        buf.writeInt(mode);
    }

    public Class<? extends Container> getContainer() {
        return ContainerDestructor.class;
    }

    public InventorySimple getUpgradesInventory() {
        return upgradesInventory;
    }

    @Override
    public IInventory getDroppedInventory() {
        return upgradesInventory;
    }

    public IInventory getInventory() {
        return inventory;
    }
}

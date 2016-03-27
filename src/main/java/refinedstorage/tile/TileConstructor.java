package refinedstorage.tile;

import io.netty.buffer.ByteBuf;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import refinedstorage.inventory.InventorySimple;
import refinedstorage.tile.settings.ICompareSetting;
import refinedstorage.util.InventoryUtils;

public class TileConstructor extends TileMachine implements ICompareSetting {
    public static final int SPEED = 10;

    public static final String NBT_COMPARE = "Compare";

    private InventorySimple inventory = new InventorySimple("constructor", 1, this);

    private int compare = 0;

    @Override
    public int getEnergyUsage() {
        return 1;
    }

    @Override
    public void updateMachine() {
        if (ticks % SPEED == 0) {
            BlockPos front = pos.offset(getDirection());

            if ((worldObj.isAirBlock(front) || worldObj.getBlockState(front).getBlock().getMaterial(worldObj.getBlockState(front)).isLiquid()) && inventory.getStackInSlot(0) != null) {
                ItemStack took = getController().take(inventory.getStackInSlot(0).copy(), compare);

                if (took != null) {
                    worldObj.setBlockState(front, ((ItemBlock) took.getItem()).getBlock().getStateFromMeta(took.getItemDamage()), 1 | 2);
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
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);

        if (nbt.hasKey(NBT_COMPARE)) {
            compare = nbt.getInteger(NBT_COMPARE);
        }

        InventoryUtils.restoreInventory(inventory, 0, nbt);
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);

        nbt.setInteger(NBT_COMPARE, compare);

        InventoryUtils.saveInventory(inventory, 0, nbt);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        super.fromBytes(buf);

        compare = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        super.toBytes(buf);

        buf.writeInt(compare);
    }

    public IInventory getInventory() {
        return inventory;
    }
}

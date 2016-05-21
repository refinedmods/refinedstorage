package refinedstorage;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;
import net.minecraftforge.items.wrapper.SidedInvWrapper;
import refinedstorage.inventory.InventorySimple;
import refinedstorage.item.ItemUpgrade;

public class RefinedStorageUtils {
    public static final String NBT_INVENTORY = "Inventory_%d";
    public static final String NBT_SLOT = "Slot";

    public static final int COMPARE_DAMAGE = 1;
    public static final int COMPARE_NBT = 2;
    public static final int COMPARE_QUANTITY = 4;

    public static void saveInventory(IInventory inventory, int id, NBTTagCompound nbt) {
        NBTTagList tagList = new NBTTagList();

        for (int i = 0; i < inventory.getSizeInventory(); i++) {
            if (inventory.getStackInSlot(i) != null) {
                NBTTagCompound compoundTag = new NBTTagCompound();

                compoundTag.setInteger(NBT_SLOT, i);

                inventory.getStackInSlot(i).writeToNBT(compoundTag);

                tagList.appendTag(compoundTag);
            }
        }

        nbt.setTag(String.format(NBT_INVENTORY, id), tagList);
    }

    public static void restoreInventory(IInventory inventory, int id, NBTTagCompound nbt) {
        String name = String.format(NBT_INVENTORY, id);

        if (nbt.hasKey(name)) {
            NBTTagList tagList = nbt.getTagList(name, Constants.NBT.TAG_COMPOUND);

            for (int i = 0; i < tagList.tagCount(); i++) {
                int slot = tagList.getCompoundTagAt(i).getInteger(NBT_SLOT);

                ItemStack stack = ItemStack.loadItemStackFromNBT(tagList.getCompoundTagAt(i));

                inventory.setInventorySlotContents(slot, stack);
            }
        }
    }

    public static void dropInventory(World world, IInventory inventory, int x, int y, int z) {
        for (int i = 0; i < inventory.getSizeInventory(); ++i) {
            ItemStack stack = inventory.getStackInSlot(i);

            if (stack != null) {
                dropStack(world, stack, x, y, z);
            }
        }
    }

    public static void dropStack(World world, ItemStack stack, int x, int y, int z) {
        float xo = world.rand.nextFloat() * 0.8F + 0.1F;
        float yo = world.rand.nextFloat() * 0.8F + 0.1F;
        float zo = world.rand.nextFloat() * 0.8F + 0.1F;

        while (stack.stackSize > 0) {
            int amount = world.rand.nextInt(21) + 10;

            if (amount > stack.stackSize) {
                amount = stack.stackSize;
            }

            stack.stackSize -= amount;

            EntityItem entity = new EntityItem(world, (float) x + xo, (float) y + yo, (float) z + zo, new ItemStack(stack.getItem(), amount, stack.getItemDamage()));

            entity.motionX = (float) world.rand.nextGaussian() * 0.05F;
            entity.motionY = (float) world.rand.nextGaussian() * 0.05F + 0.2F;
            entity.motionZ = (float) world.rand.nextGaussian() * 0.05F;

            if (stack.hasTagCompound()) {
                entity.getEntityItem().setTagCompound((NBTTagCompound) stack.getTagCompound().copy());
            }

            world.spawnEntityInWorld(entity);
        }
    }

    public static void pushToInventory(IInventory inventory, ItemStack stack) {
        int toGo = stack.stackSize;

        for (int i = 0; i < inventory.getSizeInventory(); ++i) {
            ItemStack slot = inventory.getStackInSlot(i);

            if (slot == null) {
                inventory.setInventorySlotContents(i, stack);

                return;
            } else if (compareStackNoQuantity(slot, stack)) {
                int toAdd = toGo;

                if (slot.stackSize + toAdd > slot.getMaxStackSize()) {
                    toAdd = slot.getMaxStackSize() - slot.stackSize;
                }

                slot.stackSize += toAdd;

                toGo -= toAdd;

                if (toGo == 0) {
                    return;
                }
            }
        }
    }

    public static boolean canPushToInventory(IInventory inventory, ItemStack stack) {
        int toGo = stack.stackSize;

        for (int i = 0; i < inventory.getSizeInventory(); ++i) {
            if (!inventory.isItemValidForSlot(i, stack)) {
                continue;
            }

            ItemStack slot = inventory.getStackInSlot(i);

            if (slot == null) {
                return true;
            } else if (compareStackNoQuantity(slot, stack)) {
                int toAdd = toGo;

                if (slot.stackSize + toAdd > slot.getMaxStackSize()) {
                    toAdd = slot.getMaxStackSize() - slot.stackSize;
                }

                toGo -= toAdd;

                if (toGo == 0) {
                    break;
                }
            }
        }

        return toGo == 0;
    }

    public static int getInventoryItemCount(IInventory inventory) {
        int size = 0;

        for (int i = 0; i < inventory.getSizeInventory(); ++i) {
            ItemStack slot = inventory.getStackInSlot(i);

            if (slot != null) {
                size += slot.stackSize;
            }
        }

        return size;
    }

    public static boolean compareStack(ItemStack first, ItemStack second) {
        return compareStack(first, second, COMPARE_NBT | COMPARE_DAMAGE | COMPARE_QUANTITY);
    }

    public static boolean compareStack(ItemStack first, ItemStack second, int flags) {
        if (first == null && second == null) {
            return true;
        }

        if ((first == null && second != null) || (first != null && second == null)) {
            return false;
        }

        if ((flags & COMPARE_DAMAGE) == COMPARE_DAMAGE) {
            if (first.getItemDamage() != second.getItemDamage()) {
                return false;
            }
        }

        if ((flags & COMPARE_NBT) == COMPARE_NBT) {
            if (first.hasTagCompound() && !first.getTagCompound().equals(second.getTagCompound())) {
                return false;
            }
        }

        if ((flags & COMPARE_QUANTITY) == COMPARE_QUANTITY) {
            if (first.stackSize != second.stackSize) {
                return false;
            }
        }

        return first.getItem() == second.getItem();
    }

    public static boolean compareStackNoQuantity(ItemStack first, ItemStack second) {
        return compareStack(first, second, COMPARE_NBT | COMPARE_DAMAGE);
    }

    public static int getSpeed(InventorySimple inventory) {
        return getSpeed(inventory, 9, 2, 0);
    }

    public static int getSpeed(InventorySimple inventory, int speed, int speedIncrease) {
        return getSpeed(inventory, speed, speedIncrease, 0);
    }

    public static int getSpeed(InventorySimple inventory, int speed, int speedIncrease, int start) {
        for (int i = start; i < inventory.getSizeInventory(); ++i) {
            if (inventory.getStackInSlot(i) != null && inventory.getStackInSlot(i).getMetadata() == ItemUpgrade.TYPE_SPEED) {
                speed -= speedIncrease;
            }
        }

        return speed;
    }

    public static boolean hasUpgrade(InventorySimple inventory, int type) {
        return getUpgradeCount(inventory, type) > 0;
    }

    public static int getUpgradeCount(InventorySimple inventory, int type) {
        return getUpgradeCount(inventory, type, 0);
    }

    public static int getUpgradeCount(InventorySimple inventory, int type, int start) {
        int upgrades = 0;

        for (int i = start; i < inventory.getSizeInventory(); ++i) {
            if (inventory.getStackInSlot(i) != null && inventory.getStackInSlot(i).getMetadata() == type) {
                upgrades++;
            }
        }

        return upgrades;
    }

    public static int getUpgradeEnergyUsage(InventorySimple inventory) {
        return getUpgradeEnergyUsage(inventory, 0);
    }

    public static int getUpgradeEnergyUsage(InventorySimple inventory, int start) {
        int usage = 0;

        for (int i = start; i < inventory.getSizeInventory(); ++i) {
            if (inventory.getStackInSlot(i) != null) {
                usage += ItemUpgrade.getEnergyUsage(inventory.getStackInSlot(i).getMetadata());
            }
        }

        return usage;
    }

    public static void writeBooleanArray(NBTTagCompound tag, String name, boolean[] array) {
        int[] intArray = new int[array.length];

        for (int i = 0; i < intArray.length; ++i) {
            intArray[i] = array[i] ? 1 : 0;
        }

        tag.setTag(name, new NBTTagIntArray(intArray));
    }

    public static boolean[] readBooleanArray(NBTTagCompound tag, String name) {
        int[] intArray = tag.getIntArray(name);

        boolean array[] = new boolean[intArray.length];

        for (int i = 0; i < intArray.length; ++i) {
            array[i] = intArray[i] == 1 ? true : false;
        }

        return array;
    }

    public static EnumHand getHandById(int id) {
        switch (id) {
            case 0:
                return EnumHand.MAIN_HAND;
            case 1:
                return EnumHand.OFF_HAND;
            default:
                return EnumHand.MAIN_HAND;
        }
    }

    public static int getIdFromHand(EnumHand hand) {
        switch (hand) {
            case MAIN_HAND:
                return 0;
            case OFF_HAND:
                return 1;
            default:
                return 0;
        }
    }

    public static void updateBlock(World world, BlockPos pos) {
        world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 1 | 2);
    }

    public static IItemHandler getItemHandler(TileEntity te, EnumFacing side) {
        IItemHandler handler = te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side);

        if (handler == null) {
            if (side != null && te instanceof ISidedInventory) {
                handler = new SidedInvWrapper((ISidedInventory) te, side);
            } else if (te instanceof IInventory) {
                handler = new InvWrapper((IInventory) te);
            }
        }

        return handler;
    }
}

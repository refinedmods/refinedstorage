package refinedstorage;

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
import net.minecraftforge.oredict.OreDictionary;
import org.apache.commons.lang3.ArrayUtils;
import refinedstorage.api.autocrafting.ICraftingPattern;
import refinedstorage.api.network.INetworkMaster;
import refinedstorage.api.storage.CompareFlags;
import refinedstorage.item.ItemUpgrade;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class RefinedStorageUtils {
    private static final String NBT_INVENTORY = "Inventory_%d";
    private static final String NBT_SLOT = "Slot";

    public static void writeItems(IItemHandler handler, int id, NBTTagCompound nbt) {
        NBTTagList tagList = new NBTTagList();

        for (int i = 0; i < handler.getSlots(); i++) {
            if (handler.getStackInSlot(i) != null) {
                NBTTagCompound compoundTag = new NBTTagCompound();

                compoundTag.setInteger(NBT_SLOT, i);

                handler.getStackInSlot(i).writeToNBT(compoundTag);

                tagList.appendTag(compoundTag);
            }
        }

        nbt.setTag(String.format(NBT_INVENTORY, id), tagList);
    }

    public static void readItems(IItemHandler handler, int id, NBTTagCompound nbt) {
        String name = String.format(NBT_INVENTORY, id);

        if (nbt.hasKey(name)) {
            NBTTagList tagList = nbt.getTagList(name, Constants.NBT.TAG_COMPOUND);

            for (int i = 0; i < tagList.tagCount(); i++) {
                int slot = tagList.getCompoundTagAt(i).getInteger(NBT_SLOT);

                ItemStack stack = ItemStack.loadItemStackFromNBT(tagList.getCompoundTagAt(i));

                handler.insertItem(slot, stack, false);
            }
        }
    }

    public static void writeItemsLegacy(IInventory inventory, int id, NBTTagCompound nbt) {
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

    public static void readItemsLegacy(IInventory inventory, int id, NBTTagCompound nbt) {
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

    public static boolean compareStack(ItemStack left, ItemStack right) {
        return compareStack(left, right, CompareFlags.COMPARE_NBT | CompareFlags.COMPARE_DAMAGE | CompareFlags.COMPARE_QUANTITY);
    }

    public static boolean compareStack(ItemStack left, ItemStack right, int flags) {
        if (left == null && right == null) {
            return true;
        }

        if ((left == null && right != null) || (left != null && right == null)) {
            return false;
        }

        if ((flags & CompareFlags.COMPARE_DAMAGE) == CompareFlags.COMPARE_DAMAGE) {
            if (left.getMetadata() != right.getMetadata()) {
                return false;
            }
        }

        if ((flags & CompareFlags.COMPARE_NBT) == CompareFlags.COMPARE_NBT) {
            if (!ItemStack.areItemStackTagsEqual(left, right)) {
                return false;
            }
        }

        if ((flags & CompareFlags.COMPARE_QUANTITY) == CompareFlags.COMPARE_QUANTITY) {
            if (left.stackSize != right.stackSize) {
                return false;
            }
        }

        return left.getItem() == right.getItem();
    }

    public static boolean compareStackNoQuantity(ItemStack left, ItemStack right) {
        return compareStack(left, right, CompareFlags.COMPARE_NBT | CompareFlags.COMPARE_DAMAGE);
    }

    public static boolean compareStackOreDict(ItemStack left, ItemStack right) {
        if (left == null && right == null) {
            return true;
        }

        if ((left == null && right != null) || (left != null && right == null)) {
            return false;
        }

        int[] leftIds = OreDictionary.getOreIDs(left);
        int[] rightIds = OreDictionary.getOreIDs(right);

        for (int i : rightIds) {
            if (ArrayUtils.contains(leftIds, i)) {
                return true;
            }
        }

        return false;
    }

    public static int getSpeed(IItemHandler handler) {
        return getSpeed(handler, 9, 2);
    }

    public static int getSpeed(IItemHandler handler, int speed, int speedIncrease) {
        for (int i = 0; i < handler.getSlots(); ++i) {
            if (handler.getStackInSlot(i) != null && handler.getStackInSlot(i).getMetadata() == ItemUpgrade.TYPE_SPEED) {
                speed -= speedIncrease;
            }
        }

        return speed;
    }

    public static boolean hasUpgrade(IItemHandler handler, int type) {
        return getUpgradeCount(handler, type) > 0;
    }

    public static int getUpgradeCount(IItemHandler handler, int type) {
        int upgrades = 0;

        for (int i = 0; i < handler.getSlots(); ++i) {
            if (handler.getStackInSlot(i) != null && handler.getStackInSlot(i).getMetadata() == type) {
                upgrades++;
            }
        }

        return upgrades;
    }

    public static int getUpgradeEnergyUsage(IItemHandler handler) {
        int usage = 0;

        for (int i = 0; i < handler.getSlots(); ++i) {
            if (handler.getStackInSlot(i) != null) {
                usage += ItemUpgrade.getEnergyUsage(handler.getStackInSlot(i).getMetadata());
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

    public static IItemHandler getItemHandler(TileEntity tile, EnumFacing side) {
        if (tile == null) {
            return null;
        }

        IItemHandler handler = tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side);

        if (handler == null) {
            if (side != null && tile instanceof ISidedInventory) {
                handler = new SidedInvWrapper((ISidedInventory) tile, side);
            } else if (tile instanceof IInventory) {
                handler = new InvWrapper((IInventory) tile);
            }
        }

        return handler;
    }

    public static int calculateOffsetOnScale(int pos, float scale) {
        float multiplier = (pos / scale);

        return (int) multiplier;
    }

    public static void combineMultipleItemsInTooltip(List<String> lines, ItemStack... stacks) {
        Set<Integer> combinedIndices = new HashSet<Integer>();

        for (int i = 0; i < stacks.length; ++i) {
            if (!combinedIndices.contains(i)) {
                String data = stacks[i].getDisplayName();

                int amount = stacks[i].stackSize;

                for (int j = i + 1; j < stacks.length; ++j) {
                    if (RefinedStorageUtils.compareStack(stacks[i], stacks[j])) {
                        amount += stacks[j].stackSize;

                        combinedIndices.add(j);
                    }
                }

                if (amount != 1) {
                    data += " (" + amount + "x)";
                }

                lines.add(data);
            }
        }
    }

    public static ItemStack takeFromNetwork(INetworkMaster network, ItemStack stack, int size) {
        return network.take(stack, size, CompareFlags.COMPARE_DAMAGE | CompareFlags.COMPARE_NBT);
    }

    public static ItemStack getFromNetwork(INetworkMaster network, ItemStack stack) {
        return network.getStorage().get(stack, CompareFlags.COMPARE_DAMAGE | CompareFlags.COMPARE_NBT);
    }

    public static ICraftingPattern getPatternFromNetwork(INetworkMaster network, ItemStack stack) {
        return network.getPattern(stack, CompareFlags.COMPARE_DAMAGE | CompareFlags.COMPARE_NBT);
    }

    public static boolean hasPattern(INetworkMaster network, ItemStack stack) {
        return RefinedStorageUtils.getPatternFromNetwork(network, stack) != null;
    }
}

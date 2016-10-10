package refinedstorage;

import io.netty.buffer.ByteBuf;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidContainerItem;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.wrappers.FluidHandlerWrapper;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;
import net.minecraftforge.items.wrapper.SidedInvWrapper;
import org.apache.commons.lang3.tuple.Pair;
import refinedstorage.api.network.INetworkMaster;
import refinedstorage.api.storage.AccessType;
import refinedstorage.apiimpl.API;
import refinedstorage.apiimpl.storage.fluid.FluidStorageNBT;
import refinedstorage.apiimpl.storage.item.ItemStorageNBT;

import java.util.Locale;
import java.util.function.Function;

public final class RSUtils {
    public static final ItemStack EMPTY_BUCKET = new ItemStack(Items.BUCKET);

    private static final String NBT_INVENTORY = "Inventory_%d";
    private static final String NBT_SLOT = "Slot";
    private static final String NBT_ACCESS_TYPE = "AccessType";

    public static void writeItemStack(ByteBuf buf, INetworkMaster network, ItemStack stack) {
        buf.writeInt(Item.getIdFromItem(stack.getItem()));
        buf.writeInt(stack.stackSize);
        buf.writeInt(stack.getItemDamage());
        ByteBufUtils.writeTag(buf, stack.getTagCompound());
        buf.writeInt(API.instance().getItemStackHashCode(stack));
        buf.writeBoolean(network.hasPattern(stack));
    }

    public static void writeFluidStack(ByteBuf buf, FluidStack stack) {
        buf.writeInt(API.instance().getFluidStackHashCode(stack));
        ByteBufUtils.writeUTF8String(buf, FluidRegistry.getFluidName(stack.getFluid()));
        buf.writeInt(stack.amount);
        ByteBufUtils.writeTag(buf, stack.tag);
    }

    public static Pair<Integer, FluidStack> readFluidStack(ByteBuf buf) {
        return Pair.of(buf.readInt(), new FluidStack(FluidRegistry.getFluid(ByteBufUtils.readUTF8String(buf)), buf.readInt(), ByteBufUtils.readTag(buf)));
    }

    public static void constructFromDrive(ItemStack disk, int slot, ItemStorageNBT[] itemStorages, FluidStorageNBT[] fluidStorages, Function<ItemStack, ItemStorageNBT> itemStorageSupplier, Function<ItemStack, FluidStorageNBT> fluidStorageNBTSupplier) {
        if (disk == null) {
            itemStorages[slot] = null;
            fluidStorages[slot] = null;
        } else {
            if (disk.getItem() == RSItems.STORAGE_DISK) {
                itemStorages[slot] = itemStorageSupplier.apply(disk);
            } else if (disk.getItem() == RSItems.FLUID_STORAGE_DISK) {
                fluidStorages[slot] = fluidStorageNBTSupplier.apply(disk);
            }
        }
    }

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

                if (slot >= 0 && slot < handler.getSlots()) {
                    handler.insertItem(slot, stack, false);
                }
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

    public static void writeAccessType(NBTTagCompound tag, AccessType type) {
        tag.setInteger(NBT_ACCESS_TYPE, type.getId());
    }

    public static AccessType readAccessType(NBTTagCompound tag) {
        return tag.hasKey(NBT_ACCESS_TYPE) ? getAccessType(tag.getInteger(NBT_ACCESS_TYPE)) : AccessType.READ_WRITE;
    }

    public static AccessType getAccessType(int id) {
        for (AccessType type : AccessType.values()) {
            if (type.getId() == id) {
                return type;
            }
        }

        return AccessType.READ_WRITE;
    }

    public static IItemHandler getItemHandler(TileEntity tile, EnumFacing side) {
        if (tile == null) {
            return null;
        }

        IItemHandler handler = tile.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side) ? tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side) : null;

        if (handler == null) {
            if (side != null && tile instanceof ISidedInventory) {
                handler = new SidedInvWrapper((ISidedInventory) tile, side);
            } else if (tile instanceof IInventory) {
                handler = new InvWrapper((IInventory) tile);
            }
        }

        return handler;
    }

    @SuppressWarnings("deprecation")
    public static IFluidHandler getFluidHandler(TileEntity tile, EnumFacing side) {
        if (tile == null) {
            return null;
        }

        IFluidHandler handler = null;

        if (tile.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, side)) {
            handler = tile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, side);
        } else if (tile instanceof net.minecraftforge.fluids.IFluidHandler) {
            handler = new FluidHandlerWrapper((net.minecraftforge.fluids.IFluidHandler) tile, side);
        }

        return handler;
    }

    @SuppressWarnings("deprecation")
    public static FluidStack getFluidFromStack(ItemStack stack, boolean simulate) {
        if (stack.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null)) {
            return stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null).drain(Fluid.BUCKET_VOLUME, !simulate);
        } else if (stack.getItem() instanceof IFluidContainerItem) {
            return ((IFluidContainerItem) stack.getItem()).drain(stack, Fluid.BUCKET_VOLUME, !simulate);
        }

        return null;
    }

    public static boolean hasFluidBucket(FluidStack stack) {
        return stack.getFluid() == FluidRegistry.WATER || stack.getFluid() == FluidRegistry.LAVA || FluidRegistry.getBucketFluids().contains(stack.getFluid());
    }

    public static String formatFluidStackQuantity(FluidStack stack) {
        return String.format(Locale.US, "%.1f", (float) stack.amount / 1000).replace(".0", "");
    }

    public static FluidStack copyStackWithSize(FluidStack stack, int size) {
        FluidStack copy = stack.copy();
        copy.amount = size;
        return copy;
    }

    public static FluidStack copyStack(FluidStack stack) {
        return stack == null ? null : stack.copy();
    }
}

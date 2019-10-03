package com.raoulvdberge.refinedstorage.util;

import com.raoulvdberge.refinedstorage.api.network.INetwork;
import com.raoulvdberge.refinedstorage.api.storage.IStorageTracker;
import com.raoulvdberge.refinedstorage.api.storage.disk.IStorageDisk;
import com.raoulvdberge.refinedstorage.api.storage.disk.IStorageDiskProvider;
import com.raoulvdberge.refinedstorage.apiimpl.API;
import com.raoulvdberge.refinedstorage.apiimpl.storage.StorageTrackerEntry;
import com.raoulvdberge.refinedstorage.inventory.item.ItemHandlerBase;
import com.raoulvdberge.refinedstorage.screen.grid.stack.GridStackFluid;
import com.raoulvdberge.refinedstorage.screen.grid.stack.GridStackItem;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemHandlerHelper;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Function;

public final class StackUtils {
    public static final ItemStack EMPTY_BUCKET = new ItemStack(Items.BUCKET);

    private static final String NBT_INVENTORY = "Inventory_%d";
    private static final String NBT_SLOT = "Slot";

    // @Volatile: from PacketBuffer#writeItemStack, with some tweaks to allow int stack counts
    public static void writeItemStack(PacketBuffer buf, @Nonnull ItemStack stack) {
        if (stack.isEmpty()) {
            buf.writeBoolean(false);
        } else {
            buf.writeBoolean(true);

            Item item = stack.getItem();

            buf.writeVarInt(Item.getIdFromItem(item));
            buf.writeByte(stack.getCount());

            CompoundNBT tag = null;

            if (item.isDamageable() || item.shouldSyncTag()) {
                tag = stack.getTag();
            }

            buf.writeCompoundTag(tag);
        }
    }

    // @Volatile: from PacketBuffer#readItemStack, with some tweaks to allow int stack counts
    public static ItemStack readItemStack(PacketBuffer buf) {
        if (!buf.readBoolean()) {
            return ItemStack.EMPTY;
        } else {
            int id = buf.readVarInt();
            int count = buf.readByte();

            ItemStack stack = new ItemStack(Item.getItemById(id), count);

            stack.readShareTag(buf.readCompoundTag());

            return stack;
        }
    }

    public static void writeItemGridStack(PacketBuffer buf, ItemStack stack, @Nullable INetwork network, boolean displayCraftText, @Nullable IStorageTracker.IStorageTrackerEntry entry) {
        writeItemStack(buf, stack);

        buf.writeInt(API.instance().getItemStackHashCode(stack));

        if (network != null) {
            buf.writeBoolean(network.getCraftingManager().getPattern(stack) != null);
            buf.writeBoolean(displayCraftText);
        } else {
            buf.writeBoolean(false);
            buf.writeBoolean(false);
        }

        if (entry == null) {
            buf.writeBoolean(false);
        } else {
            buf.writeBoolean(true);

            buf.writeLong(entry.getTime());
            buf.writeString(entry.getName());
        }
    }

    public static GridStackItem readItemGridStack(PacketBuffer buf) {
        ItemStack stack = readItemStack(buf);
        int hash = buf.readInt();
        boolean craftable = buf.readBoolean();
        boolean displayCraftText = buf.readBoolean();

        IStorageTracker.IStorageTrackerEntry entry = null;
        if (buf.readBoolean()) {
            entry = new StorageTrackerEntry(buf.readLong(), buf.readString());
        }

        return new GridStackItem(hash, stack, craftable, displayCraftText, entry);
    }

    public static void writeFluidGridStack(PacketBuffer buf, FluidStack stack, @Nullable INetwork network, boolean displayCraftText, @Nullable IStorageTracker.IStorageTrackerEntry entry) {
        stack.writeToPacket(buf);

        buf.writeInt(API.instance().getFluidStackHashCode(stack));

        if (network != null) {
            buf.writeBoolean(network.getCraftingManager().getPattern(stack) != null);
            buf.writeBoolean(displayCraftText);
        } else {
            buf.writeBoolean(false);
            buf.writeBoolean(false);
        }

        if (entry == null) {
            buf.writeBoolean(false);
        } else {
            buf.writeBoolean(true);

            buf.writeLong(entry.getTime());
            buf.writeString(entry.getName());
        }
    }

    public static GridStackFluid readFluidGridStack(PacketBuffer buf) {
        FluidStack stack = FluidStack.readFromPacket(buf);
        int hash = buf.readInt();
        boolean craftable = buf.readBoolean();
        boolean displayCraftText = buf.readBoolean();

        IStorageTracker.IStorageTrackerEntry entry = null;
        if (buf.readBoolean()) {
            entry = new StorageTrackerEntry(buf.readLong(), buf.readString());
        }

        return new GridStackFluid(hash, stack, entry, craftable, displayCraftText);
    }

    public static ItemStack nullToEmpty(@Nullable ItemStack stack) {
        return stack == null ? ItemStack.EMPTY : stack;
    }

    @Nullable
    public static ItemStack emptyToNull(@Nonnull ItemStack stack) {
        return stack.isEmpty() ? null : stack;
    }

    @SuppressWarnings("unchecked")
    public static void createStorages(ServerWorld world, ItemStack diskStack, int slot, IStorageDisk<ItemStack>[] itemDisks, IStorageDisk<FluidStack>[] fluidDisks, Function<IStorageDisk<ItemStack>, IStorageDisk> itemDiskWrapper, Function<IStorageDisk<FluidStack>, IStorageDisk> fluidDiskWrapper) {
        if (diskStack.isEmpty()) {
            itemDisks[slot] = null;
            fluidDisks[slot] = null;
        } else {
            IStorageDisk disk = API.instance().getStorageDiskManager(world).getByStack(diskStack);

            if (disk != null) {
                switch (((IStorageDiskProvider) diskStack.getItem()).getType()) {
                    case ITEM: {
                        itemDisks[slot] = itemDiskWrapper.apply(disk);
                        break;
                    }
                    case FLUID: {
                        fluidDisks[slot] = fluidDiskWrapper.apply(disk);
                        break;
                    }
                }
            } else {
                itemDisks[slot] = null;
                fluidDisks[slot] = null;
            }
        }
    }

    public static void writeItems(IItemHandler handler, int id, CompoundNBT tag, Function<ItemStack, CompoundNBT> serializer) {
        ListNBT tagList = new ListNBT();

        for (int i = 0; i < handler.getSlots(); i++) {
            if (!handler.getStackInSlot(i).isEmpty()) {
                CompoundNBT stackTag = serializer.apply(handler.getStackInSlot(i));

                stackTag.putInt(NBT_SLOT, i);

                tagList.add(stackTag);
            }
        }

        tag.put(String.format(NBT_INVENTORY, id), tagList);
    }

    public static void writeItems(IItemHandler handler, int id, CompoundNBT tag) {
        writeItems(handler, id, tag, stack -> stack.write(new CompoundNBT()));
    }

    public static void readItems(IItemHandlerModifiable handler, int id, CompoundNBT tag, Function<CompoundNBT, ItemStack> deserializer) {
        String name = String.format(NBT_INVENTORY, id);

        if (tag.contains(name)) {
            ListNBT tagList = tag.getList(name, Constants.NBT.TAG_COMPOUND);

            for (int i = 0; i < tagList.size(); i++) {
                int slot = tagList.getCompound(i).getInt(NBT_SLOT);

                if (slot >= 0 && slot < handler.getSlots()) {
                    handler.setStackInSlot(slot, deserializer.apply(tagList.getCompound(i)));
                }
            }
        }
    }

    public static void readItems(IItemHandlerModifiable handler, int id, CompoundNBT tag) {
        readItems(handler, id, tag, ItemStack::read);
    }

    public static void readItems(ItemHandlerBase handler, int id, CompoundNBT tag) {
        handler.setReading(true);

        readItems(handler, id, tag, ItemStack::read);

        handler.setReading(false);
    }

    public static void writeItems(IInventory inventory, int id, CompoundNBT tag) {
        ListNBT tagList = new ListNBT();

        for (int i = 0; i < inventory.getSizeInventory(); i++) {
            if (!inventory.getStackInSlot(i).isEmpty()) {
                CompoundNBT stackTag = new CompoundNBT();

                stackTag.putInt(NBT_SLOT, i);

                inventory.getStackInSlot(i).write(stackTag);

                tagList.add(stackTag);
            }
        }

        tag.put(String.format(NBT_INVENTORY, id), tagList);
    }

    public static void readItems(IInventory inventory, int id, CompoundNBT tag) {
        String name = String.format(NBT_INVENTORY, id);

        if (tag.contains(name)) {
            ListNBT tagList = tag.getList(name, Constants.NBT.TAG_COMPOUND);

            for (int i = 0; i < tagList.size(); i++) {
                int slot = tagList.getCompound(i).getInt(NBT_SLOT);

                ItemStack stack = ItemStack.read(tagList.getCompound(i));

                if (!stack.isEmpty()) {
                    inventory.setInventorySlotContents(slot, stack);
                }
            }
        }
    }

    public static FluidStack copy(FluidStack stack, int size) {
        FluidStack copy = stack.copy();
        copy.setAmount(size);
        return copy;
    }

    public static FluidStack copy(@Nullable FluidStack stack) {
        return stack == null ? null : stack.copy();
    }

    public static Pair<ItemStack, FluidStack> getFluid(ItemStack stack, boolean simulate) {
        if (stack.isEmpty()) {
            return Pair.of(ItemStack.EMPTY, FluidStack.EMPTY);
        }

        if (stack.getCount() > 1) {
            stack = ItemHandlerHelper.copyStackWithSize(stack, 1);
        }

        IFluidHandlerItem handler = stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null).orElse(null);
        if (handler != null) {
            FluidStack result = handler.drain(FluidAttributes.BUCKET_VOLUME, simulate ? IFluidHandler.FluidAction.SIMULATE : IFluidHandler.FluidAction.EXECUTE);

            return Pair.of(handler.getContainer(), result);
        }

        return Pair.of(ItemStack.EMPTY, FluidStack.EMPTY);
    }

    private static final String NBT_ITEM_TYPE = "Type";
    private static final String NBT_ITEM_QUANTITY = "Quantity";
    private static final String NBT_ITEM_NBT = "NBT";
    private static final String NBT_ITEM_CAPS = "Caps";

    public static CompoundNBT serializeStackToNbt(@Nonnull ItemStack stack) {
        CompoundNBT dummy = new CompoundNBT();

        CompoundNBT itemTag = new CompoundNBT();

        itemTag.putInt(NBT_ITEM_TYPE, Item.getIdFromItem(stack.getItem()));
        itemTag.putInt(NBT_ITEM_QUANTITY, stack.getCount());

        if (stack.hasTag()) {
            itemTag.put(NBT_ITEM_NBT, stack.getTag());
        }

        stack.write(dummy);

        if (dummy.contains("ForgeCaps")) {
            itemTag.put(NBT_ITEM_CAPS, dummy.get("ForgeCaps"));
        }

        dummy.remove("ForgeCaps");

        return itemTag;
    }

    @Nonnull
    public static ItemStack deserializeStackFromNbt(CompoundNBT tag) {
        ItemStack stack = new ItemStack(
            Item.getItemById(tag.getInt(NBT_ITEM_TYPE)),
            tag.getInt(NBT_ITEM_QUANTITY),
            tag.contains(NBT_ITEM_CAPS) ? tag.getCompound(NBT_ITEM_CAPS) : null
        );

        stack.setTag(tag.contains(NBT_ITEM_NBT) ? tag.getCompound(NBT_ITEM_NBT) : null);

        return stack;
    }
}

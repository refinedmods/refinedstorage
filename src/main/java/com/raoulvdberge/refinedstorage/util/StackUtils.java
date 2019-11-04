package com.raoulvdberge.refinedstorage.util;

import com.raoulvdberge.refinedstorage.api.storage.disk.IStorageDisk;
import com.raoulvdberge.refinedstorage.api.storage.disk.IStorageDiskProvider;
import com.raoulvdberge.refinedstorage.api.storage.tracker.StorageTrackerEntry;
import com.raoulvdberge.refinedstorage.apiimpl.API;
import com.raoulvdberge.refinedstorage.inventory.item.BaseItemHandler;
import com.raoulvdberge.refinedstorage.screen.grid.stack.FluidGridStack;
import com.raoulvdberge.refinedstorage.screen.grid.stack.ItemGridStack;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
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
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.UUID;
import java.util.function.Function;

public final class StackUtils {
    public static final ItemStack EMPTY_BUCKET = new ItemStack(Items.BUCKET);

    private static final String NBT_INVENTORY = "Inventory_%d";
    private static final String NBT_SLOT = "Slot";

    private static final Logger LOGGER = LogManager.getLogger(StackUtils.class);

    // @Volatile: from PacketBuffer#writeItemStack, with some tweaks to allow int stack counts
    public static void writeItemStack(PacketBuffer buf, @Nonnull ItemStack stack) {
        if (stack.isEmpty()) {
            buf.writeBoolean(false);
        } else {
            buf.writeBoolean(true);

            Item item = stack.getItem();

            buf.writeVarInt(Item.getIdFromItem(item));
            buf.writeInt(stack.getCount());

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
            int count = buf.readInt();

            ItemStack stack = new ItemStack(Item.getItemById(id), count);

            stack.readShareTag(buf.readCompoundTag());

            return stack;
        }
    }

    public static void writeItemGridStack(PacketBuffer buf, ItemStack stack, UUID id, @Nullable UUID otherId, boolean craftable, @Nullable StorageTrackerEntry entry) {
        writeItemStack(buf, stack);

        buf.writeBoolean(craftable);
        buf.writeUniqueId(id);

        buf.writeBoolean(otherId != null);
        if (otherId != null) {
            buf.writeUniqueId(otherId);
        }

        if (entry == null) {
            buf.writeBoolean(false);
        } else {
            buf.writeBoolean(true);

            buf.writeLong(entry.getTime());
            buf.writeString(entry.getName());
        }
    }

    public static ItemGridStack readItemGridStack(PacketBuffer buf) {
        ItemStack stack = readItemStack(buf);

        boolean craftable = buf.readBoolean();
        UUID id = buf.readUniqueId();

        UUID otherId = null;
        if (buf.readBoolean()) {
            otherId = buf.readUniqueId();
        }

        StorageTrackerEntry entry = null;
        if (buf.readBoolean()) {
            entry = new StorageTrackerEntry(buf.readLong(), PacketBufferUtils.readString(buf));
        }

        return new ItemGridStack(id, otherId, stack, craftable, entry);
    }

    public static void writeFluidGridStack(PacketBuffer buf, FluidStack stack, UUID id, @Nullable UUID otherId, boolean craftable, @Nullable StorageTrackerEntry entry) {
        stack.writeToPacket(buf);

        buf.writeBoolean(craftable);
        buf.writeUniqueId(id);

        buf.writeBoolean(otherId != null);
        if (otherId != null) {
            buf.writeUniqueId(otherId);
        }

        if (entry == null) {
            buf.writeBoolean(false);
        } else {
            buf.writeBoolean(true);

            buf.writeLong(entry.getTime());
            buf.writeString(entry.getName());
        }
    }

    public static FluidGridStack readFluidGridStack(PacketBuffer buf) {
        FluidStack stack = FluidStack.readFromPacket(buf);
        boolean craftable = buf.readBoolean();
        UUID id = buf.readUniqueId();

        UUID otherId = null;
        if (buf.readBoolean()) {
            otherId = buf.readUniqueId();
        }

        StorageTrackerEntry entry = null;
        if (buf.readBoolean()) {
            entry = new StorageTrackerEntry(buf.readLong(), PacketBufferUtils.readString(buf));
        }

        return new FluidGridStack(id, otherId, stack, entry, craftable);
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

    public static void readItems(BaseItemHandler handler, int id, CompoundNBT tag) {
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

    private static final String NBT_ITEM_ID = "Id";
    private static final String NBT_ITEM_QUANTITY = "Quantity";
    private static final String NBT_ITEM_NBT = "NBT";
    private static final String NBT_ITEM_CAPS = "Caps";

    public static CompoundNBT serializeStackToNbt(@Nonnull ItemStack stack) {
        CompoundNBT dummy = new CompoundNBT();

        CompoundNBT itemTag = new CompoundNBT();

        itemTag.putString(NBT_ITEM_ID, stack.getItem().getRegistryName().toString());
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
        Item item;
        if (tag.contains(NBT_ITEM_ID)) {
            item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(tag.getString(NBT_ITEM_ID)));

            if (item == null) {
                LOGGER.warn("Could not deserialize item from string ID, it no longer exists: " + tag.getString(NBT_ITEM_ID));
            }
        } else {
            throw new IllegalStateException("Cannot deserialize ItemStack: no " + NBT_ITEM_ID + " tag was found!");
        }

        if (item == null) {
            return ItemStack.EMPTY;
        }

        ItemStack stack = new ItemStack(
            item,
            tag.getInt(NBT_ITEM_QUANTITY),
            tag.contains(NBT_ITEM_CAPS) ? tag.getCompound(NBT_ITEM_CAPS) : null
        );

        stack.setTag(tag.contains(NBT_ITEM_NBT) ? tag.getCompound(NBT_ITEM_NBT) : null);

        return stack;
    }
}

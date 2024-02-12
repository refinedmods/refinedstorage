package com.refinedmods.refinedstorage.util;

import com.refinedmods.refinedstorage.api.storage.StorageType;
import com.refinedmods.refinedstorage.api.storage.disk.IStorageDisk;
import com.refinedmods.refinedstorage.api.storage.disk.IStorageDiskProvider;
import com.refinedmods.refinedstorage.api.storage.tracker.StorageTrackerEntry;
import com.refinedmods.refinedstorage.apiimpl.API;
import com.refinedmods.refinedstorage.inventory.item.BaseItemHandler;
import com.refinedmods.refinedstorage.network.grid.GridStackDelta;
import com.refinedmods.refinedstorage.screen.grid.stack.FluidGridStack;
import com.refinedmods.refinedstorage.screen.grid.stack.ItemGridStack;

import java.util.UUID;
import java.util.function.Function;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.attachment.AttachmentInternals;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import org.apache.commons.lang3.tuple.Pair;

public final class StackUtils {
    public static final ItemStack EMPTY_BUCKET = new ItemStack(Items.BUCKET);

    private static final String NBT_INVENTORY = "Inventory_%d";
    private static final String NBT_SLOT = "Slot";

    private static final String NBT_ITEM_ID = "Id";
    private static final String NBT_ITEM_QUANTITY = "Quantity";
    private static final String NBT_ITEM_NBT = "NBT";

    private StackUtils() {
    }

    // @Volatile: from FriendlyByteBuf#writeItem, but allows int item stack counts.
    public static void writeItemStack(FriendlyByteBuf buf, @Nonnull ItemStack stack) {
        if (stack.isEmpty()) {
            buf.writeBoolean(false);
        } else {
            buf.writeBoolean(true);
            Item item = stack.getItem();
            buf.writeId(BuiltInRegistries.ITEM, item);
            buf.writeInt(stack.getCount());
            CompoundTag compoundtag = null;
            if (item.isDamageable(stack) || item.shouldOverrideMultiplayerNbt()) {
                compoundtag = stack.getTag();
            }
            compoundtag = AttachmentInternals.addAttachmentsToTag(compoundtag, stack, false);

            buf.writeNbt(compoundtag);
        }
    }

    // @Volatile: from FriendlyByteBuf#readItem, but allows int item stack counts.
    public static ItemStack readItemStack(FriendlyByteBuf buf) {
        if (!buf.readBoolean()) {
            return ItemStack.EMPTY;
        } else {
            Item item = buf.readById(BuiltInRegistries.ITEM);
            int i = buf.readInt();
            return AttachmentInternals.reconstructItemStack(item, i, buf.readNbt());
        }
    }

    public static void writeItemGridStackDelta(FriendlyByteBuf buf, GridStackDelta<ItemGridStack> delta) {
        buf.writeInt(delta.change());
        writeItemGridStack(buf, delta.stack());
    }

    public static void writeItemGridStack(FriendlyByteBuf buf, ItemGridStack stack) {
        writeItemStack(buf, stack.getStack());

        buf.writeBoolean(stack.isCraftable());
        buf.writeUUID(stack.getId());

        buf.writeBoolean(stack.getOtherId() != null);
        if (stack.getOtherId() != null) {
            buf.writeUUID(stack.getOtherId());
        }

        if (stack.getTrackerEntry() == null) {
            buf.writeBoolean(false);
        } else {
            buf.writeBoolean(true);

            buf.writeLong(stack.getTrackerEntry().getTime());
            buf.writeUtf(stack.getTrackerEntry().getName());
        }
    }

    public static GridStackDelta<ItemGridStack> readItemGridStackDelta(FriendlyByteBuf buf) {
        int delta = buf.readInt();
        return new GridStackDelta<>(delta, readItemGridStack(buf));
    }

    public static ItemGridStack readItemGridStack(FriendlyByteBuf buf) {
        ItemStack stack = readItemStack(buf);

        boolean craftable = buf.readBoolean();
        UUID id = buf.readUUID();

        UUID otherId = null;
        if (buf.readBoolean()) {
            otherId = buf.readUUID();
        }

        StorageTrackerEntry entry = null;
        if (buf.readBoolean()) {
            entry = new StorageTrackerEntry(buf.readLong(), PacketBufferUtils.readString(buf));
        }

        return new ItemGridStack(id, otherId, stack, craftable, entry);
    }

    public static void writeFluidGridStackDelta(FriendlyByteBuf buf, GridStackDelta<FluidGridStack> delta) {
        buf.writeInt(delta.change());
        writeFluidGridStack(buf, delta.stack());
    }

    public static void writeFluidGridStack(FriendlyByteBuf buf, FluidGridStack stack) {
        stack.getStack().writeToPacket(buf);

        buf.writeBoolean(stack.isCraftable());
        buf.writeUUID(stack.getId());

        buf.writeBoolean(stack.getOtherId() != null);
        if (stack.getOtherId() != null) {
            buf.writeUUID(stack.getOtherId());
        }

        if (stack.getTrackerEntry() == null) {
            buf.writeBoolean(false);
        } else {
            buf.writeBoolean(true);

            buf.writeLong(stack.getTrackerEntry().getTime());
            buf.writeUtf(stack.getTrackerEntry().getName());
        }
    }

    public static GridStackDelta<FluidGridStack> readFluidGridStackDelta(FriendlyByteBuf buf) {
        int delta = buf.readInt();
        return new GridStackDelta<>(delta, readFluidGridStack(buf));
    }

    public static FluidGridStack readFluidGridStack(FriendlyByteBuf buf) {
        FluidStack stack = FluidStack.readFromPacket(buf);
        boolean craftable = buf.readBoolean();
        UUID id = buf.readUUID();

        UUID otherId = null;
        if (buf.readBoolean()) {
            otherId = buf.readUUID();
        }

        StorageTrackerEntry entry = null;
        if (buf.readBoolean()) {
            entry = new StorageTrackerEntry(buf.readLong(), PacketBufferUtils.readString(buf));
        }

        return new FluidGridStack(id, otherId, stack, craftable, entry);
    }

    @SuppressWarnings("unchecked")
    public static void createStorages(ServerLevel level, ItemStack diskStack, int slot,
                                      IStorageDisk<ItemStack>[] itemDisks, IStorageDisk<FluidStack>[] fluidDisks,
                                      Function<IStorageDisk<ItemStack>, IStorageDisk> itemDiskWrapper,
                                      Function<IStorageDisk<FluidStack>, IStorageDisk> fluidDiskWrapper) {
        if (diskStack.isEmpty()) {
            itemDisks[slot] = null;
            fluidDisks[slot] = null;
        } else {
            IStorageDisk disk = API.instance().getStorageDiskManager(level).getByStack(diskStack);

            if (disk != null) {
                StorageType type = ((IStorageDiskProvider) diskStack.getItem()).getType();

                if (type == StorageType.ITEM) {
                    itemDisks[slot] = itemDiskWrapper.apply(disk);
                } else if (type == StorageType.FLUID) {
                    fluidDisks[slot] = fluidDiskWrapper.apply(disk);
                }
            } else {
                itemDisks[slot] = null;
                fluidDisks[slot] = null;
            }
        }
    }

    public static void writeItems(IItemHandler handler, int id, CompoundTag tag,
                                  Function<ItemStack, CompoundTag> serializer) {
        ListTag tagList = new ListTag();

        for (int i = 0; i < handler.getSlots(); i++) {
            if (!handler.getStackInSlot(i).isEmpty()) {
                CompoundTag stackTag = serializer.apply(handler.getStackInSlot(i));

                stackTag.putInt(NBT_SLOT, i);

                tagList.add(stackTag);
            }
        }

        tag.put(String.format(NBT_INVENTORY, id), tagList);
    }

    public static void writeItems(IItemHandler handler, int id, CompoundTag tag) {
        writeItems(handler, id, tag, stack -> stack.save(new CompoundTag()));
    }

    public static void readItems(IItemHandlerModifiable handler, int id, CompoundTag tag,
                                 Function<CompoundTag, ItemStack> deserializer) {
        String name = String.format(NBT_INVENTORY, id);

        if (tag.contains(name)) {
            ListTag tagList = tag.getList(name, Tag.TAG_COMPOUND);

            for (int i = 0; i < tagList.size(); i++) {
                int slot = tagList.getCompound(i).getInt(NBT_SLOT);

                if (slot >= 0 && slot < handler.getSlots()) {
                    handler.setStackInSlot(slot, deserializer.apply(tagList.getCompound(i)));
                }
            }
        }
    }

    public static void readItems(IItemHandlerModifiable handler, int id, CompoundTag tag) {
        readItems(handler, id, tag, ItemStack::of);
    }

    public static void readItems(BaseItemHandler handler, int id, CompoundTag tag) {
        handler.setReading(true);

        readItems(handler, id, tag, ItemStack::of);

        handler.setReading(false);
    }

    public static void writeItems(Container inventory, int id, CompoundTag tag) {
        ListTag tagList = new ListTag();

        for (int i = 0; i < inventory.getContainerSize(); i++) {
            if (!inventory.getItem(i).isEmpty()) {
                CompoundTag stackTag = new CompoundTag();

                stackTag.putInt(NBT_SLOT, i);

                inventory.getItem(i).save(stackTag);

                tagList.add(stackTag);
            }
        }

        tag.put(String.format(NBT_INVENTORY, id), tagList);
    }

    public static void readItems(Container inventory, int id, CompoundTag tag) {
        String name = String.format(NBT_INVENTORY, id);

        if (tag.contains(name)) {
            ListTag tagList = tag.getList(name, Tag.TAG_COMPOUND);

            for (int i = 0; i < tagList.size(); i++) {
                int slot = tagList.getCompound(i).getInt(NBT_SLOT);

                ItemStack stack = ItemStack.of(tagList.getCompound(i));

                if (!stack.isEmpty()) {
                    inventory.setItem(slot, stack);
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

        IFluidHandlerItem handler = stack.getCapability(Capabilities.FluidHandler.ITEM);
        if (handler != null) {
            FluidStack result = handler.drain(FluidType.BUCKET_VOLUME,
                simulate ? IFluidHandler.FluidAction.SIMULATE : IFluidHandler.FluidAction.EXECUTE);

            return Pair.of(handler.getContainer(), result);
        }

        return Pair.of(ItemStack.EMPTY, FluidStack.EMPTY);
    }

    public static CompoundTag serializeStackToNbt(@Nonnull ItemStack stack) {
        CompoundTag itemTag = new CompoundTag();
        ResourceLocation key = BuiltInRegistries.ITEM.getKey(stack.getItem());
        itemTag.putString(NBT_ITEM_ID, key.toString());
        itemTag.putInt(NBT_ITEM_QUANTITY, stack.getCount());
        var tag = AttachmentInternals.addAttachmentsToTag(stack.getTag(), stack, true);
        if (tag != null) {
            itemTag.put(NBT_ITEM_NBT, tag);
        }
        return itemTag;
    }

    @Nonnull
    public static ItemStack deserializeStackFromNbt(CompoundTag tag) {
        return AttachmentInternals.reconstructItemStack(
            BuiltInRegistries.ITEM.get(new ResourceLocation(tag.getString(NBT_ITEM_ID))),
            tag.getInt(NBT_ITEM_QUANTITY),
            tag.getCompound(NBT_ITEM_NBT)
        );
    }
}

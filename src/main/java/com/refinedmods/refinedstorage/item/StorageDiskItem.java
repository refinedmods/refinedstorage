package com.refinedmods.refinedstorage.item;

import com.refinedmods.refinedstorage.RSItems;
import com.refinedmods.refinedstorage.api.storage.StorageType;
import com.refinedmods.refinedstorage.api.storage.disk.IStorageDisk;
import com.refinedmods.refinedstorage.api.storage.disk.IStorageDiskProvider;
import com.refinedmods.refinedstorage.api.storage.disk.StorageDiskSyncData;
import com.refinedmods.refinedstorage.apiimpl.API;
import com.refinedmods.refinedstorage.apiimpl.storage.ItemStorageType;
import com.refinedmods.refinedstorage.render.Styles;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

public class StorageDiskItem extends Item implements IStorageDiskProvider {
    private static final String NBT_ID = "Id";

    private final ItemStorageType type;

    public StorageDiskItem(ItemStorageType type) {
        super(new Item.Properties().stacksTo(1));

        this.type = type;
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean selected) {
        super.inventoryTick(stack, level, entity, slot, selected);

        if (!level.isClientSide && !stack.hasTag() && entity instanceof Player) {
            UUID id = UUID.randomUUID();

            API.instance().getStorageDiskManager((ServerLevel) level).set(id, API.instance().createDefaultItemDisk((ServerLevel) level, getCapacity(stack), (Player) entity));
            API.instance().getStorageDiskManager((ServerLevel) level).markForSaving();

            setId(stack, id);
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);

        if (isValid(stack)) {
            UUID id = getId(stack);

            API.instance().getStorageDiskSync().sendRequest(id);

            StorageDiskSyncData data = API.instance().getStorageDiskSync().getData(id);
            if (data != null) {
                if (data.getCapacity() == -1) {
                    tooltip.add(Component.translatable("misc.refinedstorage.storage.stored", API.instance().getQuantityFormatter().format(data.getStored())).setStyle(Styles.GRAY));
                } else {
                    tooltip.add(Component.translatable("misc.refinedstorage.storage.stored_capacity", API.instance().getQuantityFormatter().format(data.getStored()), API.instance().getQuantityFormatter().format(data.getCapacity())).setStyle(Styles.GRAY));
                }
            }

            if (flag.isAdvanced()) {
                tooltip.add(Component.literal(id.toString()).setStyle(Styles.GRAY));
            }
        }
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack diskStack = player.getItemInHand(hand);

        if (!level.isClientSide && player.isCrouching() && type != ItemStorageType.CREATIVE) {
            IStorageDisk disk = API.instance().getStorageDiskManager((ServerLevel) level).getByStack(diskStack);

            if (disk != null && disk.getStored() == 0) {
                ItemStack storagePart = new ItemStack(StoragePartItem.getByType(type), diskStack.getCount());

                if (!player.getInventory().add(storagePart.copy())) {
                    Containers.dropItemStack(level, player.getX(), player.getY(), player.getZ(), storagePart);
                }

                API.instance().getStorageDiskManager((ServerLevel) level).remove(getId(diskStack));
                API.instance().getStorageDiskManager((ServerLevel) level).markForSaving();

                return new InteractionResultHolder<>(InteractionResult.SUCCESS, new ItemStack(RSItems.STORAGE_HOUSING.get()));
            }
        }

        return new InteractionResultHolder<>(InteractionResult.PASS, diskStack);
    }

    @Override
    public int getEntityLifespan(ItemStack stack, Level level) {
        return Integer.MAX_VALUE;
    }

    @Override
    public UUID getId(ItemStack disk) {
        return disk.getTag().getUUID(NBT_ID);
    }

    @Override
    public void setId(ItemStack disk, UUID id) {
        disk.setTag(new CompoundTag());
        disk.getTag().putUUID(NBT_ID, id);
    }

    @Override
    public boolean isValid(ItemStack disk) {
        return disk.hasTag() && disk.getTag().hasUUID(NBT_ID);
    }

    @Override
    public int getCapacity(ItemStack disk) {
        return type.getCapacity();
    }

    @Override
    public StorageType getType() {
        return StorageType.ITEM;
    }
}

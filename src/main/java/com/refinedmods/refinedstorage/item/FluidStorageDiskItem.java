package com.refinedmods.refinedstorage.item;

import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.RSItems;
import com.refinedmods.refinedstorage.api.storage.StorageType;
import com.refinedmods.refinedstorage.api.storage.disk.IStorageDisk;
import com.refinedmods.refinedstorage.api.storage.disk.IStorageDiskProvider;
import com.refinedmods.refinedstorage.api.storage.disk.StorageDiskSyncData;
import com.refinedmods.refinedstorage.apiimpl.API;
import com.refinedmods.refinedstorage.apiimpl.storage.FluidStorageType;
import com.refinedmods.refinedstorage.render.Styles;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

public class FluidStorageDiskItem extends Item implements IStorageDiskProvider {
    private static final String NBT_ID = "Id";

    private final FluidStorageType type;

    public FluidStorageDiskItem(FluidStorageType type) {
        super(new Item.Properties().tab(RS.MAIN_GROUP).stacksTo(1));

        this.type = type;
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        super.inventoryTick(stack, world, entity, slot, selected);

        if (!world.isClientSide && !stack.hasTag() && entity instanceof PlayerEntity) {
            UUID id = UUID.randomUUID();

            API.instance().getStorageDiskManager((ServerWorld) world).set(id, API.instance().createDefaultFluidDisk((ServerWorld) world, getCapacity(stack), (PlayerEntity) entity));
            API.instance().getStorageDiskManager((ServerWorld) world).markForSaving();

            setId(stack, id);
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable World world, List<ITextComponent> tooltip, ITooltipFlag flag) {
        super.appendHoverText(stack, world, tooltip, flag);

        if (isValid(stack)) {
            UUID id = getId(stack);

            API.instance().getStorageDiskSync().sendRequest(id);

            StorageDiskSyncData data = API.instance().getStorageDiskSync().getData(id);
            if (data != null) {
                if (data.getCapacity() == -1) {
                    tooltip.add(new TranslationTextComponent("misc.refinedstorage.storage.stored", API.instance().getQuantityFormatter().format(data.getStored())).setStyle(Styles.GRAY));
                } else {
                    tooltip.add(new TranslationTextComponent("misc.refinedstorage.storage.stored_capacity", API.instance().getQuantityFormatter().format(data.getStored()), API.instance().getQuantityFormatter().format(data.getCapacity())).setStyle(Styles.GRAY));
                }
            }

            if (flag.isAdvanced()) {
                tooltip.add(new StringTextComponent(id.toString()).setStyle(Styles.GRAY));
            }
        }
    }

    @Override
    public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        ItemStack diskStack = player.getItemInHand(hand);

        if (!world.isClientSide && player.isCrouching() && type != FluidStorageType.CREATIVE) {
            IStorageDisk disk = API.instance().getStorageDiskManager((ServerWorld) world).getByStack(diskStack);

            if (disk != null && disk.getStored() == 0) {
                ItemStack storagePart = new ItemStack(FluidStoragePartItem.getByType(type), diskStack.getCount());

                if (!player.inventory.add(storagePart.copy())) {
                    InventoryHelper.dropItemStack(world, player.getX(), player.getY(), player.getZ(), storagePart);
                }

                API.instance().getStorageDiskManager((ServerWorld) world).remove(getId(diskStack));
                API.instance().getStorageDiskManager((ServerWorld) world).markForSaving();

                return new ActionResult<>(ActionResultType.SUCCESS, new ItemStack(RSItems.STORAGE_HOUSING.get()));
            }
        }

        return new ActionResult<>(ActionResultType.PASS, diskStack);
    }

    @Override
    public int getEntityLifespan(ItemStack stack, World world) {
        return Integer.MAX_VALUE;
    }

    @Override
    public UUID getId(ItemStack disk) {
        return disk.getTag().getUUID(NBT_ID);
    }

    @Override
    public void setId(ItemStack disk, UUID id) {
        disk.setTag(new CompoundNBT());
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
        return StorageType.FLUID;
    }
}

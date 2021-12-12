package com.refinedmods.refinedstorage.item.blockitem;

import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.RSBlocks;
import com.refinedmods.refinedstorage.RSItems;
import com.refinedmods.refinedstorage.api.storage.disk.IStorageDisk;
import com.refinedmods.refinedstorage.api.storage.disk.StorageDiskSyncData;
import com.refinedmods.refinedstorage.apiimpl.API;
import com.refinedmods.refinedstorage.apiimpl.network.node.storage.FluidStorageNetworkNode;
import com.refinedmods.refinedstorage.apiimpl.storage.FluidStorageType;
import com.refinedmods.refinedstorage.block.FluidStorageBlock;
import com.refinedmods.refinedstorage.item.FluidStoragePartItem;
import com.refinedmods.refinedstorage.item.ProcessorItem;
import com.refinedmods.refinedstorage.render.Styles;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
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

public class FluidStorageBlockItem extends BaseBlockItem {
    private final FluidStorageType type;

    public FluidStorageBlockItem(FluidStorageBlock block) {
        super(block, new Item.Properties().tab(RS.MAIN_GROUP));

        this.type = block.getType();
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
        ItemStack storageStack = player.getItemInHand(hand);

        if (!world.isClientSide && player.isCrouching() && type != FluidStorageType.CREATIVE) {
            UUID diskId = null;
            IStorageDisk disk = null;

            if (isValid(storageStack)) {
                diskId = getId(storageStack);
                disk = API.instance().getStorageDiskManager((ServerWorld) world).get(diskId);
            }

            // Newly created fluid storages won't have a tag yet, so allow invalid disks as well.
            if (disk == null || disk.getStored() == 0) {
                ItemStack fluidStoragePart = new ItemStack(FluidStoragePartItem.getByType(type));

                if (!player.inventory.add(fluidStoragePart.copy())) {
                    InventoryHelper.dropItemStack(world, player.getX(), player.getY(), player.getZ(), fluidStoragePart);
                }

                ItemStack processor = new ItemStack(RSItems.PROCESSORS.get(ProcessorItem.Type.BASIC).get());

                if (!player.inventory.add(processor.copy())) {
                    InventoryHelper.dropItemStack(world, player.getX(), player.getY(), player.getZ(), processor);
                }

                ItemStack bucket = new ItemStack(Items.BUCKET);

                if (!player.inventory.add(bucket.copy())) {
                    InventoryHelper.dropItemStack(world, player.getX(), player.getY(), player.getZ(), bucket);
                }

                if (disk != null) {
                    API.instance().getStorageDiskManager((ServerWorld) world).remove(diskId);
                    API.instance().getStorageDiskManager((ServerWorld) world).markForSaving();
                }

                return new ActionResult<>(ActionResultType.SUCCESS, new ItemStack(RSBlocks.MACHINE_CASING.get()));
            }
        }

        return new ActionResult<>(ActionResultType.PASS, storageStack);
    }

    @Override
    public int getEntityLifespan(ItemStack stack, World world) {
        return Integer.MAX_VALUE;
    }

    private UUID getId(ItemStack disk) {
        return disk.getTag().getUUID(FluidStorageNetworkNode.NBT_ID);
    }

    private boolean isValid(ItemStack disk) {
        return disk.hasTag() && disk.getTag().hasUUID(FluidStorageNetworkNode.NBT_ID);
    }
}

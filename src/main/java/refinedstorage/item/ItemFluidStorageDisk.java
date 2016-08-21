package refinedstorage.item;

import net.minecraft.client.resources.I18n;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import refinedstorage.RefinedStorageItems;
import refinedstorage.apiimpl.storage.fluid.FluidStorageNBT;
import refinedstorage.block.EnumFluidStorageType;

import java.util.List;

public class ItemFluidStorageDisk extends ItemBase {
    public static final int TYPE_64K = 0;
    public static final int TYPE_128K = 1;
    public static final int TYPE_256K = 2;
    public static final int TYPE_512K = 3;
    public static final int TYPE_CREATIVE = 4;
    public static final int TYPE_DEBUG = 5;

    private NBTTagCompound debugDiskTag;

    public ItemFluidStorageDisk() {
        super("fluid_storage_disk");

        setMaxStackSize(1);
        setHasSubtypes(true);
        setMaxDamage(0);
    }

    @Override
    public void getSubItems(Item item, CreativeTabs tab, List<ItemStack> list) {
        for (int i = 0; i < 5; ++i) {
            list.add(FluidStorageNBT.createStackWithNBT(new ItemStack(item, 1, i)));
        }
    }

    private void applyDebugDiskData(ItemStack stack) {
        if (debugDiskTag == null) {
            debugDiskTag = FluidStorageNBT.createNBT();

            FluidStorageNBT storage = new FluidStorageNBT(debugDiskTag, -1, null) {
                @Override
                public int getPriority() {
                    return 0;
                }
            };

            for (Fluid fluid : FluidRegistry.getRegisteredFluids().values()) {
                storage.insertFluid(new FluidStack(fluid, 0), Fluid.BUCKET_VOLUME * 1000, false);
            }

            storage.writeToNBT();
        }

        stack.setTagCompound(debugDiskTag.copy());
    }

    @Override
    public void onUpdate(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        super.onUpdate(stack, world, entity, slot, selected);

        if (!stack.hasTagCompound()) {
            if (stack.getMetadata() == TYPE_DEBUG) {
                applyDebugDiskData(stack);
            } else {
                FluidStorageNBT.createStackWithNBT(stack);
            }
        }
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(ItemStack disk, World world, EntityPlayer player, EnumHand hand) {
        if (!world.isRemote && player.isSneaking() && FluidStorageNBT.isValid(disk) && FluidStorageNBT.getStoredFromNBT(disk.getTagCompound()) == 0 && disk.getMetadata() != TYPE_CREATIVE) {
            ItemStack storagePart = new ItemStack(RefinedStorageItems.FLUID_STORAGE_PART, 1, disk.getMetadata());

            if (!player.inventory.addItemStackToInventory(storagePart.copy())) {
                InventoryHelper.spawnItemStack(world, player.getPosition().getX(), player.getPosition().getY(), player.getPosition().getZ(), storagePart);
            }

            return new ActionResult<>(EnumActionResult.SUCCESS, new ItemStack(RefinedStorageItems.STORAGE_HOUSING));
        }

        return new ActionResult<>(EnumActionResult.PASS, disk);
    }

    @Override
    public void addInformation(ItemStack disk, EntityPlayer player, List<String> tooltip, boolean advanced) {
        if (FluidStorageNBT.isValid(disk)) {
            int capacity = EnumFluidStorageType.getById(disk.getItemDamage()).getCapacity();

            if (capacity == -1) {
                tooltip.add(I18n.format("misc.refinedstorage:storage.stored", FluidStorageNBT.getStoredFromNBT(disk.getTagCompound())));
            } else {
                tooltip.add(I18n.format("misc.refinedstorage:storage.stored_capacity", FluidStorageNBT.getStoredFromNBT(disk.getTagCompound()), capacity));
            }
        }
    }

    @Override
    public void onCreated(ItemStack stack, World world, EntityPlayer player) {
        super.onCreated(stack, world, player);

        FluidStorageNBT.createStackWithNBT(stack);
    }
}

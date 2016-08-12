package refinedstorage.item;

import net.minecraft.client.resources.I18n;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import refinedstorage.apiimpl.storage.fluid.FluidStorageNBT;
import refinedstorage.block.EnumFluidStorageType;

import java.util.List;

public class ItemFluidStorageDisk extends ItemBase {
    public static final int TYPE_64K = 0;
    public static final int TYPE_128K = 1;
    public static final int TYPE_256K = 2;
    public static final int TYPE_512K = 3;
    public static final int TYPE_CREATIVE = 4;

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

    @Override
    public void onUpdate(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        super.onUpdate(stack, world, entity, slot, selected);

        if (!stack.hasTagCompound()) {
            FluidStorageNBT.createStackWithNBT(stack);
        }
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

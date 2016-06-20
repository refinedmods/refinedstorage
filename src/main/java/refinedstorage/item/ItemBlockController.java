package refinedstorage.item;

import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import refinedstorage.RefinedStorageBlocks;
import refinedstorage.api.network.NetworkMaster;
import refinedstorage.block.EnumControllerType;

import java.util.List;

public class ItemBlockController extends ItemBlockBase {
    public ItemBlockController() {
        super(RefinedStorageBlocks.CONTROLLER, true);
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean b) {
        if (stack.getMetadata() != EnumControllerType.CREATIVE.getId()) {
            int energyStored = 0;

            if (stack.getTagCompound() != null && stack.getTagCompound().hasKey(NetworkMaster.NBT_ENERGY)) {
                energyStored = stack.getTagCompound().getInteger(NetworkMaster.NBT_ENERGY);
            }

            list.add(I18n.format("misc.refinedstorage:energy_stored", energyStored, NetworkMaster.ENERGY_CAPACITY));
        }
    }

    @Override
    public void onCreated(ItemStack stack, World world, EntityPlayer player) {
        super.onCreated(stack, world, player);

        createStackWithNBT(stack);
    }

    public static ItemStack createStackWithNBT(ItemStack stack) {
        NBTTagCompound tag = stack.getTagCompound();

        if (tag == null) {
            tag = new NBTTagCompound();
        }

        tag.setInteger(NetworkMaster.NBT_ENERGY, stack.getMetadata() == EnumControllerType.CREATIVE.getId() ? NetworkMaster.ENERGY_CAPACITY : 0);

        return stack;
    }
}

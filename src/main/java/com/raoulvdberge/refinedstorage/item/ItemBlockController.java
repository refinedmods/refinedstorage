package com.raoulvdberge.refinedstorage.item;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.block.enums.ControllerType;
import com.raoulvdberge.refinedstorage.block.info.BlockDirection;
import com.raoulvdberge.refinedstorage.tile.TileController;
import net.minecraft.block.Block;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class ItemBlockController extends ItemBlockBase {
    public ItemBlockController(Block block, BlockDirection direction) {
        super(block, direction, true);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag flag) {
        super.addInformation(stack, world, tooltip, flag);

        if (stack.getMetadata() != ControllerType.CREATIVE.getId()) {
            tooltip.add(I18n.format("misc.refinedstorage:energy_stored", getEnergyStored(stack), RS.INSTANCE.config.controllerCapacity));
        }
    }

    public static int getEnergyStored(ItemStack stack) {
        return (stack.hasTagCompound() && stack.getTagCompound().hasKey(TileController.NBT_ENERGY)) ? stack.getTagCompound().getInteger(TileController.NBT_ENERGY) : 0;
    }

    @Override
    public void onCreated(ItemStack stack, World world, EntityPlayer player) {
        super.onCreated(stack, world, player);

        createStack(stack);
    }

    public static ItemStack createStack(ItemStack stack) {
        NBTTagCompound tag = stack.getTagCompound();

        if (tag == null) {
            tag = new NBTTagCompound();
        }

        tag.setInteger(TileController.NBT_ENERGY, stack.getMetadata() == ControllerType.CREATIVE.getId() ? RS.INSTANCE.config.controllerCapacity : 0);

        return stack;
    }
}

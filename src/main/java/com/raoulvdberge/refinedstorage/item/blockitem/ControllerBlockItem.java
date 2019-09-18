package com.raoulvdberge.refinedstorage.item.blockitem;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.block.ControllerBlock;
import com.raoulvdberge.refinedstorage.tile.ControllerTile;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

// TODO: Convert to energy cap.
public class ControllerBlockItem extends BlockItem {
    private ControllerBlock controller;

    public ControllerBlockItem(ControllerBlock block) {
        super(block, new Item.Properties().group(RS.MAIN_GROUP).maxStackSize(1));

        this.controller = block;
        this.setRegistryName(block.getRegistryName());
    }

    @Override
    public double getDurabilityForDisplay(ItemStack stack) {
        return 1D - ((double) getEnergyStored(stack) / (double) RS.CONFIG.getController().getCapacity());
    }

    @Override
    public int getRGBDurabilityForDisplay(ItemStack stack) {
        return MathHelper.hsvToRGB(Math.max(0.0F, (float) getEnergyStored(stack) / (float) RS.CONFIG.getController().getCapacity()) / 3.0F, 1.0F, 1.0F);
    }

    @Override
    public boolean showDurabilityBar(ItemStack stack) {
        return controller.getType() != ControllerBlock.Type.CREATIVE;
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World world, List<ITextComponent> tooltip, ITooltipFlag flag) {
        super.addInformation(stack, world, tooltip, flag);

        if (controller.getType() != ControllerBlock.Type.CREATIVE) {
            tooltip.add(new TranslationTextComponent("misc.refinedstorage.energy_stored", getEnergyStored(stack), RS.CONFIG.getController().getCapacity()).setStyle(new Style().setColor(TextFormatting.GRAY)));
        }
    }

    public static int getEnergyStored(ItemStack stack) {
        return (stack.hasTag() && stack.getTag().contains(ControllerTile.NBT_ENERGY)) ? stack.getTag().getInt(ControllerTile.NBT_ENERGY) : 0;
    }

    @Override
    public void onCreated(ItemStack stack, World world, PlayerEntity player) {
        super.onCreated(stack, world, player);

        CompoundNBT tag = stack.getTag();

        if (tag == null) {
            tag = new CompoundNBT();
        }

        tag.putInt(ControllerTile.NBT_ENERGY, 0);

        stack.setTag(tag);
    }
}

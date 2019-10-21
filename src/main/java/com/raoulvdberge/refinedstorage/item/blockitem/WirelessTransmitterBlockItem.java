package com.raoulvdberge.refinedstorage.item.blockitem;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.RSBlocks;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class WirelessTransmitterBlockItem extends BaseBlockItem {
    public WirelessTransmitterBlockItem() {
        super(RSBlocks.WIRELESS_TRANSMITTER, new Item.Properties().group(RS.MAIN_GROUP));

        this.setRegistryName(RS.ID, "wireless_transmitter");
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World world, List<ITextComponent> tooltip, ITooltipFlag flag) {
        super.addInformation(stack, world, tooltip, flag);

        tooltip.add(new TranslationTextComponent("block.refinedstorage.wireless_transmitter.tooltip", new TranslationTextComponent("block.refinedstorage.cable")).setStyle(new Style().setColor(TextFormatting.GRAY)));
    }
}

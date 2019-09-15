package com.raoulvdberge.refinedstorage.item;

import com.raoulvdberge.refinedstorage.RS;
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

public class ItemUpgrade extends Item {
    public enum Type {
        NORMAL("normal"),
        SPEED("speed"),
        RANGE("range"),
        CRAFTING("crafting"),
        STACK("stack"),
        SILK_TOUCH("silk_touch"),
        FORTUNE_1("fortune_1"),
        FORTUNE_2("fortune_2"),
        FORTUNE_3("fortune_3");

        private final String name;

        Type(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public int getEnergyUsage() {
            switch (this) {
                case NORMAL:
                    return 0;
                case RANGE:
                    return RS.INSTANCE.config.rangeUpgradeUsage;
                case SPEED:
                    return RS.INSTANCE.config.speedUpgradeUsage;
                case CRAFTING:
                    return RS.INSTANCE.config.craftingUpgradeUsage;
                case STACK:
                    return RS.INSTANCE.config.stackUpgradeUsage;
                case SILK_TOUCH:
                    return RS.INSTANCE.config.silkTouchUpgradeUsage;
                case FORTUNE_1:
                    return RS.INSTANCE.config.fortune1UpgradeUsagePerFortune;
                case FORTUNE_2:
                    return RS.INSTANCE.config.fortune2UpgradeUsagePerFortune;
                case FORTUNE_3:
                    return RS.INSTANCE.config.fortune3UpgradeUsagePerFortune;
                default:
                    throw new IllegalStateException("What even am I?");
            }
        }

        public int getFortuneLevel() {
            switch (this) {
                case FORTUNE_1:
                    return 1;
                case FORTUNE_2:
                    return 2;
                case FORTUNE_3:
                    return 3;
                default:
                    return 0;
            }
        }
    }

    private final Type type;

    public ItemUpgrade(Type type) {
        super(new Item.Properties().group(RS.MAIN_GROUP));

        this.type = type;

        this.setRegistryName(RS.ID, type == Type.NORMAL ? "upgrade" : type.getName() + "_upgrade");
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World world, List<ITextComponent> tooltip, ITooltipFlag flag) {
        super.addInformation(stack, world, tooltip, flag);

        if (type.getFortuneLevel() > 0) {
            tooltip.add(
                new TranslationTextComponent("enchantment.minecraft.fortune")
                    .appendText(" ")
                    .appendSibling(new TranslationTextComponent("enchantment.level." + type.getFortuneLevel()))
                    .setStyle(new Style().setColor(TextFormatting.GRAY))
            );
        }
    }

    @Override
    public boolean hasEffect(ItemStack stack) {
        return type == Type.SILK_TOUCH ||
            type == Type.FORTUNE_1 ||
            type == Type.FORTUNE_2 ||
            type == Type.FORTUNE_3;
    }

    public Type getType() {
        return type;
    }
}

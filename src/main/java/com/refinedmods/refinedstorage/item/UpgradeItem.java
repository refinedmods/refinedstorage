package com.refinedmods.refinedstorage.item;

import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.render.Styles;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class UpgradeItem extends Item {
    public enum Type {
        NORMAL("normal"),
        SPEED("speed"),
        RANGE("range"),
        CRAFTING("crafting"),
        STACK("stack"),
        SILK_TOUCH("silk_touch"),
        FORTUNE_1("fortune_1"),
        FORTUNE_2("fortune_2"),
        FORTUNE_3("fortune_3"),
        REGULATOR("regulator");

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
                    return RS.SERVER_CONFIG.getUpgrades().getRangeUpgradeUsage();
                case SPEED:
                    return RS.SERVER_CONFIG.getUpgrades().getSpeedUpgradeUsage();
                case CRAFTING:
                    return RS.SERVER_CONFIG.getUpgrades().getCraftingUpgradeUsage();
                case STACK:
                    return RS.SERVER_CONFIG.getUpgrades().getStackUpgradeUsage();
                case SILK_TOUCH:
                    return RS.SERVER_CONFIG.getUpgrades().getSilkTouchUpgradeUsage();
                case FORTUNE_1:
                    return RS.SERVER_CONFIG.getUpgrades().getFortune1UpgradeUsage();
                case FORTUNE_2:
                    return RS.SERVER_CONFIG.getUpgrades().getFortune2UpgradeUsage();
                case FORTUNE_3:
                    return RS.SERVER_CONFIG.getUpgrades().getFortune3UpgradeUsage();
                case REGULATOR:
                    return RS.SERVER_CONFIG.getUpgrades().getRegulatorUpgradeUsage();
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

    public UpgradeItem(Type type) {
        super(new Item.Properties().tab(RS.MAIN_GROUP));

        this.type = type;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable World world, List<ITextComponent> tooltip, ITooltipFlag flag) {
        super.appendHoverText(stack, world, tooltip, flag);

        if (type.getFortuneLevel() > 0) {
            tooltip.add(
                new TranslationTextComponent("enchantment.minecraft.fortune")
                    .append(" ")
                    .append(new TranslationTextComponent("enchantment.level." + type.getFortuneLevel()))
                    .setStyle(Styles.GRAY)
            );
        }
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return type == Type.SILK_TOUCH ||
            type == Type.FORTUNE_1 ||
            type == Type.FORTUNE_2 ||
            type == Type.FORTUNE_3;
    }

    public Type getType() {
        return type;
    }
}

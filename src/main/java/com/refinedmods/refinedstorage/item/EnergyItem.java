package com.refinedmods.refinedstorage.item;

import com.refinedmods.refinedstorage.energy.ItemEnergyStorage;
import com.refinedmods.refinedstorage.render.Styles;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.EnergyStorage;
import net.neoforged.neoforge.energy.IEnergyStorage;
import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Supplier;

public abstract class EnergyItem extends Item {
    private final Supplier<Integer> energyCapacity;
    private final boolean creative;

    protected EnergyItem(Item.Properties properties, boolean creative, Supplier<Integer> energyCapacity) {
        super(properties);

        this.creative = creative;
        this.energyCapacity = energyCapacity;
    }

    public EnergyStorage createEnergyStorage(ItemStack stack) {
        return new ItemEnergyStorage(stack, energyCapacity.get());
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return !creative;
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        IEnergyStorage energy = stack.getCapability(Capabilities.EnergyStorage.ITEM);
        if (energy == null) {
            return 0;
        }
        float stored = (float) energy.getEnergyStored() / (float) energy.getMaxEnergyStored();
        return Math.round(stored * 13F);
    }

    @Override
    public int getBarColor(ItemStack stack) {
        IEnergyStorage energy = stack.getCapability(Capabilities.EnergyStorage.ITEM);
        if (energy == null) {
            return super.getBarColor(stack);
        }
        return Mth.hsvToRgb(Math.max(0.0F, (float) energy.getEnergyStored() / (float) energy.getMaxEnergyStored()) / 3.0F, 1.0F, 1.0F);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);

        if (!creative) {
            final IEnergyStorage energy = stack.getCapability(Capabilities.EnergyStorage.ITEM);
            if (energy == null) {
                return;
            }
            tooltip.add(Component.translatable("misc.refinedstorage.energy_stored", energy.getEnergyStored(), energy.getMaxEnergyStored()).setStyle(Styles.GRAY));
        }
    }
}

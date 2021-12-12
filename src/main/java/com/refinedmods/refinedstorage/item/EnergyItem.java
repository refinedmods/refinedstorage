package com.refinedmods.refinedstorage.item;

import com.refinedmods.refinedstorage.item.capabilityprovider.EnergyCapabilityProvider;
import com.refinedmods.refinedstorage.render.Styles;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

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

    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, CompoundNBT tag) {
        return new EnergyCapabilityProvider(stack, energyCapacity.get());
    }

    @Override
    public boolean showDurabilityBar(ItemStack stack) {
        return !creative;
    }

    @Override
    public double getDurabilityForDisplay(ItemStack stack) {
        IEnergyStorage energy = stack.getCapability(CapabilityEnergy.ENERGY).orElse(null);
        if (energy == null) {
            return 0;
        }

        return 1D - ((double) energy.getEnergyStored() / (double) energy.getMaxEnergyStored());
    }

    @Override
    public int getRGBDurabilityForDisplay(ItemStack stack) {
        IEnergyStorage energy = stack.getCapability(CapabilityEnergy.ENERGY, null).orElse(null);
        if (energy == null) {
            return super.getRGBDurabilityForDisplay(stack);
        }

        return MathHelper.hsvToRgb(Math.max(0.0F, (float) energy.getEnergyStored() / (float) energy.getMaxEnergyStored()) / 3.0F, 1.0F, 1.0F);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable World world, List<ITextComponent> tooltip, ITooltipFlag flag) {
        super.appendHoverText(stack, world, tooltip, flag);

        if (!creative) {
            stack.getCapability(CapabilityEnergy.ENERGY, null).ifPresent(energy -> tooltip.add(new TranslationTextComponent("misc.refinedstorage.energy_stored", energy.getEnergyStored(), energy.getMaxEnergyStored()).setStyle(Styles.GRAY)));
        }
    }
}

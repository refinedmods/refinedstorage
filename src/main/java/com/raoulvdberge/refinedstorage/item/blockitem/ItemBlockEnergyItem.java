package com.raoulvdberge.refinedstorage.item.blockitem;

import com.raoulvdberge.refinedstorage.block.BlockBase;
import com.raoulvdberge.refinedstorage.item.capabilityprovider.EnergyCapabilityProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

public abstract class ItemBlockEnergyItem extends ItemBlockBase {
    public static final int TYPE_NORMAL = 0;
    public static final int TYPE_CREATIVE = 1;

    private int energyCapacity;

    public ItemBlockEnergyItem(BlockBase block, int energyCapacity) {
        super(block);

        this.energyCapacity = energyCapacity;

        //setMaxDamage(energyCapacity);
        //setMaxStackSize(1);
    }

    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, CompoundNBT tag) {
        return new EnergyCapabilityProvider(stack, energyCapacity);
    }
/* TODO
    @Override
    public boolean isDamageable() {
        return true;
    }

    @Override
    public boolean isRepairable() {
        return false;
    }

    @Override
    public double getDurabilityForDisplay(ItemStack stack) {
        IEnergyStorage energy = stack.getCapability(CapabilityEnergy.ENERGY, null);

        return 1D - ((double) energy.getEnergyStored() / (double) energy.getMaxEnergyStored());
    }

    @Override
    public int getRGBDurabilityForDisplay(ItemStack stack) {
        IEnergyStorage energy = stack.getCapability(CapabilityEnergy.ENERGY, null);

        return MathHelper.hsvToRGB(Math.max(0.0F, (float) energy.getEnergyStored() / (float) energy.getMaxEnergyStored()) / 3.0F, 1.0F, 1.0F);
    }

    @Override
    public boolean isDamaged(ItemStack stack) {
        return stack.getItemDamage() != TYPE_CREATIVE;
    }

    @Override
    public void setDamage(ItemStack stack, int damage) {
        // NO OP
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        if (!isInCreativeTab(tab)) {
            return;
        }

        items.add(new ItemStack(this, 1, TYPE_NORMAL));

        ItemStack fullyCharged = new ItemStack(this, 1, TYPE_NORMAL);

        IEnergyStorage energy = fullyCharged.getCapability(CapabilityEnergy.ENERGY, null);
        energy.receiveEnergy(energy.getMaxEnergyStored(), false);

        items.add(fullyCharged);

        items.add(new ItemStack(this, 1, TYPE_CREATIVE));
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag flag) {
        super.addInformation(stack, world, tooltip, flag);

        if (stack.getItemDamage() != TYPE_CREATIVE) {
            IEnergyStorage energy = stack.getCapability(CapabilityEnergy.ENERGY, null);

            tooltip.add(I18n.format("misc.refinedstorage.energy_stored", energy.getEnergyStored(), energy.getMaxEnergyStored()));
        }
    }*/
}

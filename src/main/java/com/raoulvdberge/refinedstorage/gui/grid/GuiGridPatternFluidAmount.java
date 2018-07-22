package com.raoulvdberge.refinedstorage.gui.grid;

import com.google.common.primitives.Ints;
import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.container.ContainerFluidAmount;
import com.raoulvdberge.refinedstorage.gui.GuiAmountSpecifying;
import com.raoulvdberge.refinedstorage.gui.GuiBase;
import com.raoulvdberge.refinedstorage.network.MessageGridFluidAmount;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;

public class GuiGridPatternFluidAmount extends GuiAmountSpecifying {
    private int slot;
    private ItemStack fluidContainer;

    public GuiGridPatternFluidAmount(GuiBase parent, EntityPlayer player, int slot, ItemStack fluidContainer) {
        super(parent, new ContainerFluidAmount(player, fluidContainer), 172, 99);

        this.slot = slot;
        this.fluidContainer = fluidContainer;
    }

    @Override
    protected int getDefaultAmount() {
        return fluidContainer.getCount();
    }

    @Override
    protected boolean canAmountGoNegative() {
        return false;
    }

    @Override
    protected int getMaxAmount() {
        return Fluid.BUCKET_VOLUME;
    }

    @Override
    protected String getOkButtonText() {
        return t("misc.refinedstorage:set");
    }

    @Override
    protected String getTitle() {
        return t("gui.refinedstorage:pattern_grid.fluid_amount");
    }

    @Override
    protected String getTexture() {
        return "gui/crafting_settings.png";
    }

    @Override
    protected int[] getIncrements() {
        return new int[]{
            10, 50, 100,
            -10, -50, -100
        };
    }

    @Override
    protected void onOkButtonPressed(boolean shiftDown) {
        Integer amount = Ints.tryParse(amountField.getText());

        if (amount != null && amount > 0 && amount <= Fluid.BUCKET_VOLUME) {
            RS.INSTANCE.network.sendToServer(new MessageGridFluidAmount(slot, amount));

            close();
        }
    }
}

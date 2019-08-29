package com.raoulvdberge.refinedstorage.gui;

import com.google.common.primitives.Ints;
import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.container.ContainerFluidAmount;
import com.raoulvdberge.refinedstorage.network.MessageSlotFilterFluidSetAmount;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.fluids.FluidStack;

public class GuiFluidAmount extends GuiAmountSpecifying {
    private int containerSlot;
    private FluidStack stack;
    private int maxAmount;

    public GuiFluidAmount(GuiBase parent, PlayerEntity player, int containerSlot, FluidStack stack, int maxAmount) {
        super(parent, new ContainerFluidAmount(player, stack), 172, 99);

        this.containerSlot = containerSlot;
        this.stack = stack;
        this.maxAmount = maxAmount;
    }

    @Override
    protected int getDefaultAmount() {
        return stack.amount;
    }

    @Override
    protected boolean canAmountGoNegative() {
        return false;
    }

    @Override
    protected int getMaxAmount() {
        return maxAmount;
    }

    @Override
    protected String getOkButtonText() {
        return t("misc.refinedstorage:set");
    }

    @Override
    protected String getTitle() {
        return t("gui.refinedstorage:fluid_amount");
    }

    @Override
    protected String getTexture() {
        return "gui/crafting_settings.png";
    }

    @Override
    protected int[] getIncrements() {
        return new int[]{
            100, 500, 1000,
            -100, -500, -1000
        };
    }

    @Override
    protected void onOkButtonPressed(boolean shiftDown) {
        Integer amount = Ints.tryParse(amountField.getText());

        if (amount != null) {
            RS.INSTANCE.network.sendToServer(new MessageSlotFilterFluidSetAmount(containerSlot, amount));

            close();
        }
    }
}

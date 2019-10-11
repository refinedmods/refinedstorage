package com.raoulvdberge.refinedstorage.screen;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.container.FluidAmountContainer;
import com.raoulvdberge.refinedstorage.network.SetFluidFilterSlotMessage;
import com.raoulvdberge.refinedstorage.util.StackUtils;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fluids.FluidStack;

public class FluidAmountScreen extends AmountSpecifyingScreen<FluidAmountContainer> {
    private int containerSlot;
    private FluidStack stack;
    private int maxAmount;

    public FluidAmountScreen(BaseScreen parent, PlayerEntity player, int containerSlot, FluidStack stack, int maxAmount) {
        super(parent, new FluidAmountContainer(player, stack), 172, 99, player.inventory, new TranslationTextComponent("gui.refinedstorage.fluid_amount"));

        this.containerSlot = containerSlot;
        this.stack = stack;
        this.maxAmount = maxAmount;
    }

    @Override
    protected int getDefaultAmount() {
        return stack.getAmount();
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
        return I18n.format("misc.refinedstorage.set");
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
        try {
            int amount = Integer.parseInt(amountField.getText());

            RS.NETWORK_HANDLER.sendToServer(new SetFluidFilterSlotMessage(containerSlot, StackUtils.copy(stack, amount)));

            close();
        } catch (NumberFormatException e) {
            // NO OP
        }
    }
}

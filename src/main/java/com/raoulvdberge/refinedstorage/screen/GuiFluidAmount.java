package com.raoulvdberge.refinedstorage.screen;

import com.google.common.primitives.Ints;
import com.raoulvdberge.refinedstorage.container.FluidAmountContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fluids.FluidStack;

public class GuiFluidAmount extends AmountSpecifyingScreen<FluidAmountContainer> {
    private int containerSlot;
    private FluidStack stack;
    private int maxAmount;

    public GuiFluidAmount(BaseScreen parent, PlayerEntity player, int containerSlot, FluidStack stack, int maxAmount) {
        super(parent, new FluidAmountContainer(player, stack), 172, 99, player.inventory, new TranslationTextComponent("gui.refinedstorage:fluid_amount"));

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
        return I18n.format("misc.refinedstorage:set");
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
            // TODO RS.INSTANCE.network.sendToServer(new MessageSlotFilterFluidSetAmount(containerSlot, amount));

            close();
        }
    }
}

package com.raoulvdberge.refinedstorage.screen;

import com.google.common.primitives.Ints;
import com.raoulvdberge.refinedstorage.container.AmountContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

public class GuiAmount extends GuiAmountSpecifying<AmountContainer> {
    private int containerSlot;
    private ItemStack stack;
    private int maxAmount;

    public GuiAmount(BaseScreen parent, PlayerEntity player, int containerSlot, ItemStack stack, int maxAmount) {
        super(parent, new AmountContainer(player, stack), 172, 99, player.inventory);

        this.containerSlot = containerSlot;
        this.stack = stack;
        this.maxAmount = maxAmount;
    }

    @Override
    protected int getDefaultAmount() {
        return stack.getCount();
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
    protected String getGuiTitle() {
        return I18n.format("gui.refinedstorage:item_amount");
    }

    @Override
    protected String getTexture() {
        return "gui/crafting_settings.png";
    }

    @Override
    protected int[] getIncrements() {
        return new int[]{
            1, 10, 64,
            -1, -10, -64
        };
    }

    @Override
    protected void onOkButtonPressed(boolean shiftDown) {
        Integer amount = Ints.tryParse(amountField.getText());

        if (amount != null) {
            // TODO RS.INSTANCE.network.sendToServer(new MessageSlotFilterSet(containerSlot, ItemHandlerHelper.copyStackWithSize(stack, amount)));

            close();
        }
    }
}

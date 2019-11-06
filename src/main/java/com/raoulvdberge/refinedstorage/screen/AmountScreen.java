package com.raoulvdberge.refinedstorage.screen;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.container.AmountContainer;
import com.raoulvdberge.refinedstorage.network.SetFilterSlotMessage;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.items.ItemHandlerHelper;

public class AmountScreen extends AmountSpecifyingScreen<AmountContainer> {
    private int containerSlot;
    private ItemStack stack;
    private int maxAmount;

    public AmountScreen(BaseScreen parent, PlayerEntity player, int containerSlot, ItemStack stack, int maxAmount) {
        super(parent, new AmountContainer(player, stack), 172, 99, player.inventory, new TranslationTextComponent("gui.refinedstorage.item_amount"));

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
        return I18n.format("misc.refinedstorage.set");
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
        try {
            int amount = Integer.parseInt(amountField.getText());

            RS.NETWORK_HANDLER.sendToServer(new SetFilterSlotMessage(containerSlot, ItemHandlerHelper.copyStackWithSize(stack, amount)));

            close();
        } catch (NumberFormatException e) {
            // NO OP
        }
    }
}

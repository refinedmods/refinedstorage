package com.refinedmods.refinedstorage.screen;

import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.container.AmountContainerMenu;
import com.refinedmods.refinedstorage.network.SetFilterSlotMessage;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemHandlerHelper;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;
import java.util.function.UnaryOperator;

public class ItemAmountScreen extends AmountSpecifyingScreen<AmountContainerMenu> {
    private final int containerSlot;
    private final ItemStack stack;
    private final int maxAmount;
    @Nullable
    private final UnaryOperator<Screen> alternativesScreenFactory;

    public ItemAmountScreen(BaseScreen parent, Player player, int containerSlot, ItemStack stack, int maxAmount, @Nullable UnaryOperator<Screen> alternativesScreenFactory) {
        super(parent, new AmountContainerMenu(player, stack), alternativesScreenFactory != null ? 194 : 172, 99, player.getInventory(), new TranslatableComponent("gui.refinedstorage.item_amount"));

        this.containerSlot = containerSlot;
        this.stack = stack;
        this.maxAmount = maxAmount;
        this.alternativesScreenFactory = alternativesScreenFactory;
    }

    @Override
    protected int getOkCancelButtonWidth() {
        return alternativesScreenFactory != null ? 75 : super.getOkCancelButtonWidth();
    }

    @Override
    public void onPostInit(int x, int y) {
        super.onPostInit(x, y);

        if (alternativesScreenFactory != null) {
            addButton(x + 114, cancelButton.y + 24, getOkCancelButtonWidth(), 20, new TranslatableComponent("gui.refinedstorage.alternatives"), true, true, btn -> minecraft.setScreen(alternativesScreenFactory.apply(this)));
        }
    }

    @Override
    protected Pair<Integer, Integer> getOkCancelPos() {
        if (alternativesScreenFactory == null) {
            return super.getOkCancelPos();
        }

        return Pair.of(114, 22);
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
    protected Component getOkButtonText() {
        return new TranslatableComponent("misc.refinedstorage.set");
    }

    @Override
    protected String getTexture() {
        return alternativesScreenFactory != null ? "gui/amount_specifying_wide.png" : "gui/amount_specifying.png";
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
            int amount = Integer.parseInt(amountField.getValue());

            RS.NETWORK_HANDLER.sendToServer(new SetFilterSlotMessage(containerSlot, ItemHandlerHelper.copyStackWithSize(stack, amount)));

            close();
        } catch (NumberFormatException e) {
            // NO OP
        }
    }
}

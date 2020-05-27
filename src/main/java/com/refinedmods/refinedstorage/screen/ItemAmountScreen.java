package com.refinedmods.refinedstorage.screen;

import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.container.AmountContainer;
import com.refinedmods.refinedstorage.network.SetFilterSlotMessage;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.items.ItemHandlerHelper;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;
import java.util.function.Function;

public class ItemAmountScreen extends AmountSpecifyingScreen<AmountContainer> {
    private int containerSlot;
    private ItemStack stack;
    private int maxAmount;
    @Nullable
    private Function<Screen, Screen> alternativesScreenFactory;

    public ItemAmountScreen(BaseScreen parent, PlayerEntity player, int containerSlot, ItemStack stack, int maxAmount, @Nullable Function<Screen, Screen> alternativesScreenFactory) {
        super(parent, new AmountContainer(player, stack), alternativesScreenFactory != null ? 194 : 172, 99, player.inventory, new TranslationTextComponent("gui.refinedstorage.item_amount"));

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
            addButton(x + 114, cancelButton.y + 24, getOkCancelButtonWidth(), 20, I18n.format("gui.refinedstorage.alternatives"), true, true, btn -> {
                minecraft.displayGuiScreen(alternativesScreenFactory.apply(this));
            });
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
    protected String getOkButtonText() {
        return I18n.format("misc.refinedstorage.set");
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
            int amount = Integer.parseInt(amountField.getText());

            RS.NETWORK_HANDLER.sendToServer(new SetFilterSlotMessage(containerSlot, ItemHandlerHelper.copyStackWithSize(stack, amount)));

            close();
        } catch (NumberFormatException e) {
            // NO OP
        }
    }
}

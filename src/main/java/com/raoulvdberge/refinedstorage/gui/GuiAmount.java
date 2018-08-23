package com.raoulvdberge.refinedstorage.gui;

import com.google.common.primitives.Ints;
import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.container.ContainerAmount;
import com.raoulvdberge.refinedstorage.network.MessageSlotFilterSet;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.ItemHandlerHelper;

public class GuiAmount extends GuiAmountSpecifying {
    private int containerSlot;
    private ItemStack stack;
    private int maxAmount;

    public GuiAmount(GuiBase parent, EntityPlayer player, int containerSlot, ItemStack stack, int maxAmount) {
        super(parent, new ContainerAmount(player, stack), 172, 99);

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
        return t("misc.refinedstorage:set");
    }

    @Override
    protected String getTitle() {
        return t("gui.refinedstorage:item_amount");
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
            RS.INSTANCE.network.sendToServer(new MessageSlotFilterSet(containerSlot, ItemHandlerHelper.copyStackWithSize(stack, amount)));

            close();
        }
    }
}

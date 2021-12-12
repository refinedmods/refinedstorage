package com.refinedmods.refinedstorage.screen;

import com.refinedmods.refinedstorage.tile.data.TileDataManager;
import com.refinedmods.refinedstorage.tile.data.TileDataParameter;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.apache.commons.lang3.tuple.Pair;

public class PriorityScreen extends AmountSpecifyingScreen<AbstractContainerMenu> {
    private final TileDataParameter<Integer, ?> priority;

    public PriorityScreen(BaseScreen parent, TileDataParameter<Integer, ?> priority, Inventory inventory) {
        super(parent, new AbstractContainerMenu(null, 0) {
            @Override
            public boolean stillValid(Player player) {
                return false;
            }
        }, 164, 92, inventory, new TranslatableComponent("misc.refinedstorage.priority"));

        this.priority = priority;
    }

    @Override
    protected int getDefaultAmount() {
        return priority.getValue();
    }

    @Override
    protected Component getOkButtonText() {
        return new TranslatableComponent("misc.refinedstorage.set");
    }

    @Override
    protected String getTexture() {
        return "gui/priority.png";
    }

    @Override
    protected Pair<Integer, Integer> getAmountPos() {
        return Pair.of(18 + 1, 47 + 1);
    }

    @Override
    protected Pair<Integer, Integer> getOkCancelPos() {
        return Pair.of(107, 30);
    }

    @Override
    protected boolean canAmountGoNegative() {
        return true;
    }

    @Override
    protected int getMaxAmount() {
        return Integer.MAX_VALUE;
    }

    @Override
    protected int[] getIncrements() {
        return new int[]{
            1, 5, 10,
            -1, -5, -10
        };
    }

    @Override
    protected void onOkButtonPressed(boolean noPreview) {
        try {
            int amount = Integer.parseInt(amountField.getValue());

            TileDataManager.setParameter(priority, amount);

            close();
        } catch (NumberFormatException e) {
            // NO OP
        }
    }
}

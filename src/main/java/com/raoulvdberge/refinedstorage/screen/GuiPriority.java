package com.raoulvdberge.refinedstorage.screen;

import com.google.common.primitives.Ints;
import com.raoulvdberge.refinedstorage.tile.data.TileDataManager;
import com.raoulvdberge.refinedstorage.tile.data.TileDataParameter;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import org.apache.commons.lang3.tuple.Pair;

public class GuiPriority extends GuiAmountSpecifying<Container> {
    private TileDataParameter<Integer, ?> priority;

    public GuiPriority(BaseScreen parent, TileDataParameter<Integer, ?> priority, PlayerInventory inventory) {
        super(parent, new Container(null, 0) { // TODO ctor
            @Override
            public boolean canInteractWith(PlayerEntity player) {
                return false;
            }
        }, 164, 92, inventory);

        this.priority = priority;
    }

    @Override
    protected int getDefaultAmount() {
        return priority.getValue();
    }

    @Override
    protected String getOkButtonText() {
        return I18n.format("misc.refinedstorage:set");
    }

    @Override
    protected String getGuiTitle() {
        return I18n.format("misc.refinedstorage:priority");
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
        Integer amount = Ints.tryParse(amountField.getText());

        if (amount != null) {
            TileDataManager.setParameter(priority, amount);

            close();
        }
    }
}

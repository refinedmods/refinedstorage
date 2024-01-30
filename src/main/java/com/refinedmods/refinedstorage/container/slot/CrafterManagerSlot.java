package com.refinedmods.refinedstorage.container.slot;

import com.refinedmods.refinedstorage.apiimpl.network.node.CrafterManagerNetworkNode;
import com.refinedmods.refinedstorage.screen.IScreenInfoProvider;
import net.neoforged.neoforge.items.IItemHandler;

public class CrafterManagerSlot extends BaseSlot {
    private final boolean visible;
    private final CrafterManagerNetworkNode crafterManager;
    private final IScreenInfoProvider display;

    public CrafterManagerSlot(IItemHandler itemHandler, int inventoryIndex, int x, int y, boolean visible, IScreenInfoProvider display, CrafterManagerNetworkNode crafterManager) {
        super(itemHandler, inventoryIndex, x, y);

        this.visible = visible;
        this.display = display;
        this.crafterManager = crafterManager;
    }

    @Override
    public boolean isActive() {
        return y >= display.getTopHeight() && y < display.getTopHeight() + 18 * display.getVisibleRows() && visible && crafterManager.isActiveOnClient();
    }
}

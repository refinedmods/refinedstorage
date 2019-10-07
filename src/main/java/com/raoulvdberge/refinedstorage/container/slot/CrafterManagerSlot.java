package com.raoulvdberge.refinedstorage.container.slot;

import com.raoulvdberge.refinedstorage.apiimpl.network.node.NetworkNodeCrafterManager;
import com.raoulvdberge.refinedstorage.screen.IScreenInfoProvider;
import net.minecraftforge.items.IItemHandler;

public class CrafterManagerSlot extends BaseSlot {
    private boolean visible;
    private NetworkNodeCrafterManager crafterManager;
    private IScreenInfoProvider display;

    public CrafterManagerSlot(IItemHandler itemHandler, int inventoryIndex, int x, int y, boolean visible, IScreenInfoProvider display, NetworkNodeCrafterManager crafterManager) {
        super(itemHandler, inventoryIndex, x, y);

        this.visible = visible;
        this.display = display;
        this.crafterManager = crafterManager;
    }

    @Override
    public boolean isEnabled() {
        return yPos >= display.getTopHeight() && yPos < display.getTopHeight() + 18 * display.getVisibleRows() && visible && crafterManager.isActive();
    }
}

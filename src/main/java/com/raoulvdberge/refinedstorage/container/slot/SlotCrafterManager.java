package com.raoulvdberge.refinedstorage.container.slot;

import com.raoulvdberge.refinedstorage.apiimpl.network.node.NetworkNodeCrafterManager;
import com.raoulvdberge.refinedstorage.gui.IResizableDisplay;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class SlotCrafterManager extends SlotItemHandler {
    private boolean visible;
    private NetworkNodeCrafterManager crafterManager;
    private IResizableDisplay display;

    public SlotCrafterManager(IItemHandler itemHandler, int index, int xPosition, int yPosition, boolean visible, IResizableDisplay display, NetworkNodeCrafterManager crafterManager) {
        super(itemHandler, index, xPosition, yPosition);

        this.visible = visible;
        this.display = display;
        this.crafterManager = crafterManager;
    }

    @Override
    public boolean isEnabled() {
        return yPos >= display.getTopHeight() && yPos < display.getTopHeight() + 18 * display.getVisibleRows() && visible && crafterManager.isActive();
    }
}

package com.raoulvdberge.refinedstorage.container.factory;

import com.raoulvdberge.refinedstorage.container.CrafterManagerContainer;
import com.raoulvdberge.refinedstorage.screen.EmptyScreenInfoProvider;
import com.raoulvdberge.refinedstorage.tile.CrafterManagerTile;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import javax.annotation.Nullable;

public class CrafterManagerContainerProvider implements INamedContainerProvider {
    private final CrafterManagerTile tile;

    public CrafterManagerContainerProvider(CrafterManagerTile tile) {
        this.tile = tile;
    }

    @Override
    public ITextComponent getDisplayName() {
        return new TranslationTextComponent("gui.refinedstorage.crafter_manager");
    }

    @Nullable
    @Override
    public Container createMenu(int windowId, PlayerInventory playerInventory, PlayerEntity playerEntity) {
        CrafterManagerContainer container = new CrafterManagerContainer(tile, playerEntity, windowId);

        container.setScreenInfoProvider(new EmptyScreenInfoProvider());
        container.initSlotsServer();

        return container;
    }
}

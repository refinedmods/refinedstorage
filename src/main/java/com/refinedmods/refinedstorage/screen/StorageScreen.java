package com.refinedmods.refinedstorage.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.api.storage.AccessType;
import com.refinedmods.refinedstorage.apiimpl.API;
import com.refinedmods.refinedstorage.screen.widget.sidebutton.*;
import com.refinedmods.refinedstorage.tile.data.TileDataParameter;
import com.refinedmods.refinedstorage.util.RenderUtils;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

import javax.annotation.Nullable;
import java.util.function.Supplier;

public class StorageScreen<T extends Container> extends BaseScreen<T> {
    private static final int BAR_X = 8;
    private static final int BAR_Y = 54;
    private static final int BAR_WIDTH = 16;
    private static final int BAR_HEIGHT = 70;

    private final String texture;
    private final TileDataParameter<Integer, ?> typeParameter;
    private final TileDataParameter<Integer, ?> redstoneModeParameter;
    private final TileDataParameter<Integer, ?> exactModeParameter;
    private final TileDataParameter<Integer, ?> whitelistBlacklistParameter;
    private final TileDataParameter<Integer, ?> priorityParameter;
    private final TileDataParameter<AccessType, ?> accessTypeParameter;
    private final Supplier<Long> storedSupplier;
    private final Supplier<Long> capacitySupplier;

    public StorageScreen(T container,
                         PlayerInventory inventory,
                         ITextComponent title,
                         String texture,
                         @Nullable TileDataParameter<Integer, ?> typeParameter,
                         @Nullable TileDataParameter<Integer, ?> redstoneModeParameter,
                         @Nullable TileDataParameter<Integer, ?> exactModeParameter,
                         @Nullable TileDataParameter<Integer, ?> whitelistBlacklistParameter,
                         TileDataParameter<Integer, ?> priorityParameter,
                         @Nullable TileDataParameter<AccessType, ?> accessTypeParameter,
                         Supplier<Long> storedSupplier, Supplier<Long> capacitySupplier) {
        super(container, 176, 223, inventory, title);

        this.texture = texture;
        this.typeParameter = typeParameter;
        this.redstoneModeParameter = redstoneModeParameter;
        this.exactModeParameter = exactModeParameter;
        this.whitelistBlacklistParameter = whitelistBlacklistParameter;
        this.priorityParameter = priorityParameter;
        this.accessTypeParameter = accessTypeParameter;
        this.storedSupplier = storedSupplier;
        this.capacitySupplier = capacitySupplier;
    }

    @Override
    public void onPostInit(int x, int y) {
        if (redstoneModeParameter != null) {
            addSideButton(new RedstoneModeSideButton(this, redstoneModeParameter));
        }

        if (typeParameter != null) {
            addSideButton(new TypeSideButton(this, typeParameter));
        }

        if (whitelistBlacklistParameter != null) {
            addSideButton(new WhitelistBlacklistSideButton(this, whitelistBlacklistParameter));
        }

        if (exactModeParameter != null) {
            addSideButton(new ExactModeSideButton(this, exactModeParameter));
        }

        if (accessTypeParameter != null) {
            addSideButton(new AccessTypeSideButton(this, accessTypeParameter));
        }

        int buttonWidth = 10 + font.getStringWidth(new TranslationTextComponent("misc.refinedstorage.priority").getString());

        addButton(x + 169 - buttonWidth, y + 41, buttonWidth, 20, new TranslationTextComponent("misc.refinedstorage.priority"), true, true, btn -> minecraft.displayGuiScreen(new PriorityScreen(this, priorityParameter, playerInventory)));
    }

    @Override
    public void tick(int x, int y) {
    }

    @Override
    public void renderBackground(MatrixStack matrixStack, int x, int y, int mouseX, int mouseY) {
        bindTexture(RS.ID, texture);

        blit(matrixStack, x, y, 0, 0, xSize, ySize);

        int barHeightNew = (int) ((float) storedSupplier.get() / (float) capacitySupplier.get() * (float) BAR_HEIGHT);

        blit(matrixStack, x + BAR_X, y + BAR_Y + BAR_HEIGHT - barHeightNew, 179, BAR_HEIGHT - barHeightNew, BAR_WIDTH, barHeightNew);
    }

    @Override
    public void renderForeground(MatrixStack matrixStack, int mouseX, int mouseY) {
        renderString(matrixStack, 7, 7, title.getString());
        renderString(matrixStack, 7, 42, capacitySupplier.get() == -1 ?
            new TranslationTextComponent("misc.refinedstorage.storage.stored_minimal", API.instance().getQuantityFormatter().formatWithUnits(storedSupplier.get())).getString() :
            new TranslationTextComponent("misc.refinedstorage.storage.stored_capacity_minimal", API.instance().getQuantityFormatter().formatWithUnits(storedSupplier.get()), API.instance().getQuantityFormatter().formatWithUnits(capacitySupplier.get())).getString()
        );

        renderString(matrixStack, 7, 129, new TranslationTextComponent("container.inventory").getString());

        if (RenderUtils.inBounds(BAR_X, BAR_Y, BAR_WIDTH, BAR_HEIGHT, mouseX, mouseY)) {
            int full = 0;

            if (capacitySupplier.get() >= 0) {
                full = (int) ((float) storedSupplier.get() / (float) capacitySupplier.get() * 100f);
            }

            renderTooltip(matrixStack, mouseX, mouseY, (capacitySupplier.get() == -1 ?
                new TranslationTextComponent("misc.refinedstorage.storage.stored_minimal", API.instance().getQuantityFormatter().format(storedSupplier.get())).getString() :
                new TranslationTextComponent("misc.refinedstorage.storage.stored_capacity_minimal", API.instance().getQuantityFormatter().format(storedSupplier.get()), API.instance().getQuantityFormatter().format(capacitySupplier.get())).getString()
            ) + "\n" + TextFormatting.GRAY + new TranslationTextComponent("misc.refinedstorage.storage.full", full).getString());
        }
    }
}

package com.refinedmods.refinedstorage.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.apiimpl.API;
import com.refinedmods.refinedstorage.screen.widget.sidebutton.*;
import com.refinedmods.refinedstorage.util.RenderUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;

import java.util.function.Supplier;

public class StorageScreen<T extends AbstractContainerMenu> extends BaseScreen<T> {
    private static final int BAR_X = 8;
    private static final int BAR_Y = 54;
    private static final int BAR_WIDTH = 16;
    private static final int BAR_HEIGHT = 70;

    private final String texture;
    private final StorageScreenSynchronizationParameters parameters;
    private final Supplier<Long> storedSupplier;
    private final Supplier<Long> capacitySupplier;

    public StorageScreen(T containerMenu,
                         Inventory inventory,
                         Component title,
                         String texture,
                         StorageScreenSynchronizationParameters parameters,
                         Supplier<Long> storedSupplier,
                         Supplier<Long> capacitySupplier) {
        super(containerMenu, 176, 223, inventory, title);

        this.texture = texture;
        this.parameters = parameters;
        this.storedSupplier = storedSupplier;
        this.capacitySupplier = capacitySupplier;
    }

    @Override
    public void onPostInit(int x, int y) {
        if (parameters.getRedstoneModeParameter() != null) {
            addSideButton(new RedstoneModeSideButton(this, parameters.getRedstoneModeParameter()));
        }

        if (parameters.getTypeParameter() != null) {
            addSideButton(new TypeSideButton(this, parameters.getTypeParameter()));
        }

        if (parameters.getWhitelistBlacklistParameter() != null) {
            addSideButton(new WhitelistBlacklistSideButton(this, parameters.getWhitelistBlacklistParameter()));
        }

        if (parameters.getExactModeParameter() != null) {
            addSideButton(new ExactModeSideButton(this, parameters.getExactModeParameter()));
        }

        if (parameters.getAccessTypeParameter() != null) {
            addSideButton(new AccessTypeSideButton(this, parameters.getAccessTypeParameter()));
        }

        int buttonWidth = 10 + font.width(I18n.get("misc.refinedstorage.priority"));

        addButton(
            x + 169 - buttonWidth,
            y + 41, buttonWidth,
            20,
            new TranslatableComponent("misc.refinedstorage.priority"),
            true,
            true,
            btn -> minecraft.setScreen(new PriorityScreen(this, parameters.getPriorityParameter(), inventory))
        );
    }

    @Override
    public void tick(int x, int y) {
        // NO OP
    }

    @Override
    public void renderBackground(PoseStack matrixStack, int x, int y, int mouseX, int mouseY) {
        bindTexture(RS.ID, texture);

        blit(matrixStack, x, y, 0, 0, imageWidth, imageHeight);

        int barHeightNew = capacitySupplier.get() < 0 ? 0 : (int) ((float) storedSupplier.get() / (float) capacitySupplier.get() * (float) BAR_HEIGHT);

        blit(matrixStack, x + BAR_X, y + BAR_Y + BAR_HEIGHT - barHeightNew, 179, BAR_HEIGHT - barHeightNew, BAR_WIDTH, barHeightNew);
    }

    @Override
    public void renderForeground(PoseStack matrixStack, int mouseX, int mouseY) {
        renderString(matrixStack, 7, 7, title.getString());
        renderString(matrixStack, 7, 42, capacitySupplier.get() == -1 ?
            I18n.get("misc.refinedstorage.storage.stored_minimal", API.instance().getQuantityFormatter().formatWithUnits(storedSupplier.get())) :
            I18n.get("misc.refinedstorage.storage.stored_capacity_minimal", API.instance().getQuantityFormatter().formatWithUnits(storedSupplier.get()), API.instance().getQuantityFormatter().formatWithUnits(capacitySupplier.get()))
        );

        renderString(matrixStack, 7, 129, I18n.get("container.inventory"));

        if (RenderUtils.inBounds(BAR_X, BAR_Y, BAR_WIDTH, BAR_HEIGHT, mouseX, mouseY)) {
            int full = 0;

            if (capacitySupplier.get() >= 0) {
                full = (int) ((float) storedSupplier.get() / (float) capacitySupplier.get() * 100f);
            }

            renderTooltip(matrixStack, mouseX, mouseY, (capacitySupplier.get() == -1 ?
                I18n.get("misc.refinedstorage.storage.stored_minimal", API.instance().getQuantityFormatter().format(storedSupplier.get())) :
                I18n.get("misc.refinedstorage.storage.stored_capacity_minimal", API.instance().getQuantityFormatter().format(storedSupplier.get()), API.instance().getQuantityFormatter().format(capacitySupplier.get()))
            ) + "\n" + ChatFormatting.GRAY + I18n.get("misc.refinedstorage.storage.full", full));
        }
    }
}
